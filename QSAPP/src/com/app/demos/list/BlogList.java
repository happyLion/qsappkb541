package com.app.demos.list;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.myqsmy.app.R;
import com.app.demos.base.BaseTask;
import com.app.demos.base.BaseTaskPool;
import com.app.demos.base.BaseUi;
import com.app.demos.base.BaseList;
import com.app.demos.model.Blog;
import com.app.demos.util.AppCache;
import com.app.demos.util.AppFilter;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;

public class BlogList extends BaseList {

	private BaseUi ui;
	private LayoutInflater inflater;
	private ArrayList<Blog> blogList;
	
	public final class BlogListItem {
		public ImageView face;
		public TextView content;
		public TextView uptime;
		public TextView comment;
		public ImageView picture;
		public TextView number;
		public TextView colorBackground;
		public ImageView circle;
		public TextView date;
		public TextView time;
		public ImageView smallCircleLeft;
		public ImageView smallCircleRight;
	}
	
	public BlogList (BaseUi ui, ArrayList<Blog> blogList) {
		this.ui = ui;
		this.inflater = LayoutInflater.from(this.ui);
		this.blogList = blogList;
	}
	
	@Override
	public int getCount() {
		return blogList.size();
	}

	@Override
	public Object getItem(int position) {
		return blogList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int p, View v, ViewGroup parent) {
		// init tpl
		BlogListItem  blogItem = null;
		// if cached expired
		if (v == null) {
			v = inflater.inflate(R.layout.tpl_list_blogs, null);
			blogItem = new BlogListItem();
//			blogItem.face = (ImageView) v.findViewById(R.id.tpl_list_blog_image_face);
//			blogItem.content = (TextView) v.findViewById(R.id.tpl_list_blog_text_content);
//			blogItem.uptime = (TextView) v.findViewById(R.id.tpl_list_blog_text_uptime);
//			blogItem.comment = (TextView) v.findViewById(R.id.tpl_list_blog_text_comment);
//			blogItem.picture = (ImageView) v.findViewById(R.id.tpl_list_blog_text_picture);
			blogItem.picture = (ImageView) v.findViewById(R.id.tpl_list_blog_text_picture);
			blogItem.content = (TextView) v.findViewById(R.id.tpl_list_blog_text_content);
			blogItem.uptime = (TextView) v.findViewById(R.id.tpl_list_blog_text_uptime);
			blogItem.comment = (TextView) v.findViewById(R.id.tpl_list_blog_text_comment);
			blogItem.circle = (ImageView) v.findViewById(R.id.circle);
			blogItem.number = (TextView) v.findViewById(R.id.number);
			blogItem.colorBackground = (TextView) v.findViewById(R.id.background_pine);
			blogItem.date=(TextView) v.findViewById(R.id.circle_left_date);
			blogItem.time=(TextView) v.findViewById(R.id.circle_right_time);
			blogItem.smallCircleLeft=(ImageView) v.findViewById(R.id.circle_left);
			blogItem.smallCircleRight=(ImageView) v.findViewById(R.id.circle_right);
			
			v.setTag(blogItem);
		} else {
			blogItem = (BlogListItem) v.getTag();
		}
//		// fill data
//		blogItem.uptime.setText(blogList.get(p).getUptime());
//		// fill html data
//		blogItem.content.setText(AppFilter.getHtml(blogList.get(p).getContent()));
//		blogItem.comment.setText(AppFilter.getHtml(blogList.get(p).getComment()));
		if (blogList.get(p).getNumber()!=0&&blogList.get(p).getColorNumber()!=-1) {
			blogItem.colorBackground.setVisibility(View.VISIBLE);
			blogItem.circle.setVisibility(View.VISIBLE);
			blogItem.number.setVisibility(View.VISIBLE);
			blogItem.number.setText(blogList.get(p).getNumber()+"");
			blogItem.circle.setAlpha(100);
		SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date=df.parse(blogList.get(p).getUptime());
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);
			int month=cal.get(Calendar.MONTH);//获取月份
	        int day=cal.get(Calendar.DATE);//获取日
	        int hour=cal.get(Calendar.HOUR_OF_DAY);//小时
	        int minute=cal.get(Calendar.MINUTE);//分
	        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
	        blogItem.smallCircleLeft.setVisibility(View.VISIBLE);
	        blogItem.smallCircleLeft.setAlpha(100);
	        blogItem.smallCircleRight.setVisibility(View.VISIBLE);
	        blogItem.smallCircleRight.setAlpha(100);
	        blogItem.time.setVisibility(View.VISIBLE);
	        blogItem.date.setVisibility(View.VISIBLE);
	        switch (WeekOfYear) {
			case 1:
				blogItem.colorBackground.setBackgroundColor(Color.parseColor("#7fD2691E"));
				break;
			case 2:
				blogItem.colorBackground.setBackgroundColor(Color.parseColor("#7f00BFFF"));
				break;
			case 3:
				blogItem.colorBackground.setBackgroundColor(Color.parseColor("#7f3CB371"));
				break;
			case 4:
				blogItem.colorBackground.setBackgroundColor(Color.parseColor("#7f778899"));
				break;
			case 5:
				blogItem.colorBackground.setBackgroundColor(Color.parseColor("#7fFFE4B5"));
				break;
			case 6:
				blogItem.colorBackground.setBackgroundColor(Color.parseColor("#7fBC8F8F"));
				break;
			} 
	        Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间 
	        Calendar calCurrent=Calendar.getInstance();
	        cal.setTime(curDate);
	        if (month==calCurrent.get(Calendar.MONTH)&&calCurrent.get(Calendar.DATE)==day) {
				blogItem.date.setText("今天");
			}else if (month==calCurrent.get(Calendar.MONTH)&&calCurrent.get(Calendar.DATE)-1==day) {
				blogItem.date.setText("昨天");
			}else if (month==calCurrent.get(Calendar.MONTH)&&calCurrent.get(Calendar.DATE)-2==day) {
				blogItem.date.setText("前天");
			}else {
				blogItem.date.setText(month+1+"."+day);
			}
	        if (minute<10) {
	        blogItem.time.setText(hour+":"+"0"+minute);	
			}else {
			blogItem.time.setText(hour+":"+minute);
			}
 
		} catch (ParseException e) {
			e.printStackTrace();
		} 

		}
		
