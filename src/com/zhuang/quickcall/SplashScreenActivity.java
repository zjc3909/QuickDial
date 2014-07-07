package com.zhuang.quickcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.baidu.mobstat.StatService;
import com.zhuang.quickcall.activity.QuickCallActivity;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;

public class SplashScreenActivity extends QuickCallActivity{
	
	private static final String TAG = "[COBE_FSA]SplashScreenActivity";

	private static final int SPLASH_SHOW_TIME = 1000;
	private boolean mIsFirstOpen = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreate...");
		}
		
		if(HomeFragmentActivity.isAlive()){
			finish();
			return;
		}
		this.setContentView(R.layout.splash_screen_layout);
		
		mIsFirstOpen = false;
		SharedPreferences setting = getSharedPreferences(QuickCallConstants.QC_SHARED_PREFERENCES_CONSTANTS, 0);  
		Boolean user_first = setting.getBoolean(QuickCallConstants.QC_IS_FIRST_OPEN, true);
		if (user_first) {
			mIsFirstOpen = true;
			setting.edit().putBoolean(QuickCallConstants.QC_IS_FIRST_OPEN, false).commit();
		}
		
		   
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				if(mIsFirstOpen){
					startGuideScreen();
				} else {
					startHomeScreen();
				}
			}
			
		}, SPLASH_SHOW_TIME);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onResume...");
		}
		StatService.onResume(this);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onStart...");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onPause...");
		}
		StatService.onPause(this);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		
		super.leakCleanUpRootView();
	}
	
	private void startHomeScreen() {
		if (LogLevel.DEV) {
			DevLog.i(TAG, "start login screen");
		}
		Intent intent = new Intent(this, QuickCallMainActivity.class);
		this.startActivity(intent);
		this.finish();
		this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void startGuideScreen() {
		if (LogLevel.DEV) {
			DevLog.i(TAG, "start guide screen");
		}
		Intent intent = new Intent(this, GuideScreenActivity.class);
		this.startActivity(intent);
		this.finish();
		this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
}
