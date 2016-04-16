package com.app.demos.base;


import com.myqsmy.app.R;
import com.app.demos.util.AppCache;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class EnLargePic extends BaseUiAuth {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.e("errorhaha", "17");
        super.onCreate(savedInstanceState);
        Log.e("errorhaha", "18");
        try {
        	setContentView(R.layout.pic_enlarge);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("errors",Log.getStackTraceString(e));
		}
        
        Log.e("errorhaha", "19");
        Intent intent =getIntent();
        String picUrl = intent.getStringExtra("picUrl");
        
        
        ImageView mImageView = (ImageView) findViewById(R.id.iv_photo);

 //       Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
//        mImageView.setImageDrawable(bitmap);
        if (picUrl != null && picUrl.length() > 0) {
			Bitmap picImage = AppCache.getImage( picUrl);
			if (picImage != null) {
				mImageView.setImageBitmap(picImage);
				mImageView.setVisibility(View.VISIBLE);
				
			}
		}
	}
	
	


}
	

