package com.app.demos.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.myqsmy.app.R;
import com.app.demos.base.BaseTaskPool;
import com.app.demos.base.BaseUi;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.BaseHandler;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseTask;
import com.app.demos.base.C;
import com.app.demos.list.BlogList;
import com.app.demos.list.ExpandList;
import com.app.demos.model.Blog;
import com.app.demos.model.Comment;
import com.app.demos.model.Customer;
import com.app.demos.util.AppCache;
import com.app.demos.util.AppUtil;
import com.app.demos.util.UIUtil;

import android.R.string;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UiBlog extends BaseUiAuth {
	
	private String blogId = null;
	private String customerId = null;
	private Button addfansBtn = null;
	private Button commentBtn = null;
	private ImageView faceImage = null;
	private String faceImageUrl = null;
	private ImageView textPicture = null;
	private String picUrl;//异步得到的储存在数据库中的博客图片的url
	private String choosedBlogUpTime;
	private BlogList blogListAdapter;
	private ListView blogListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_blog);
		// set handler
		this.setHandler(new BlogHandler(this));
		// get params
		Bundle params = this.getIntent().getExtras();
		blogId = params.getString("blogId");
		blogListView=(ListView) findViewById(R.id.app_index_list_view);
		
		
		// do add fans
//		try {
//			addfansBtn = (Button) this.findViewById(R.id.app_blog_btn_addfans);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Log.e("errors",Log.getStackTraceString(e));
//
//		}
//		Log.e("errorhaha", "8");
//		addfansBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// prepare blog data
//				HashMap<String, String> urlParams = new HashMap<String, String>();
//				urlParams.put("customerId", customerId);
//				doTaskAsync(C.task.fansAdd, C.api.fansAdd, urlParams);
//			}
//		});
		
//		// do add comment
//		commentBtn = (Button) this.findViewById(R.id.app_blog_btn_comment);
//		commentBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Bundle data = new Bundle();
//				data.putInt("action", C.action.edittext.COMMENT);
//				data.putString("blogId", blogId);
//				doEditText(data);
//			}
//		});
		//放大图片 
