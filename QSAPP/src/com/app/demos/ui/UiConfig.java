package com.app.demos.ui;

import java.util.HashMap;

import com.app.demos.base.BaseHandler;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseTask;
import com.app.demos.base.BaseUi;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.C;
import com.app.demos.model.Customer;
import com.app.demos.mycam.CameraActivity;
import com.app.demos.mycam.RecordVideo;
import com.app.demos.util.AppCache;
import com.app.demos.util.HelpDoc;
import com.app.demos.util.UIUtil;
import com.myqsmy.app.R;

import android.R.integer;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class UiConfig extends BaseUiAuth {

	private ImageView customer_face;
	private TextView customer_signature;
	private TextView moverNum;
	private ImageView helpdocument;
	private ImageView changeSignature;
	private ImageView recordVideo;
	private ImageView changeHeadPicture;
	private ImageView sensitive;
	private String faceImageUrl;
	private LinearLayout layout;
	private ImageButton imageButton;
	private SeekBar seekBarUp;
	private SeekBar seekBarMiddle;
	private SeekBar seekBardown;
	private SharedPreferences sharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_config);
		initView();
		initEvent();
		// set handler
		this.setHandler(new ConfigHandler(this));
	
	}

	private void initEvent() {
		faceImageUrl = customer.getFaceurl();
		if (faceImageUrl!=null) {
			Bitmap face = AppCache.getImage(faceImageUrl);
			if (face!=null) {
				customer_face.setImageBitmap(face);	
		    }
		}
		
		layout.setBackgroundColor(Color.parseColor("#3c3f41"));
		imageButton.setBackgroundColor(Color.parseColor("#3c3f41"));
		sharedPreferences=getSharedPreferences("data", MODE_PRIVATE);
		int a =sharedPreferences.getInt("sensitiveNum", 50);
		int b =sharedPreferences.getInt("fps", 12);
		float c= sharedPreferences.getFloat("movingRateThresh", (float) 0.01);
		seekBarUp.setProgress(a);
		seekBarMiddle.setProgress((b-4)*5);
		seekBardown.setProgress((int) ((c-0.005)*10000));
		
		helpdocument.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				overlay(HelpDoc.class);
			}
		});
		
		changeHeadPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				overlay(UiSetFace.class);
			}
		});
		
		changeSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				data.putInt("action", C.action.edittext.CONFIG);
				data.putString("value", customer.getSign());
				doEditText(data);
			}
		});
		
		recordVideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				overlay(RecordVideo.class);
				
			}
		});
		
		sensitive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		seekBarUp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				SharedPreferences.Editor editor=getSharedPreferences("data", MODE_PRIVATE).edit();
				editor.putInt("sensitiveNum", progress);
				editor.commit();
			}
		});
		
		seekBarMiddle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int progress1=4+progress/5;
				SharedPreferences.Editor editor=getSharedPreferences("data", MODE_PRIVATE).edit();
				editor.putInt("fps", progress1);
				editor.commit();
			}
		});
		
		seekBardown.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				double progessFlo=0.005+(double)(progress)/(double)(10000);
				SharedPreferences.Editor editor=getSharedPreferences("data", MODE_PRIVATE).edit();
				editor.putFloat("movingRateThresh", (float) progessFlo);
				editor.commit();
				
			}
		});
		
	}

	private void initView() {
	    customer_face=(ImageView) findViewById(R.id.tpl_list_info_image_face);
	    customer_signature=(TextView) findViewById(R.id.tpl_list_info_text_top);
	    moverNum=(TextView) findViewById(R.id.tpl_list_info_text_bottom);
	    helpdocument=(ImageView) findViewById(R.id.imageView_up_left);
	    changeSignature=(ImageView) findViewById(R.id.imageView_up_right);
	    recordVideo=(ImageView) findViewById(R.id.imageView_middle_left);
	    changeHeadPicture=(ImageView) findViewById(R.id.imageView_middle_right);
	    sensitive=(ImageView) findViewById(R.id.imageView_down_left);
	    layout=(LinearLayout) findViewById(R.id.main_tab_layout2);
        imageButton=(ImageButton) findViewById(R.id.main_tab_3);
        seekBarUp=(SeekBar) findViewById(R.id.seekbar_up);
        seekBarMiddle=(SeekBar) findViewById(R.id.seekbar_middle);
        seekBardown=(SeekBar) findViewById(R.id.seekbar_down);
	}	
	
	
	@Override
	public void onStart() {
		super.onStart();
		// prepare customer data
		HashMap<String, String> cvParams = new HashMap<String, String>();
		cvParams.put("customerId", customer.getId());
		this.doTaskAsync(C.task.customerView, C.api.customerView, cvParams);
	}
	
	// async task callback methods
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		switch (taskId) {
			case C.task.customerView:
				try {
						final Customer customer = (Customer) message.getResult("Customer");
						customer_signature.setText(customer.getSign());
						moverNum.setText(customer.getBlogcount());
						// load face image async
//						faceImageUrl = customer.getFaceurl();
//						Bitmap face = AppCache.getImage(faceImageUrl);
//						if (face!=null) {
//							customer_face.setImageBitmap(face);
//						}else {loadImage(faceImageUrl);}
						if ( AppCache.getImage(faceImageUrl)==null) {
							loadImage(faceImageUrl);
						}
				
						
					} catch (Exception e) {
						e.printStackTrace();
						toast(e.getMessage());
					}
					break;
			}
		}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////
// inner classes

private class ConfigHandler extends BaseHandler {
  public ConfigHandler(BaseUi ui) {
    super(ui);
  }
  @Override
  public void handleMessage(Message msg) {
    super.handleMessage(msg);
    try {
    switch (msg.what) {
    case BaseTask.LOAD_IMAGE:
    Bitmap face = AppCache.getImage(faceImageUrl);
        customer_face.setImageBitmap(face);
    break;
    }
    }
     catch (Exception e) {
    e.printStackTrace();
    ui.toast(e.getMessage());
    }
  }
}
	
}


