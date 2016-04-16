package com.app.demos.mycam;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.myqsmy.app.R;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.C;

// Use the deprecated Camera class.
@SuppressWarnings("deprecation")
public final class CameraActivity extends BaseUiAuth implements
		CvCameraViewListener2 {

	// A tag for log output.
	private static final String TAG = CameraActivity.class.getSimpleName();

	// A key for storing the index of the active camera.
	private static final String STATE_CAMERA_INDEX = "cameraIndex";

	// A key for storing the index of the active image size.
	private static final String STATE_IMAGE_SIZE_INDEX = "imageSizeIndex";

	// An ID for items in the image size submenu.
	private static final int MENU_GROUP_ID_SIZE = 2;

	// The index of the active camera.
	private int mCameraIndex;

	// The index of the active image size.
	private int mImageSizeIndex;

	// Whether the active camera is front-facing.
	// If so, the camera view should be mirrored.
	private boolean mIsCameraFrontFacing;

	// The number of cameras on the device.
	private int mNumCameras;

	// The image sizes supported by the active camera.
	private List<Size> mSupportedImageSizes;

	// The camera view.
	private CameraBridgeViewBase mCameraView;

	// Whether the next camera frame should be saved as a photo.
	private boolean mIsPhotoPending;

	// A matrix that is used when saving photos.
	private Mat mBgr;

	// Whether an asynchronous menu action is in progress.
	// If so, menu interaction should be disabled.
	private boolean mIsMenuLocked;

	public final static int TASK_ID_UPDATE_UI=11;
	TextView fpsView;
	TextView movView;
	TextView threshView;
	TextView uploadfreqView;
	TextView uploadingView;
	
	// The OpenCV loader callback.
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(final int status) {
			Log.e("errobyintent", "1");
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				Log.d(TAG, "OpenCV loaded successfully");
				mCameraView.enableView();
				// mCameraView.enableFpsMeter();
				mBgr = new Mat();
				break;
			default:
				super.onManagerConnected(status);
				break;
			}
		}
	};

	// Suppress backward incompatibility errors because we provide
	// backward-compatible fallbacks.
	@SuppressLint("NewApi")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("errobyintent", "2");
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
		//window.setTitle("标题测试");
		if (savedInstanceState != null) {
			mCameraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
			mImageSizeIndex = savedInstanceState.getInt(STATE_IMAGE_SIZE_INDEX,
					12);
		} else {
			mCameraIndex = 0;
			mImageSizeIndex = 3;
		}

		final Camera camera;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(mCameraIndex, cameraInfo);
			mIsCameraFrontFacing = (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
			mNumCameras = Camera.getNumberOfCameras();
			camera = Camera.open(mCameraIndex);
		} else { // pre-Gingerbread
			// Assume there is only 1 camera and it is rear-facing.
			mIsCameraFrontFacing = false;
			mNumCameras = 1;
			camera = Camera.open();
		}
		final Parameters parameters = camera.getParameters();
		camera.release();
		mSupportedImageSizes = parameters.getSupportedPreviewSizes();
		final Size size = mSupportedImageSizes.get(mImageSizeIndex);

		mCameraView = new JavaCameraView(this, mCameraIndex);
		mCameraView.setMaxFrameSize(size.width, size.height);
		mCameraView.setCvCameraViewListener(this);
		
		mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(mCameraView);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.camera_activity_titlebar);
		
		fpsView=(TextView) findViewById(R.id.camera_activity_titlebar_fps);
		movView=(TextView) findViewById(R.id.camera_activity_titlebar_mov);
		threshView=(TextView) findViewById(R.id.camera_activity_titlebar_thresh);
		uploadfreqView=(TextView) findViewById(R.id.camera_activity_titlebar_uploadfreq);
		uploadingView=(TextView) findViewById(R.id.camera_activity_titlebar_uploading);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.e("errobyintent", "3");
		// Save the current camera index.
		savedInstanceState.putInt(STATE_CAMERA_INDEX, mCameraIndex);

		// Save the current image size index.
		savedInstanceState.putInt(STATE_IMAGE_SIZE_INDEX, mImageSizeIndex);

		super.onSaveInstanceState(savedInstanceState);
	}

	// Suppress backward incompatibility errors because we provide
	// backward-compatible fallbacks.
	@SuppressLint("NewApi")
	@Override
	public void recreate() {
		Log.e("errobyintent", "4");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.recreate();
		} else {
			finish();
			startActivity(getIntent());
		}
	}

	@Override
	public void onPause() {
		Log.e("errobyintent", "5");
		if (mCameraView != null) {
			mCameraView.disableView();
		}
		super.onPause();
	}


	static{ 
		
		System.loadLibrary("opencv_java3");

/*	    if (!OpenCVLoader.initDebug()) {
	        // Handle initialization error
	    	int a=0;
	    	a+=1;
	    }
	    */
	}
	
	@Override
	public void onResume() {
		Log.e("errobyintent", "6");
		super.onResume();

		//OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
		mIsMenuLocked = false;
	}

	@Override
	public void onDestroy() {
		if (mCameraView != null) {
			mCameraView.disableView();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		Log.e("errobyintent", "7");
		//static int a=0;
		getMenuInflater().inflate(R.menu.activity_camera, menu);
		if (mNumCameras < 2) {
			// Remove the option to switch cameras, since there is
			// only 1.
			menu.removeItem(R.id.menu_next_camera);
		}
		int numSupportedImageSizes = mSupportedImageSizes.size();
		if (numSupportedImageSizes > 1) {
			final SubMenu sizeSubMenu = menu
					.addSubMenu(R.string.menu_image_size);
			for (int i = 0; i < numSupportedImageSizes; i++) {
				final Size size = mSupportedImageSizes.get(i);
				sizeSubMenu.add(MENU_GROUP_ID_SIZE, i, Menu.NONE,
						String.format("%dx%d", size.width, size.height));
			}
		}
		return true;
	}

	// Suppress backward incompatibility errors because we provide
	// backward-compatible fallbacks (for recreate).
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Log.e("errobyintent", "8");
		if (mIsMenuLocked) {
			return true;
		}
		if (item.getGroupId() == MENU_GROUP_ID_SIZE) {
			Log.e("errobyintent", "9");
			mImageSizeIndex = item.getItemId();
			recreate();

			return true;
		}
		switch (item.getItemId()) {

		case R.id.menu_next_camera:
			mIsMenuLocked = true;

			// With another camera index, recreate the activity.
			mCameraIndex++;
			if (mCameraIndex == mNumCameras) {
				mCameraIndex = 0;
			}
			mImageSizeIndex = 0;
			recreate();

			return true;
		case R.id.menu_take_photo:
			mIsMenuLocked = true;

			// Next frame, take the photo.
			mIsPhotoPending = true;

			return true;
		default:
			return new Activity().onOptionsItemSelected(item);
		}
	}

	// 开始背景减除逻辑
	BackgroundSubtractorMOG2 bg2;
	Mat zoomFore;// 背景减除后的前景
	Mat zoomForePre;
	Mat zoomFrame;// 本帧
	Mat zoomFramepre;
	
	public static final int      VIEW_MODE_NORMAL      = 0;//平时看前景，有异动的时候画面中央为异动
	public static final int      VIEW_MODE_FORE      = 1;//前景
	public static final int      VIEW_MODE_BACK      = 2;//背景
	public static final int      VIEW_MODE_CAMERA      = 3;//背景
	
	private double fps;
	private double movingRateThresh;
	private int mViewMode = VIEW_MODE_NORMAL;
	private boolean mOnProcessing=true;//开始监控
	private long mae=java.lang.System.currentTimeMillis();
	
	
	
	
	
	
	//private long ima=java.lang.System.currentTimeMillis();	
	
	
	void limitFps()
	{
		long ima=java.lang.System.currentTimeMillis();
		long bin=(long)(1000.0/fps);
		long sub=ima-mae;
		if(sub<bin)
		{
			long res=bin-sub;//0~500ms
			try {
				if(res>0 && res<500)
					Thread.sleep(bin-sub);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mae=ima;
	}
	
	double uploadfreq=3;//每3秒上传一张
	double uploadfreqRest=0;//<=0表示可以上传
	int uploading=0;//0个任务在上传
	int uploadingCount=0;//上传闪实现
	double movingRate=0;
	
	//private int sendPoint=-24;//每个循环+1，每发送一次变成-fps，也就是说异动发生时，每s发送一次
	private long now=java.lang.System.currentTimeMillis();
	@Override
	public void onCameraViewStarted(final int width, final int height) {
		bg2 = Video.createBackgroundSubtractorMOG2();
		bg2.setVarMin(100);
		bg2.setVarMax(100000000);
	}

	@Override
	public void onCameraViewStopped() {
	}

	
	public void updateUI()//在主线程更新UI
	{
		fpsView.setText(String.format("[%.1f] ",this.fps));
		movView.setText(String.format("[%.3f] ",movingRate));
		
		threshView.setText(String.format("[%.3f] ",movingRateThresh));
		uploadfreqView.setText(String.format("[%.1f] ",uploadfreq));

		if(uploading>0)
		{
		//把1s分成4份实习上传
		String t=String.format("上传中(%d)", uploading);
		uploadingCount++;				
		double t2=(uploadingCount%(fps+1)+0.0)/(fps+1);
		for(double t3=0;t3<t2;t3+=0.2)
			t+='.';
		uploadingView.setText(t);
		}
		else
		{
			uploadingView.setText("");
		}
	}
	
	@Override
	public Mat onCameraFrame(final CvCameraViewFrame inputFrame) {//这是工作者线程，不是UI线程，不能操作UI！
		Log.e("errobyintent", "10");
		
        SharedPreferences preferences = null;
		preferences=getSharedPreferences("data",MODE_PRIVATE);//得到SharedPreferences
		movingRateThresh=preferences.getFloat("movingRateThresh", (float) 0.01); 
		fps=preferences.getInt("fps", 12);
		
		
		
		Mat gray = inputFrame.gray();// rgba负责显示，可能在rgba上画东西
		Mat rgba = null;// inputFrame.rgba();
		//getWindow().setTitle("title test");
		zoomFore = new Mat();
		bg2.apply(gray, zoomFore);

		if (mIsPhotoPending) {
			mIsPhotoPending = false;
			if (rgba == null)
				rgba = inputFrame.rgba();
			takePhoto(rgba);
		}

		if (mIsCameraFrontFacing) {
			// Mirror (horizontally flip) the preview.
			if (rgba == null)
				rgba = inputFrame.rgba();
			Core.flip(rgba, rgba, 1);
		}
		
		if(mOnProcessing==true)//计算异变区域占据总画面的多少
		{
			if (rgba == null)
				rgba = inputFrame.rgba();
			
			double nonZero=org.opencv.core.Core.countNonZero(zoomFore);
			double sum=zoomFore.width() * zoomFore.height();
			movingRate=nonZero/(sum+1);//防止除0
			//java.lang.String text=java.lang.String.format("mov:%4.1f%%",movingRate*100);
			long now_new=java.lang.System.currentTimeMillis();
			//double fps=1000.0/(now_new-now);
			now=now_new;

			if(movingRate>movingRateThresh && uploadfreqRest<=0)//有异动并且过了指定间隔时间
			{
				//发送rgba
				Mat dst=new Mat();
				if(rgba.size().width > 480)
				{
				org.opencv.core.Size sz=rgba.size(); 
				org.opencv.imgproc.Imgproc.resize(rgba, dst, new org.opencv.core.Size(), 480.0/sz.width, 480.0/sz.width, org.opencv.imgproc.Imgproc.INTER_AREA);
				}
				else
				{
					dst=rgba;
				}
				//开始从zoomFore里计算前景的Rect
				//org.opencv.core.Rect boundingRect=org.opencv.imgproc.Imgproc.boundingRect(zoomFore);
				uploading++;
				takePhotoUpload(dst, zoomFore);
				//sendPoint=-3*(int)fps;//每3s发送一次
				uploadfreqRest=uploadfreq;
			}
			//字的原点和大小以高1080为标准缩放
			//double hi=zoomFore.height();
			//org.opencv.core.Scalar iro=movingRate>movingRateThresh?new org.opencv.core.Scalar(255, 0, 0):new org.opencv.core.Scalar(0, 0, 255);
			//org.opencv.imgproc.Imgproc.putText(rgba, text, new org.opencv.core.Point(25*hi/720, 25*hi/720), 1, 2.5*hi/720, iro); 
			uploadfreqRest-=1/(fps+1);
			//sendPoint++;
		}
		//通知UI线程调用updateUI
		doTaskAsync(TASK_ID_UPDATE_UI, 0);
		
		switch (mViewMode) {

		case VIEW_MODE_FORE:
			return zoomFore;
		case VIEW_MODE_BACK: {
			Mat bg = new Mat();
			bg2.getBackgroundImage(bg);
			return bg;
		}
		case VIEW_MODE_NORMAL:
		{
			if (rgba == null)
				rgba = inputFrame.rgba();
			
			org.opencv.core.Size sizeRgba = rgba.size();

	        int rows = (int) sizeRgba.height;
	        int cols = (int) sizeRgba.width;

	        int left = cols / 8;
	        int top = rows / 8;

	        int width = cols * 3 / 4;
	        int height = rows * 3 / 4;

	        if(movingRate>movingRateThresh)//有异动
	        {
	        	Mat rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
	            Imgproc.cvtColor(zoomFore.submat(top, top + height, left, left + width), rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
	            rgbaInnerWindow.release();
	        }
	        
	        limitFps();
			return rgba;
		}
		case VIEW_MODE_CAMERA:
			default:
				if (rgba == null)
					rgba = inputFrame.rgba();
				return rgba;				
		}
	}
	
	@SuppressLint("NewApi")
	private void takePhotoUpload(final Mat rgba, final Mat zoomFore) {
		// Determine the path and metadata for the photo.
		final long currentTimeMillis = System.currentTimeMillis();
		final String appName = getString(R.string.app_name);
		final String galleryPath = Environment
				.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES).toString();
		final String albumPath = galleryPath + File.separator + appName;
		final String photoPath = albumPath + File.separator + currentTimeMillis
				+ LabActivity.PHOTO_FILE_EXTENSION;
		
		final String zoomForePath = albumPath + File.separator+ currentTimeMillis+"zoomFore" + ".png";//单通道的zoomFore不使用默认的jpg而使用png更合适
		
		Log.e("erroe", "photoPath==" + photoPath);

		final ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, photoPath);
		values.put(Images.Media.MIME_TYPE, LabActivity.PHOTO_MIME_TYPE);
		values.put(Images.Media.TITLE, appName);
		values.put(Images.Media.DESCRIPTION, appName);
		values.put(Images.Media.DATE_TAKEN, currentTimeMillis);

		// Ensure that the album directory exists.
		File album = new File(albumPath);
		if (!album.isDirectory() && !album.mkdirs()) {
			Log.e(TAG, "Failed to create album directory at " + albumPath);
			onTakePhotoFailed();
			return;
		}

		// Try to create the photo.
		Imgproc.cvtColor(rgba, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
		if (!Imgcodecs.imwrite(photoPath, mBgr)) {
			Log.e(TAG, "Failed to save photo to " + photoPath);
			onTakePhotoFailed();
		}
		
		if (!Imgcodecs.imwrite(zoomForePath, zoomFore)) {
			Log.e(TAG, "Failed to save zoomFore to " + zoomForePath);
			onTakePhotoFailed();
		}
		
		Log.d(TAG, "Photo saved successfully to " + photoPath);

		// Try to insert the photo into the MediaStore.
		Uri uri;
		try {
			uri = getContentResolver().insert(
					Images.Media.EXTERNAL_CONTENT_URI, values);
		} catch (final Exception e) {
			Log.e(TAG, "Failed to insert photo into MediaStore");
			e.printStackTrace();

			// Since the insertion failed, delete the photo.
			File photo = new File(photoPath);
			if (!photo.delete()) {
				Log.e(TAG, "Failed to delete non-inserted photo");
			}

			onTakePhotoFailed();
			return;
		}
		
    	HashMap<String, String> urlParams = new HashMap<String, String>();
        
    	//java.util.Date d = new java.util.Date();  
        //System.out.println(d);  
        //java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
       // String dateNowStr = sdf.format(d);  
       // String content=dateNowStr+": "+"moving object";
        String content="";
        
		urlParams.put("content", content);
		Log.e("errors","finished1" );
		if (photoPath != null) {
			List<NameValuePair> files = new ArrayList<NameValuePair>();
			files.add(new BasicNameValuePair("file0", photoPath));
			files.add(new BasicNameValuePair("file1", zoomForePath));
			Log.e("errors","finished2" );
			doTaskAsync(C.task.blogCreate, C.api.blogCreate, urlParams, files);
		}
	}
	@SuppressLint("NewApi")
	private void takePhoto(final Mat rgba) {

		// Determine the path and metadata for the photo.
		final long currentTimeMillis = System.currentTimeMillis();
		final String appName = getString(R.string.app_name);
		final String galleryPath = Environment
				.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES).toString();
		final String albumPath = galleryPath + File.separator + appName;
		final String photoPath = albumPath + File.separator + currentTimeMillis
				+ LabActivity.PHOTO_FILE_EXTENSION;
		Log.e("erroe", "photoPath==" + photoPath);

		final ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, photoPath);
		values.put(Images.Media.MIME_TYPE, LabActivity.PHOTO_MIME_TYPE);
		values.put(Images.Media.TITLE, appName);
		values.put(Images.Media.DESCRIPTION, appName);
		values.put(Images.Media.DATE_TAKEN, currentTimeMillis);

		// Ensure that the album directory exists.
		File album = new File(albumPath);
		if (!album.isDirectory() && !album.mkdirs()) {
			Log.e(TAG, "Failed to create album directory at " + albumPath);
			onTakePhotoFailed();
			return;
		}

		// Try to create the photo.
		Imgproc.cvtColor(rgba, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
		if (!Imgcodecs.imwrite(photoPath, mBgr)) {
			Log.e(TAG, "Failed to save photo to " + photoPath);
			onTakePhotoFailed();
		}
		Log.d(TAG, "Photo saved successfully to " + photoPath);

		// Try to insert the photo into the MediaStore.
		Uri uri;
		try {
			uri = getContentResolver().insert(
					Images.Media.EXTERNAL_CONTENT_URI, values);
		} catch (final Exception e) {
			Log.e(TAG, "Failed to insert photo into MediaStore");
			e.printStackTrace();

			// Since the insertion failed, delete the photo.
			File photo = new File(photoPath);
			if (!photo.delete()) {
				Log.e(TAG, "Failed to delete non-inserted photo");
			}

			onTakePhotoFailed();
			return;
		}

		// Open the photo in LabActivity.
		final Intent intent = new Intent(this, LabActivity.class);
		intent.putExtra(LabActivity.EXTRA_PHOTO_URI, uri);
		intent.putExtra(LabActivity.EXTRA_PHOTO_DATA_PATH, photoPath);
		startActivity(intent);
	}

	private void onTakePhotoFailed() {
		mIsMenuLocked = false;

		// Show an error message.
		final String errorMessage = getString(R.string.photo_error_message);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(CameraActivity.this, errorMessage,
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	@Override
	public void onTaskComplete (int taskId, BaseMessage message) {//上传图像成功
		switch(taskId)
		{
		case C.task.blogCreate:
		uploading--;
		}
	}
	
	@Override
	public void onTaskComplete (int taskId) {//上传图像成功
		switch(taskId)
		{
		case TASK_ID_UPDATE_UI:
			updateUI();
		}
	}	
	@Override
	public void onNetworkError (int taskId) {//上传图像失败
		switch(taskId)
		{		
		case C.task.blogCreate:
		toast("上传失败");
		uploading--;
		}
	}
}
