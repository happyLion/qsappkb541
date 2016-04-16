package com.app.demos.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;
import android.R.string;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.myqsmy.app.R;
import com.app.demos.base.BaseHandler;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseTask;
import com.app.demos.base.BaseUi;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.C;
import com.app.demos.list.BlogList;
import com.app.demos.model.Blog;
import com.app.demos.mycam.CameraActivity;
import com.app.demos.util.AppClient;
import com.app.demos.util.AppUtil;
import com.app.demos.util.HttpUtil;
import com.handmark.pulltorefresh.library.IPullToRefresh;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class NewUiIndex extends BaseUiAuth implements OnScrollListener {
	private PullToRefreshListView blogListView;
	private ListView listView;
	private BlogList blogListAdapter;
	private int pageId=1;
	private ArrayList<Blog> blogListLess = new ArrayList<Blog>();
	private  ImageButton ib;
	private int mStart;
	private int mEnd;
	private LinearLayout layout;
	private ImageButton imageButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_index); 
		initViews();
		initEvents();
		//第一次请求数据（每次onCreate的时候）
		HashMap<String, String> blogParams = new HashMap<String, String>();
		blogParams.put("typeId", "1");
		blogParams.put("pageId", "1");
		blogParams.put("pageNum","50");
		this.doTaskAsync(C.task.blogList, C.api.blogList, blogParams);
		
	
}
	

	private void initEvents() {
		this.setHandler(new IndexHandler(this));
	    ib.setImageResource(R.drawable.yanjingsmall);
		listView.setOnScrollListener(this);
		layout.setBackgroundColor(Color.parseColor("#3c3f41"));
		imageButton.setBackgroundColor(Color.parseColor("#3c3f41"));
	}

	private void initViews() {
        ib = (ImageButton) this.findViewById(R.id.main_tab_1);
        blogListView =(PullToRefreshListView) findViewById(R.id.app_index_list_view);
        listView=blogListView.getRefreshableView();
        layout=(LinearLayout) findViewById(R.id.main_tab_layout1);
        imageButton=(ImageButton) findViewById(R.id.main_tab_1);
	}
	
//	@Override
//	public void onStart(){
//		super.onStart();
//		
//		// 启动一次异步获取bloglist，异步请求完交给handler实例进行主线程处理（大部分由handler的handleMessage方法中的onTaskComplete（）这一步处理）
//		HashMap<String, String> blogParams = new HashMap<String, String>();
//		blogParams.put("typeId", "1");
//		blogParams.put("pageId", "1");
//		blogParams.put("pageNum","50");
//		this.doTaskAsync(C.task.blogList, C.api.blogList, blogParams);
//	}
//	
//	@Override 
//	protected void onPause() 
//	{ 
//		super.onPause();
//	    // Save scroll position 
//	    SharedPreferences preferences =getSharedPreferences("SCROLL", 0); 
//	    SharedPreferences.Editor editor = preferences.edit(); 
//	    int scroll = blogListView.getScrollY(); 
//	    editor.putInt("ScrollValue", scroll); 
//	    editor.commit(); 
//	} 
	 
