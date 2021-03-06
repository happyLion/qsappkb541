package com.app.demos.ui;

import java.util.ArrayList;
import java.util.HashMap;
import com.myqsmy.app.R;
import com.app.demos.base.BaseHandler;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseTask;
import com.app.demos.base.BaseUi;
import com.app.demos.base.BaseUiAuth;
import com.app.demos.base.C;
import com.app.demos.list.GridImageList;
import com.app.demos.model.Image;
//import com.app.demos.ui.UiBlog.BlogHandler;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UiSetFace extends BaseUiAuth {
	
	GridView faceGridView = null;
	GridImageList gridImageListAdapter = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_face);
		
		this.setHandler(new UiSetFaceHandler(this));
		// get face image list
		this.doTaskAsync(C.task.faceList, C.api.faceList);
	}
	
	private void doSetFace (String faceId) {
		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("key", "face");
		urlParams.put("val", faceId);
		doTaskAsync(C.task.customerEdit, C.api.customerEdit, urlParams);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// async task callback methods
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		switch (taskId) {
			case C.task.faceList:
				try {
					@SuppressWarnings("unchecked")
					final ArrayList<Image> imageList = (ArrayList<Image>) message.getResultList("Image");
					final ArrayList<String> imageUrls = new ArrayList<String>();
					for (int i = 0; i < imageList.size(); i++) {
						Image imageItem = imageList.get(i);
						imageUrls.add(imageItem.getUrl());
					}
					faceGridView = (GridView) this.findViewById(R.id.app_face_grid);
					gridImageListAdapter=new GridImageList(this, imageUrls);
					faceGridView.setAdapter(gridImageListAdapter);
					faceGridView.setOnItemClickListener(new OnItemClickListener(){
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							Image face = imageList.get(position);
							customer.setFace(face.getId());
							doSetFace(face.getId());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
				break;
			case C.task.customerEdit:
				toast("face has changed");
				doFinish();
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
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	// inner classes
	
	private class UiSetFaceHandler extends BaseHandler {
		public UiSetFaceHandler(BaseUi ui) {
			super(ui);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
					case BaseTask.LOAD_IMAGE:
						gridImageListAdapter.notifyDataSetChanged(); 
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				ui.toast(e.getMessage());
			}
		}
	}
}