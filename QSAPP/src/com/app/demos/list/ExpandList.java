package com.app.demos.list;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.myqsmy.app.R;
import com.app.demos.util.AppCache;
import com.app.demos.util.AppFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandList {
	
	final public static int TEXT_VIEW = 1;
	final public static int IMAGE_VIEW = 2;
	final public static int TEXT_VIEW2 = 3;
	final public static int TEXT_VIEW3 = 4;
	final public static int IMAGE_VIEW2 =5;
	
	private LayoutInflater layout = null;
	private Integer dividerId = R.color.divider1;
	private ExpandList.OnItemClickListener itemClickListener = null;
	
	private Context context = null;
	private List<? extends Map<String, ?>> dataList = null;
	private int resourceId = -1;
	private String[] colKeys = {};
	private int[] tplKeys = {};
	private int[] types = {};
	
	public ExpandList (Context context, List<? extends Map<String, ?>> data, int resource, String[] cols, int[] tpls, int[] types) {
		// layout
		this.context = context;
		this.layout = LayoutInflater.from(context);
		// data
		this.resourceId = resource;
		this.dataList = data;
		this.colKeys = cols;
		this.tplKeys = tpls;
		this.types = types;
	}
	
	public View getView () {
		return layout.inflate(resourceId, null);
	}
	
	public void setDivider (Integer dividerId) {
		this.dividerId = dividerId;
	}
	
	public void setOnItemClickListener (ExpandList.OnItemClickListener listener) {
		itemClickListener = listener;
	}
	
	public void render (ViewGroup vg) {
		int dataPos = 0;
		int dataSize = dataList.size();
		int j=0;//标记num
		int m=0;//标记日期时中间数据
		for (Map<String, ?> data : dataList) {
			m=m+1;
			j=j+1;
			View v = getView();
			// render main
			for (int i = 0; i < types.length; i++) {
				String colKey = colKeys[i];
				int tplKey = tplKeys[i];
				int type = types[i];
				switch (type) {
					case ExpandList.TEXT_VIEW :
						TextView tv = (TextView) v.findViewById(tplKey);
						AppFilter.setHtml(tv, data.get(colKey).toString());
						break;
					case ExpandList.IMAGE_VIEW :
						ImageView iv = (ImageView) v.findViewById(tplKey);
						Bitmap img = AppCache.getImage(data.get(colKey).toString());
						if (iv != null) {
							if (img != null) {
								iv.setImageBitmap(img);
								iv.setVisibility(View.VISIBLE);
							} else {
								iv.setImageBitmap(null);
								iv.setVisibility(View.GONE);
							}
						}
						break;
					case ExpandList.TEXT_VIEW2 :
						TextView num = (TextView) v.findViewById(tplKey);
						num.setVisibility(View.VISIBLE);
						num.setText(""+j);
						break;
					case ExpandList.TEXT_VIEW3:
						TextView background = (TextView) v.findViewById(tplKey);
						background.setVisibility(View.VISIBLE);
						break;
					case ExpandList.IMAGE_VIEW2:
						ImageView imageView =  (ImageView) v.findViewById(tplKey);
						imageView.setVisibility(View.VISIBLE);
						imageView.setAlpha(100);;
						break;
						
					default :
						break;
				}

			}
			// add click callback
			if (itemClickListener != null) {
				final int pos = dataPos;
				v.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						itemClickListener.onItemClick(v, pos);
					}
				});
			}
//			if (j==1) {
//				
//				TextView newView= new TextView(context);
//				newView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//						LayoutParams.WRAP_CONTENT));
//				newView.setText("间隔两个小时");
//				// 设置字体大小
//				newView.setTextSize(20);
//				// 设置背景
//				newView.setBackgroundColor(Color.BLUE);
//				// 设置字体颜色
//				newView.setTextColor(Color.RED);
//				//设置居中
//				newView.setGravity(Gravity.CENTER);
//				// 将TextView添加到Linearlayout中去
//				vg.addView(newView);
//			}
			vg.addView(v);
			
			/**
			 * 设置分割线
			 */
			SimpleDateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date   thisTime = null; 
			Date nextTime = null;
			try {
				thisTime=df.parse((String) data.get("uptime")); 
				nextTime = df.parse((String) dataList.get(m).get("uptime"));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				if(thisTime.getDate()!=nextTime.getDate()){
			
				j=0;
				TextView newView= new TextView(context);
				newView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				newView.setText(nextTime.getDay()+"");
				// 设置字体大小
				newView.setTextSize(20);
				// 设置背景
				newView.setBackgroundColor(Color.BLUE);
				// 设置字体颜色
				newView.setTextColor(Color.RED);
				//设置居中
				newView.setGravity(Gravity.CENTER);
				// 将TextView添加到Linearlayout中去
				vg.addView(newView);
			
			}
			
			// count data
			dataPos++;
			// render divider
			if (dataPos < dataSize) {
				View d = new TextView(context, null);
				d.setBackgroundResource(dividerId);
				d.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
				vg.addView(d);
			}
		}

	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// 
	
	abstract public interface OnItemClickListener {
		abstract public void onItemClick(View view, int pos);
	}
}