//	@Override 
//	protected void onResume() 
//	{ 
//		super.onResume();
////	    // Get the scroll position 
////	    SharedPreferences preferences =getSharedPreferences("SCROLL", 0); 
////	    int scroll = preferences.getInt("ScrollValue", 0); 
////	    blogListView.scrollTo(0, scroll); 
//		
//	} 

	
	//内部类IndexHandle 继承了BaseHandler
	private class IndexHandler extends BaseHandler {
		public IndexHandler(BaseUi ui) {
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
	
	//handler的handleMessage方法中的onTaskComplete（）方法
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		switch (taskId) {
			case C.task.blogList:
				try {
					@SuppressWarnings("unchecked")
					 ArrayList<Blog> blogList = (ArrayList<Blog>) message.getResultList("Blog");
					/**
					 * 得到每一次异动的第一张图片，由blogListLes存储
					 */
					blogList.get(0).setNumber(1);
					blogList.get(0).setColorNumber(0);
					blogListLess.add(blogList.get(0));
					int colorNumber=0;
					int number=1;
					for(int i=1;i<blogList.size();i++){
						SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date   begin=df.parse(blogList.get(i-1).getUptime()); 
						Date   end=df.parse(blogList.get(i).getUptime()); 
						long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒      
					    int    second=(int) -between; 
					    SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
					    int sensitiveNum=preferences.getInt("sensitiveNum", 30);
					    
				      if(second>(sensitiveNum+20)){
					    number+=1;
					    if(begin.getDate()!=end.getDate()){
					    	colorNumber+=1;
					     	 number=1;
					    	 blogList.get(i).setColorNumber(colorNumber);	
					         }else {
					        	 blogList.get(i).setColorNumber(colorNumber);
						       }
					    blogList.get(i).setNumber(number);
					    blogListLess.add(blogList.get(i));
					    }
					}
					//生成blogListAdapter
					blogListAdapter = new BlogList(this, blogListLess);	
					listView.setOnItemClickListener(new OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
							Bundle params = new Bundle();
							params.putString("blogId", blogListLess.get(pos-1).getId());
							overlay(UiBlog.class, params);
						}
					});				
					blogListView.setMode(Mode.PULL_UP_TO_REFRESH);
					listView.setSelection(mStart);
					listView.setAdapter(blogListAdapter);
					// 设置下拉监听事件  
					blogListView .setOnRefreshListener(new OnRefreshListener<ListView>()  
			                {  
			                    @Override  
			                    public void onRefresh(  
			                            PullToRefreshBase<ListView> refreshView)  
			                    {  
			                        String label = DateUtils.formatDateTime(  
			                                getApplicationContext(),  
			                                System.currentTimeMillis(),  
			                                DateUtils.FORMAT_SHOW_TIME  
			                                        | DateUtils.FORMAT_SHOW_DATE  
			                                        | DateUtils.FORMAT_ABBREV_ALL);  
			                        // 显示最后更新的时间  
			                        refreshView.getLoadingLayoutProxy()  
			                                .setLastUpdatedLabel(label);  
			                     // 模拟加载任务  
			                        new GetDataTask().execute();  
			                    }

//								@Override
//								public void onPullDownToRefresh(
//										PullToRefreshBase<ListView> refreshView) {
//									HashMap<String, String> blogParams = new HashMap<String, String>();
//									blogParams.put("typeId", "1");
//									blogParams.put("pageId", "1");
//									blogParams.put("pageNum","50");
//									NewUiIndex.this.doTaskAsync(C.task.blogList, C.api.blogList, blogParams);
//									
//								}
//
//								@Override
//								public void onPullUpToRefresh(
//										PullToRefreshBase<ListView> refreshView) {
//									String label = DateUtils.formatDateTime(  
//			                                getApplicationContext(),  
//			                                System.currentTimeMillis(),  
//			                                DateUtils.FORMAT_SHOW_TIME  
//			                                        | DateUtils.FORMAT_SHOW_DATE  
//			                                        | DateUtils.FORMAT_ABBREV_ALL);  
//			                        // 显示最后更新的时间  
//			                        refreshView.getLoadingLayoutProxy()  
//			                                .setLastUpdatedLabel(label);  
//			                     // 模拟加载任务  
//			                        new GetDataTask().execute();  
//									
//								}  
			                });  
					/**
					 * 测试完成
					 */

					} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
					Log.e("error", "oncreate11");
				}
		break;
		
			case C.task.blogListAgain:
				try {
					@SuppressWarnings("unchecked")
					 ArrayList<Blog> blogList = (ArrayList<Blog>) message.getResultList("Blog");
					/**
					 * 得到每一次异动的第一张图片，由blogListLes存储
					 */
					blogList.get(0).setNumber(1);
					blogList.get(0).setColorNumber(0);
					blogListLess.clear();
					blogListLess.add(blogList.get(0));
					int colorNumber=0;
					int number=1;
					for(int i=1;i<blogList.size();i++){
						SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date   begin=df.parse(blogList.get(i-1).getUptime()); 
						Date   end=df.parse(blogList.get(i).getUptime()); 
						long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒      
					    int    second=(int) -between; 
					    SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
					    int sensitiveNum=preferences.getInt("sensitiveNum", 30);
					    
				      if(second>(sensitiveNum+20)){
					    number+=1;
					    if(begin.getDate()!=end.getDate()){
					    	colorNumber+=1;
					     	 number=1;
					    	 blogList.get(i).setColorNumber(colorNumber);	
					         }else {
					        	 blogList.get(i).setColorNumber(colorNumber);
						       }
					    blogList.get(i).setNumber(number);
					    blogListLess.add(blogList.get(i));
					    }
					  }
					 blogListAdapter.notifyDataSetChanged(); 
					
					
				}catch (Exception e) {
				e.printStackTrace();
					}
			break;
		
	}
}
	/**
	 * GetDataTask 内部类
	 */
    private class GetDataTask extends AsyncTask<Void, Void, BaseMessage>  
    {  
  
        @Override  
        protected BaseMessage doInBackground(Void... params)  
        {  
        	/**
        	 * 下拉菜单时进行一次异步传输得到下一页的bloglist数据
        	 */
        	pageId=pageId+1;
        	HashMap<String, String> blogParams = new HashMap<String, String>();
    		blogParams.put("typeId", "1");
    		blogParams.put("pageId", pageId+"");
    		blogParams.put("pageNum","50");
    		BaseMessage message =AsyncAskForBloglist(blogParams);
    		return message;
    		
        }  
  
        private  BaseMessage AsyncAskForBloglist(HashMap<String, String> blogParams) {
        	
			try {
				String httpResult = null;
	        	AppClient client = new AppClient(C.api.blogList);
				if (HttpUtil.WAP_INT == HttpUtil.getNetType(getContext())) {
					client.useWap();
				}
				httpResult = client.post(blogParams);
				BaseMessage message=AppUtil.getMessage(httpResult);
				return message;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
			
		}

		@Override  
        protected void onPostExecute(BaseMessage message)  
        { 
			 
			@SuppressWarnings("unchecked")
			ArrayList<Blog> blogList;
			try {
				blogList = (ArrayList<Blog>) message.getResultList("Blog");
				/**
				 * 依次得到刷新后list的每一次异动的第一张图片，由blogListLess增加存储
				 */
//				blogListLess.add(blogList.get(0));
//				for(int i=1;i<blogList.size();i++){
//					SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					Date   begin=df.parse(blogList.get(i-1).getUptime()); 
//					Date   end=df.parse(blogList.get(i).getUptime()); 
//					long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒      
//				    int    second=(int) -between; 
//				    if(second>30){ 
//				    	blogListLess.add(blogList.get(i));
//				  
//				    }
//				}
				int second;
				Date last;
				Date first;
				int colorNumber;
				int number;
				int i=0;
				SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				do {
					   last=df.parse(blogListLess.get(blogListLess.size()-1).getUptime()); 
					   first=df.parse(blogList.get(i).getUptime()); 
					long   between=(first.getTime()-last.getTime())/1000;//除以1000是为了转换成秒      
				    second=(int) -between; 
				    i=i+1;
				} while (second<30);
				colorNumber=blogListLess.get(blogListLess.size()-1).getColorNumber();
				number=blogListLess.get(blogListLess.size()-1).getNumber();
				if(last.getDate()!=first.getDate()){
					colorNumber=colorNumber+1;
				     	 number=1;
				    	 blogList.get(i-1).setColorNumber(colorNumber);
				    	 blogList.get(i-1).setNumber(number);
				         }else {
				        	 blogList.get(i-1).setColorNumber(colorNumber);
				        	 number+=1;
				        	 blogList.get(i-1).setNumber(number);
					       }
				   
				blogListLess.add(blogList.get(i-1));
				    
				
				for(int j=i;j<blogList.size()-i;j++){
					Date   begin1=df.parse(blogList.get(j-1).getUptime()); 
					Date   end=df.parse(blogList.get(j).getUptime()); 
					long   between=(end.getTime()-begin1.getTime())/1000;//除以1000是为了转换成秒      
				    second=(int) -between; 
			      if(second>30){
				    number+=1;
				    if(begin1.getDate()!=end.getDate()){
				    	colorNumber+=1;
				     	 number=1;
				    	 blogList.get(j).setColorNumber(colorNumber);	
				         }else {
				        	 blogList.get(j).setColorNumber(colorNumber);
					       }
				    blogList.get(j).setNumber(number);
				    blogListLess.add(blogList.get(j));
				    }
				}

		        	blogListAdapter.notifyDataSetChanged(); 
		        	Log.e("onPostExecute", "刷新了结果"+blogListLess);
		        	blogListView.onRefreshComplete(); 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
        }  
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
    //重写由CameraActivity返回到该界面后的onActivityResult方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (requestCode) {
		case 1:
			HashMap<String, String> blogParams = new HashMap<String, String>();
			blogParams.put("typeId", "1");
			blogParams.put("pageId", "1");
			blogParams.put("pageNum","50");
			this.doTaskAsync(C.task.blogListAgain, C.api.blogList, blogParams);
			
			break;
		}
 	
    	
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
	
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}


	//////////////////////////////////////////////////////////////////////////////////////////////
	//实现OnScrollListener的方法
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			mStart = listView.getFirstVisiblePosition();
            }
		}
		
	


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	
	

	
	
	
	
	
	
	
}