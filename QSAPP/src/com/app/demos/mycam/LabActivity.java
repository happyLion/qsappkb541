package com.app.demos.mycam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.myqsmy.app.R;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.C;
import com.app.demos.ui.UiConfig;
import com.app.demos.ui.NewUiIndex;

public final class LabActivity extends BaseUiAuth {
    
//    public static final String PHOTO_FILE_EXTENSION = ".png";
//    public static final String PHOTO_MIME_TYPE = "image/png";
    public static final String PHOTO_FILE_EXTENSION = ".jpg";
    public static final String PHOTO_MIME_TYPE = "image/jpg";
    
    public static final String EXTRA_PHOTO_URI =
            "com.nummist.secondsight.LabActivity.extra.PHOTO_URI";
    public static final String EXTRA_PHOTO_DATA_PATH =
            "com.nummist.secondsight.LabActivity.extra.PHOTO_DATA_PATH";
    
    private Uri mUri;
    private String mDataPath;
    
    @Override
	public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("errobyintent", "1");
        final Intent intent = getIntent();
        mUri = intent.getParcelableExtra(EXTRA_PHOTO_URI);
        mDataPath = intent.getStringExtra(EXTRA_PHOTO_DATA_PATH);
        
        final ImageView imageView = new ImageView(this);
        imageView.setImageURI(mUri);
        
        setContentView(imageView);
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
    	Log.e("errobyintent", "2");
        getMenuInflater().inflate(R.menu.activity_lab, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
    	Log.e("errobyintent", "3");
        switch (item.getItemId()) {
        case R.id.menu_delete:
            deletePhoto();
            return true;
        case R.id.menu_edit:
            editPhoto();
            return true;
        case R.id.menu_share:
            sharePhoto();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /*
     * Show a confirmation dialog. On confirmation ("Delete"), the
     * photo is deleted and the activity finishes.
     */
    private void deletePhoto() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(
                LabActivity.this);
        alert.setTitle(R.string.photo_delete_prompt_title);
        alert.setMessage(R.string.photo_delete_prompt_message);
        alert.setCancelable(false);
        alert.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                            final int which) {
                        getContentResolver().delete(
                                Images.Media.EXTERNAL_CONTENT_URI,
                                MediaStore.MediaColumns.DATA + "=?",
                                new String[] { mDataPath });
                        finish();
                    }
                });
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();
    }
    
    /*
     * Show a chooser so that the user may pick an app for editing
     * the photo.
     */
    private void editPhoto() {
        final Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(mUri, PHOTO_MIME_TYPE);
        startActivity(Intent.createChooser(intent,
                getString(R.string.photo_edit_chooser_title)));
    }
    
    /*
     * Show a chooser so that the user may pick an app for sending
     * the photo.
     */
    private void sharePhoto() {
    	HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("content","hello");
		Log.e("errors","finished1" );
		if (mDataPath != null) {
			List<NameValuePair> files = new ArrayList<NameValuePair>();
			files.add(new BasicNameValuePair("file0", mDataPath));
			Log.e("errors","finished2" );
			doTaskAsync(C.task.blogCreate, C.api.blogCreate, urlParams, files);
		} else {
			Log.e("errors","finished3" );
			doTaskAsync(C.task.blogCreate, C.api.blogCreate, urlParams);
		}
	}
    
    @Override
	public void onTaskComplete(int taskId, BaseMessage message) {
    	Log.e("errors","finished4" );
		super.onTaskComplete(taskId, message);
		Log.e("errors","finished5" );
		forward(UiConfig.class);
	}
	
	@Override
	public void onNetworkError (int taskId) {
		super.onNetworkError(taskId);
	}
    
    
    
    
    
    }