//		textPicture = (ImageView) this.findViewById(R.id.app_blog_text_picture);
//		Log.e("errorhaha", "13");
//		textPicture.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.e("errorhaha", "15");
//				Bundle data = new Bundle();
//				data.putString("picUrl", picUrl);
//				enLargePic(data);
//			}
//		});
		
		// prepare choosed blog's uptime
		HashMap<String, String> blogParams = new HashMap<String, String>();
		blogParams.put("blogId", blogId);
		BaseTask task_blogView =new BaseTask(){
			@Override
			public void onComplete (String httpResult) {
				sendMessage(BaseTask.TASK_COMPLETE, this.getId(), httpResult);
			}
			@Override
			public void onError (String error) {
				sendMessage(BaseTask.NETWORK_ERROR, this.getId(), null);
			}
		};
		task_blogView.setId(C.task.blogView);
		Thread ThreadOfBlogView=new Thread(new BaseTaskPool.TaskThread(this, C.api.blogView, blogParams,null,task_blogView ,0));
		ThreadOfBlogView.start();
		//先做完choosed blog's uptime 这个异步请求后，再在主线程往下执行
		try {
			ThreadOfBlogView.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 异步加载该用户的bloglist
				HashMap<String, String> blogParams1 = new HashMap<String, String>();
				blogParams1.put("typeId", "1");
				blogParams1.put("pageId","0");
				blogParams1.put("pageNum",200+"");
				this.doTaskAsync(C.task.blogList, C.api.blogList, blogParams1);
	}
	
	@Override
	public void onStart () {
		 Log.e("errorhaha", "2");
		super.onStart();
		
		// prepare comment data
		HashMap<String, String> commentParams = new HashMap<String, String>();
		commentParams.put("blogId", blogId);
		commentParams.put("pageId", "0");
		this.doTaskAsync(C.task.commentList, C.api.commentList, commentParams);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// async task callback methods
	
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		 Log.e("errorhaha", "3");
		super.onTaskComplete(taskId, message);
		
		switch (taskId) {
			case C.task.blogView:
				try {
					// get blog content
					Blog blog = (Blog) message.getResult("Blog");
					choosedBlogUpTime=blog.getUptime();
//					TextView textUptime = (TextView) this.findViewById(R.id.app_blog_text_uptime);
//					TextView textContent = (TextView) this.findViewById(R.id.app_blog_text_content);
//					textUptime.setText(blog.getUptime());
//					textContent.setText(blog.getContent());
//					// get blog picture
//					textPicture = (ImageView) this.findViewById(R.id.app_blog_text_picture);
//					picUrl = blog.getPicture();
//					if (picUrl != null && picUrl.length() > 0) {
//						Bitmap picImage = AppCache.getImage( picUrl);
//						if (picImage != null) {
//							textPicture.setImageBitmap(picImage);
//							textPicture.setVisibility(View.VISIBLE);
//						}
//					}
					// get customer info
//					Customer customer = (Customer) message.getResult("Customer");
//					TextView textCustomerName = (TextView) this.findViewById(R.id.app_blog_text_customer_name);
//					TextView testCustomerInfo = (TextView) this.findViewById(R.id.app_blog_text_customer_info);
//					textCustomerName.setText(customer.getName());
//					testCustomerInfo.setText(UIUtil.getCustomerInfo(this, customer));
//					// set customer id
//					customerId = customer.getId();
//					// load face image async
//					faceImage = (ImageView) this.findViewById(R.id.app_blog_image_face);
//					faceImageUrl = customer.getFaceurl();
//					loadImage(faceImageUrl);
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
				break;
//			case C.task.commentList:
//				try {
//					@SuppressWarnings("unchecked")
//					ArrayList<Comment> commentList = (ArrayList<Comment>) message.getResultList("Comment");
//					String[] cols = {
//						Comment.COL_CONTENT,
//						Comment.COL_UPTIME
//					};
//					int[] views = {
//						R.id.tpl_list_comment_content,
//						R.id.tpl_list_comment_uptime,
//					};
//					int[] types = {
//						ExpandList.TEXT_VIEW,
//						ExpandList.TEXT_VIEW
//					};
//					ExpandList el = new ExpandList(this, AppUtil.dataToList(commentList, cols), R.layout.tpl_list_comment, cols, views, types);
//					LinearLayout layout = (LinearLayout) this.findViewById(R.id.app_blog_list_comment);
//					layout.removeAllViews(); // clean first
//					el.render(layout);
//				} catch (Exception e) {
//					e.printStackTrace();
//					toast(e.getMessage());
//				}
//				break;
//			case C.task.fansAdd:
//				if (message.getCode().equals("10000")) {
//					toast("Add fans ok");
//					// refresh customer data
//					HashMap<String, String> cvParams = new HashMap<String, String>();
//					cvParams.put("customerId", customerId);
//					this.doTaskAsync(C.task.customerView, C.api.customerView, cvParams);
//				} else {
//					toast("Add fans fail");
//				}
//				break;
//			case C.task.customerView:
//				try {
//					// update customer info
//					final Customer customer = (Customer) message.getResult("Customer");
//					TextView textInfo = (TextView) this.findViewById(R.id.app_blog_text_customer_info);
//					textInfo.setText(UIUtil.getCustomerInfo(this, customer));
//				} catch (Exception e) {
//					e.printStackTrace();
//					toast(e.getMessage());
//				}
//				break;
			case C.task.blogList:
				try {
					ArrayList<Blog> blogList = (ArrayList<Blog>) message.getResultList("Blog");
					String[] cols = {
						Blog.COL_CONTENT,
						Blog.COL_UPTIME,
						Blog.COL_COMMENT,
						Blog.COL_PICTURE
					};
					int[] views = {
						R.id.tpl_list_blog_text_content,
						R.id.tpl_list_blog_text_uptime,
						R.id.tpl_list_blog_text_comment,
						R.id.tpl_list_blog_text_picture
					};
					int[] types = {
						ExpandList.TEXT_VIEW,
						ExpandList.TEXT_VIEW,
						ExpandList.TEXT_VIEW,
						ExpandList.IMAGE_VIEW
					};
					ArrayList<Blog> blogListLess = null ; 
					SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				
					Date   choosed=df.parse(choosedBlogUpTime);
					for(int i=0;i<blogList.size();i++){
						Date   begin=df.parse(blogList.get(i).getUptime()); 
						if (begin.getTime()==choosed.getTime()){
						blogList.subList(0,i).clear();
						}
					}
					for(int i=1;i<blogList.size();i++){
						Date   begin=df.parse(blogList.get(i-1).getUptime()); 
						Date   end=df.parse(blogList.get(i).getUptime()); 
						long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒      
					    int    second=(int) -between; 
					    SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
					    int sensitiveNum=preferences.getInt("sensitiveNum", 30);
					    if(second>(sensitiveNum+20)){ 
					    	blogList.subList(i,blogList.size()-1).clear();
					    	blogList.remove(blogList.size()-1);
					    	break;
					    }
					    
					   
					}
//					ExpandList el = new ExpandList(this, AppUtil.dataToList(blogList, cols), R.layout.tpl_list_blogs, cols, views, types);
//					LinearLayout layout =   (LinearLayout) this.findViewById(R.id.app_blog_list_blog);
//					layout.removeAllViews(); // clean first
//					el.setDivider(R.color.divider3);
//					el.render(layout);
					blogListAdapter = new BlogList(this, blogList);	
					blogListView.setAdapter(blogListAdapter);
					
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
				break;
				
				
		}
	}
	
	@Override
	public void onNetworkError (int taskId) {
		super.onNetworkError(taskId);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// other methods
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			doFinish();
		}
		return super.onKeyDown(keyCode, event);
	
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// inner classes
	
	private class BlogHandler extends BaseHandler {
		public BlogHandler(BaseUi ui) {
			super(ui);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
					case BaseTask.LOAD_IMAGE:
						blogListAdapter.notifyDataSetChanged(); 
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				ui.toast(e.getMessage());
			}
		}
	}
}