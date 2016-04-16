package com.app.demos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class AppCache {
	
	// tag for log
	private static String TAG = AppCache.class.getSimpleName();
	
	public static Bitmap getCachedImage (Context ctx, String url) {
		String cacheKey = AppUtil.md5(url);
		Bitmap cachedImage = SDUtil.getImage(cacheKey);

		if (cachedImage == null) {
			
			try {
				cachedImage = IOUtil.getBitmapRemote(ctx, url);
				SDUtil.saveImage(cachedImage, cacheKey);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("error", "notfound");
			 Log.e("errornet",Log.getStackTraceString(e)); 
			}
			
		} 
		
		return cachedImage;
	}
	
	public static Bitmap getImage (String url) {
		String cacheKey = AppUtil.md5(url);
		return SDUtil.getImage(cacheKey);
	}
}