//		// load face image
//		String faceUrl = blogList.get(p).getFace();
//		if (faceUrl != null && faceUrl.length() > 0) {
//			Bitmap faceImage = AppCache.getImage(faceUrl);//和第87行代码相比，我们发现这个我们用的getImage而不是getCachedImage(),因为在bloglist activity 的一开始，我们就loadImage（blog，getFace()）了，并且我们知道在loadImage中我们会使用一个
//			if (faceImage != null) {                     //异步请求来从网页站点中（注意不是服务器接口站点中）得到图片，并保存到sd卡中。并且那个异步任务的onCompletetask还有一个sendMessage（BaseTask.LOAD_INAGE）,也就是把Message传到BaseHandler中（在UiIndex中重写了），
//				blogItem.face.setImageBitmap(faceImage);//  就是会通知list 来加载图片，所以这里我们可以放心的使用getImage了，但是下面微博中的图片我们没有先弄一个loadImage加载微博图片，我们这里加载微博图片没有使用异步的弄一个线程那样。。所以用了这个方法。
//			}
//		} else {
//			blogItem.face.setImageBitmap(null);
//			
//		}
		// load blog image
		final String picUrl = blogList.get(p).getPicture();
		if (picUrl != null && picUrl.length() > 0) {
			Bitmap picImage = AppCache.getImage( picUrl);
			if (picImage != null) {
				blogItem.picture.setImageBitmap(picImage);
				blogItem.picture.setVisibility(View.VISIBLE);
			}
		 else {
			blogItem.picture.setImageResource(R.drawable.hardworking);
			blogItem.picture.setVisibility(View.VISIBLE);
			new Thread(new BaseTaskPool.TaskThread(ui.getContext(), null, null, null, new BaseTask(){
				@Override
				public void onComplete(){
					AppCache.getCachedImage(ui,  picUrl);
					ui.sendMessage(BaseTask.LOAD_IMAGE);
				}
			}, 0)).start();
		}
		}
		return v;
	}
}