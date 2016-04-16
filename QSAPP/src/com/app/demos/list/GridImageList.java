package com.app.demos.list;

import java.util.List;

import com.app.demos.base.BaseTask;
import com.app.demos.base.BaseTaskPool;
import com.app.demos.base.BaseUi;
import com.app.demos.util.AppCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridImageList extends BaseAdapter {

	private Context context;
	private List<String> imageUrls;
	
	public GridImageList (Context context, List<String> imageUrls) {
		this.context = context;
		this.imageUrls = imageUrls;
	}
	
	@Override
	public int getCount() {
		return imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setPadding(10, 10, 10, 10);
		// get pic from remote
		
		//这里是UI主线程，在UI主线程里面进行网络操作会抛出异常
		//Bitmap bitmap = AppCache.getCachedImage(context, imageUrls.get(position));
		//imageView.setImageBitmap(bitmap);
		Bitmap picImage = AppCache.getImage( imageUrls.get(position));
		final String picUrl=imageUrls.get(position);
		if (picImage != null) {
			imageView.setImageBitmap(picImage);
			imageView.setVisibility(View.VISIBLE);
		}
		else {
			imageView.setImageBitmap(null);
			//imageView.setVisibility(View.GONE);
		new Thread(new BaseTaskPool.TaskThread(context, null, null, null, new BaseTask(){
			@Override
			public void onComplete(){
				AppCache.getCachedImage(context,  picUrl);
				((BaseUi)context).sendMessage(BaseTask.LOAD_IMAGE);//转型成BaseUi来发送图片load好的信息
			}
		}, 0)).start();
	}
		return imageView;
	}
	
}