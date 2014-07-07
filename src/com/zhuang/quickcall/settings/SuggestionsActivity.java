/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.settings;

import com.baidu.mobstat.StatService;
import com.parse.Parse;
import com.parse.ParseObject;
import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.activity.QuickCallActivity;
import com.zhuang.quickcall.config.Build;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.utils.DateTimeUtils;
import com.zhuang.quickcall.utils.DeviceUtils;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.NetworkUtils;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SuggestionsActivity extends QuickCallActivity {
	
	private static final String TAG = "[ZHUANG]SuggestionsActivity";
	
	private ImageButton mBackButton;
	private EditText mReplyContentText;
	private EditText mReplyContactText;
	private Button mSubmitButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.DEV){
			DevLog.i(TAG, "onCreate...");
		}
		this.setContentView(R.layout.suggestions_layout);
		
		buildLayout();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(LogLevel.DEV){
			DevLog.i(TAG, "onResume...");
		}
		StatService.onResume(this);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(LogLevel.DEV){
			DevLog.i(TAG, "onStart...");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(LogLevel.DEV){
			DevLog.i(TAG, "onPause...");
		}
		StatService.onPause(this);
	}
	
	@Override
	public void finish(){
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left, R.anim.back_out_right);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(LogLevel.DEV){
			DevLog.i(TAG, "onDestroy...");
		}
		
		leakCleanUpRootView();
	}
	
	private void buildLayout(){
		mBackButton = (ImageButton) this.findViewById(R.id.button_back);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mReplyContentText = (EditText) this.findViewById(R.id.reply_content);
		mReplyContactText = (EditText) this.findViewById(R.id.reply_contacts);
		mSubmitButton = (Button) this.findViewById(R.id.confirm_button);
		mSubmitButton.setEnabled(false);
		mReplyContentText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				mSubmitButton.setEnabled(!TextUtils.isEmpty(s.toString()));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
		});
		
		mSubmitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!NetworkUtils.isEasiioAvailable(SuggestionsActivity.this)){
					DialogUtils.showQuickCallAlertDialog(SuggestionsActivity.this, R.string.cannot_submit, R.string.call_error_message_network_error);
					return;
				}
				submitSuggestions(SuggestionsActivity.this, mReplyContentText.getText().toString(), mReplyContactText.getText().toString());
			}
		});
	}
	
	private void submitSuggestions(Context context, String content, String contact){
		if(LogLevel.MARKET){
			MarketLog.d(TAG, "start to submit suggestions");
		}
		
		try{
			Parse.initialize(context, Build.PARSE_APPLICATION_ID, Build.PARSE_CLIENT_KEY);
			ParseObject replyObject = new ParseObject(QuickCallConstants.PARSE_REPLY_TABLE_NAME);
			replyObject.put(QuickCallConstants.PARSE_REPLY_TIME, DateTimeUtils.getCurrentTimeStr());
			replyObject.put(QuickCallConstants.PARSE_DEVICE, DeviceUtils.getDeviceName());
			replyObject.put(QuickCallConstants.PARSE_ANDROID_LEVEL, DeviceUtils.getAndroidLevel());
			replyObject.put(QuickCallConstants.PARSE_ANDROID_VERSION, DeviceUtils.getAndroidReleaseVersion());
			replyObject.put(QuickCallConstants.PARSE_APP_VERSION, this.getString(R.string.app_version));
			replyObject.put(QuickCallConstants.PARSE_REPLY_CONTENT, content);
			replyObject.put(QuickCallConstants.PARSE_REPLY_CONTACT, TextUtils.isEmpty(contact) ? "Unknown" : contact);
			replyObject.saveInBackground();
			Toast.makeText(this, R.string.submit_suggestion_success, Toast.LENGTH_SHORT).show();
			finish();
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "submit suggestions failed : " + e.toString());
			}
			Toast.makeText(this, R.string.submit_suggestion_failed, Toast.LENGTH_SHORT).show();
		}
		
		if(LogLevel.MARKET){
			MarketLog.d(TAG, "end submit suggestions");
		}
	}
	
}
