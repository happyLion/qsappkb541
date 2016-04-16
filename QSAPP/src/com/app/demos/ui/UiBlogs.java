package com.app.demos.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.myqsmy.app.R;
import com.app.demos.base.BaseHandler;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseTask;
import com.app.demos.base.BaseUi;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.C;
import com.app.demos.list.ExpandList;
import com.app.demos.model.Blog;
import com.app.demos.model.Customer;
import com.app.demos.util.AppCache;
import com.app.demos.util.AppUtil;
import com.app.demos.util.UIUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UiBlogs extends BaseUiAuth {
	
//	private ListView blogListView;
	private ImageView faceImage;
	private String faceImageUrl;
	private LinearLayout layout;
	private ExpandList el;
	private String firstImageUrl;
	private int pageId=1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle params = this.getIntent().getExtras();
		if (params!=null) {
			pageId=params.getInt("pageId");	
		}
		
		setContentView(R.layout.ui_blogs);
		// set handler
		this.setHandler(new BlogsHandler(this));
		
		// tab button
//		ImageButton ib = (ImageButton) this.findViewById(R.id.main_tab_2);
//		ib.setImageResource(R.drawable.tab_heart_2);	
		//NextPageButton
		Button nextPageButton=(Button) findViewById(R.id.ui_blogs_nextPageButton);
		
		nextPageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle=new Bundle();
				pageId=pageId+1;
				bundle.putInt("pageId",pageId);
				overlay(UiBlogs.class,bundle);
			}
		});
		//LastPageButton
		Button LastPageButton=(Button) findViewById(R.id.ui_blogs_lastPageButton);
		if (pageId==1) {
			LastPageButton.setEnabled(false);
		}
		LastPageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle=new Bundle();
				pageId=pageId-1;
				bundle.putInt("pageId",pageId);
				overlay(UiBlogs.class,bundle);
			}
		});
		
	}
	
	@Override
	public void onStart () {
		super.onStart(); 
		
		// prepare customer data
		HashMap<String, String> cvParams = new HashMap<String, String>();
		cvParams.put("customerId", customer.getId());
		this.doTaskAsync(C.task.customerView, C.api.customerView, cvParams);
		
		// prepare blog data
		HashMap<String, String> blogParams = new HashMap<String, String>();
		
		blogParams.put("typeId", "1");
		blogParams.put("pageId", pageId+"");
		this.doTaskAsync(C.task.blogList, C.api.blogList, blogParams);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// async task callback methods
	
	@Override
	@SuppressWarnings("unchecked")
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		
		switch (taskId) {
			case C.task.customerView:
				try {
					final Customer customer = (Customer) message.getResult("Customer");
					TextView textName = (TextView) this.findViewById(R.id.app_blogs_text_customer_name);
					TextView textInfo = (TextView) this.findViewById(R.id.app_blogs_text_customer_info);
					textName.setText(customer.getSign());
					textInfo.setText(UIUtil.getCustomerInfo(this, customer));
					// load face image async
					faceImage = (ImageView) this.findViewById(R.id.app_blogs_image_face);
					faceImageUrl = customer.getFaceurl();
					loadImage(faceImageUrl);
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
				break;
			case C.task.blogList:
				try {
					final ArrayList<Blog> blogList = (ArrayList<Blog>) message.getResultList("Blog");
					for (Blog blog : blogList) {
						loadImage(blog.getFace());					
						loadImage(blog.getPicture());	
					}
					String[] cols = {
						Blog.COL_CONTENT,
						Blog.COL_UPTIME,
						Blog.COL_COMMENT,
						Blog.COL_PICTURE,
						"",
						"",
						""
						
					};
					int[] views = {
						R.id.tpl_list_blog_text_content,
						R.id.tpl_list_blog_text_uptime,
						R.id.tpl_list_blog_text_comment,
						R.id.tpl_list_blog_text_picture,
						R.id.number,
						R.id.circle,
						R.id.background_pine
						
					};
					int[] types = {
						ExpandList.TEXT_VIEW,
						ExpandList.TEXT_VIEW,
						ExpandList.TEXT_VIEW,
						ExpandList.IMAGE_VIEW,
						ExpandList.TEXT_VIEW2,
						ExpandList.IMAGE_VIEW2,
						ExpandList.TEXT_VIEW3
						
					};
					// can not use listview under scrollview
//					blogListView = (ListView) this.findViewById(R.id.app_blogs_list_view);
//					blogListView.setAdapter(new SimpleList(this, AppUtil.dataToList(blogList, from), R.layout.tpl_list_blogs, from, to));
//					blogListView.setOnItemClickListener(new OnItemClickListener(){
//						@Override
//						public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
//							Bundle params = new Bundle();
//							params.putString("blogId", blogList.get(pos).getId());
//							overlay(AppBlog.class, params);
//						}
//					});
					// use expandlist to do this
					final List<Blog> blogListLess = new ArrayList<Blog>();
					blogListLess.add(blogList.get(0));
					for(int i=1;i<blogList.size();i++){
						SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date   begin=df.parse(blogList.get(i-1).getUptime()); 
						Date   end=df.parse(blogList.get(i).getUptime()); 
						long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒      
					    int    second=(int) -between; 
					    if(second>30){ 
					    	blogListLess.add(blogList.get(i));
					  
					    }
					}
						
					el = new ExpandList(this, AppUtil.dataToList(blogListLess, cols), R.layout.tpl_list_blogs, cols, views, types);
					layout = (LinearLayout) this.findViewById(R.id.app_blogs_list_view);
					layout.removeAllViews(); // clean first
					el.setDivider(R.color.divider3);
					el.setOnItemClickListener(new ExpandList.OnItemClickListener() {
						@Override
						public void onItemClick(View view, int pos) {
							Bundle params = new Bundle();
							params.putString("blogId", blogListLess.get(pos).getId());
							params.putInt("pageId", pageId);
							overlay(UiBlog.class, params);
						}
					});
					el.render(layout);
					 ImageView imageView =(ImageView) layout.getChildAt(0).findViewById(R.id.tpl_list_blog_text_picture);
					if( imageView.getDrawable()==null){
					firstImageUrl=blogListLess.get(0).getPicture();
					loadImage2(firstImageUrl);	
					}	
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
			this.forward(NewUiIndex.class);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// inner classes
	
	private class BlogsHandler extends BaseHandler {
		public BlogsHandler(BaseUi ui) {
			super(ui);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
					case BaseTask.LOAD_IMAGE:
						Bitmap face = AppCache.getImage(faceImageUrl);
						faceImage.setImageBitmap(face);
						break;
					case BaseTask.LOAD_IMAGE2:
					   Bitmap bitmap= AppCache.getImage(firstImageUrl);
					    ImageView imageView =(ImageView) layout.getChildAt(0).findViewById(R.id.tpl_list_blog_text_picture);
					    imageView.setImageBitmap(bitmap);
						break;
						
				}
			} catch (Exception e) {
				e.printStackTrace();
				ui.toast(e.getMessage());
			}
		}
	}
}