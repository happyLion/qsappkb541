package com.app.demos.ui;

import java.util.HashMap;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myqsmy.app.R;
import com.app.demos.base.BaseAuth;
import com.app.demos.base.BaseMessage;
import com.app.demos.base.BaseService;
import com.app.demos.base.BaseUi;
import com.app.demos.base.C;
import com.app.demos.model.Customer;
import com.app.demos.service.NoticeService;


//微博登录sdk相关
import java.text.SimpleDateFormat;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
//import com.sina.weibo.sdk.demo.AccessTokenKeeper;
//import com.sina.weibo.sdk.demo.WBAuthActivity;
//import com.sina.weibo.sdk.demo.AccessTokenKeeper;
//import com.sina.weibo.sdk.demo.Constants;
//import com.sina.weibo.sdk.demo.WBAuthActivity;
//import com.sina.weibo.sdk.demo.WBAuthActivity.AuthListener;
//import com.sina.weibo.sdk.net.RequestListener;
//import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.sina.weibo.sdk.widget.LoginButton;
//import com.sina.weibo.sdk.widget.LoginoutButton;
import com.sina.weibo.sdk.exception.WeiboException;


public class UiLogin extends BaseUi {

	private EditText mEditName;
	private EditText mEditPass;
	private CheckBox mCheckBox;
	private SharedPreferences settings;
	
	//微博登录按钮
    private LoginButton mLoginBtnDefault;

    /** 显示认证后的信息，如 AccessToken */
    private TextView mTokenText;
    
    private AuthInfo mAuthInfo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// check if login
		if (BaseAuth.isLogin()) {
			this.forward(NewUiIndex.class);
		}
		
		// set view after check login
		setContentView(R.layout.ui_login);
		
		// remember password
		mEditName = (EditText) this.findViewById(R.id.app_login_edit_name);
		mEditPass = (EditText) this.findViewById(R.id.app_login_edit_pass);
		mCheckBox = (CheckBox) this.findViewById(R.id.app_login_check_remember);
		mTokenText= (TextView) this.findViewById(R.id.token_text_view);
				
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(UiLogin.this, mAuthInfo);
        mLoginBtnDefault =(LoginButton) findViewById(R.id.login_button_default);
        
        //注册新的onClickListener会把LoginButton自己的冲掉，正合我意
        mLoginBtnDefault.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               mSsoHandler.authorize(new AuthListener());
            }
        });
        
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        if (mAccessToken.isSessionValid()) {
            updateTokenView(true);
           // Toast.makeText(UiLogin.this, 
            //        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
            
            //登录成功后更新到服务器，登录只需要提供access_token和uid就ok了，服务器也只检测这2个参数
            Bundle weiboParams=new Bundle();
            weiboParams.putString("access_token", mAccessToken.getToken());
            weiboParams.putString("uid", mAccessToken.getUid());
            doTaskLogin(weiboParams);
        }
        
		settings = getPreferences(Context.MODE_PRIVATE);
		if (settings.getBoolean("remember", false)) {
			mCheckBox.setChecked(true);
			mEditName.setText(settings.getString("username", ""));
			mEditPass.setText(settings.getString("password", ""));
		}
		
		// remember checkbox
		mCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = settings.edit();
				if (mCheckBox.isChecked()) {
					editor.putBoolean("remember", true);
					editor.putString("username", mEditName.getText().toString());
					editor.putString("password", mEditPass.getText().toString());
				} else {
					editor.putBoolean("remember", false);
					editor.putString("username", "");
					editor.putString("password", "");
				}
				editor.commit();
			}
		});
		
		// login submit
		OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.app_login_btn_submit : 
						doTaskLogin(null);
						break;
				}
			}
		};
		findViewById(R.id.app_login_btn_submit).setOnClickListener(mOnClickListener);
	}
	
	private void doTaskLogin(Bundle weiboParams) {
		app.setLong(System.currentTimeMillis());
		if (weiboParams==null && mEditName.length() > 0 && mEditPass.length() > 0) {
			HashMap<String, String> urlParams = new HashMap<String, String>();
			urlParams.put("name", mEditName.getText().toString());
			urlParams.put("pass", mEditPass.getText().toString());
            
			try {
				this.doTaskAsync(C.task.login, C.api.login, urlParams);
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
		if(weiboParams!=null)
		{   
			HashMap<String, String> urlParams = new HashMap<String, String>();
			urlParams.put("weiboParams", "1");
			//Set<String> keySet = weiboParams.keySet();
			//for(String key:keySet){
			//	urlParams.put(key, weiboParams.getString(key));
			//}
			urlParams.put("access_token", weiboParams.getString("access_token"));
			urlParams.put("uid", weiboParams.getString("uid"));
			//urlParams.put("name", mEditName.getText().toString());
			//urlParams.put("pass", mEditPass.getText().toString());
			try {
				this.doTaskAsync(C.task.login, C.api.login, urlParams);
			} catch (Exception e) {
				e.printStackTrace();
	            Toast.makeText(UiLogin.this, 
	                    e.getMessage(), Toast.LENGTH_SHORT).show();
			}			
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	// async task callback methods
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) {
		super.onTaskComplete(taskId, message);
		switch (taskId) {
			case C.task.login:
				Customer customer = null;
				// login logic
				try {
					customer = (Customer) message.getResult("Customer");
					// login success
					if (customer.getName() != null) {
						BaseAuth.setCustomer(customer);
						BaseAuth.setLogin(true);
					// login fail
					} else {
						BaseAuth.setCustomer(customer); // set sid
						BaseAuth.setLogin(false);
						toast(this.getString(R.string.msg_loginfail));
					}
				} catch (Exception e) {
					e.printStackTrace();
					toast(e.getMessage());
				}
				// login complete
				long startTime = app.getLong();
				long loginTime = System.currentTimeMillis() - startTime;
				Log.w("LoginTime", Long.toString(loginTime));
				// turn to index
				if (BaseAuth.isLogin()) {
					// start service
					BaseService.start(this, NoticeService.class);
					// turn to index
					forward(NewUiIndex.class);
				}
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
			doFinish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
    /**
     * 显示当前 Token 信息。
     * 
     * @param hasExisted 配置文件中是否已存在 token 信息并且合法
     */
    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
       // mAccessToken.get
        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        mTokenText.setText(message);
    }
    
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
    }
    
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息 
            String  phoneNum =  mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), mAccessToken);
                Toast.makeText(UiLogin.this, 
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                
                //登录成功后更新到服务器
                doTaskLogin(values);
                
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(UiLogin.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(UiLogin.this, 
                   R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(UiLogin.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
}

