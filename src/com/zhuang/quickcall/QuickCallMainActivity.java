package com.zhuang.quickcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zhuang.quickcall.activity.QuickCallActivity;
import com.zhuang.quickcall.contacts.ContactsSelectorActivity;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.quickcall.QuickCallInfo;
import com.zhuang.quickcall.quickcall.QuickCallUtils;
import com.zhuang.quickcall.utils.DialUtils;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.widgets.LocusPassWordView;
import com.zhuang.quickcall.utils.widgets.LocusPassWordView.OnCompleteListener;

public class QuickCallMainActivity extends QuickCallActivity {
	
	private static final String TAG = "[ZHUANG]QuickCallMainActivity";

	private LocusPassWordView mLocusPassWordView;
	private ImageButton mMenuButton;
	private ImageButton mAddButton;
	
	private View mTipsView;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreate...");
		}
		
		this.setContentView(R.layout.quick_call_main_layout);
		
		mTipsView = this.findViewById(R.id.main_tips_layout);
		mTipsView.setVisibility(View.GONE);
		SharedPreferences setting = getSharedPreferences(QuickCallConstants.QC_SHARED_PREFERENCES_CONSTANTS, 0);  
		Boolean first_create = setting.getBoolean(QuickCallConstants.QC_IS_FIRST_CREATE, true);
		if (first_create) {
			mTipsView.setVisibility(View.VISIBLE);
			setting.edit().putBoolean(QuickCallConstants.QC_IS_FIRST_CREATE, false).commit();
		}
		
		mTipsView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTipsView.setVisibility(View.GONE);
				return false;
			}
		});
		
		mLocusPassWordView = (LocusPassWordView) this.findViewById(R.id.quick_call_word_view);
		mLocusPassWordView.setOnCompleteListener(new OnCompleteListener(){

			@Override
			public void onComplete(String password) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "Trace finish, password = " + password);
				}
				
				if(QuickCallUtils.isQuickTraceExist(QuickCallMainActivity.this, password)){
					String title = QuickCallMainActivity.this.getString(R.string.dialog_quick_call_title);
					final QuickCallInfo info = QuickCallUtils.getQuickCallInfo(QuickCallMainActivity.this, password);
					if(info == null){
						mLocusPassWordView.clearPassword();
						return;
					}
					String msg = TextUtils.isEmpty(info.name) ? info.number : (info.name + ":" + info.number);
					String message = QuickCallMainActivity.this.getString(R.string.dialog_quick_call_msg, msg);
					String postStr = QuickCallMainActivity.this.getString(R.string.call);
					DialogUtils.showQuickCallAlertDialog(QuickCallMainActivity.this, title, message, postStr, new DialogUtils.OnClickConfirmButtonListener() {
						
						@Override
						public void onClick() {
							mLocusPassWordView.clearPassword(2000);
							DialUtils.callNumber(QuickCallMainActivity.this, info.number);
						}
					}, new DialogUtils.OnClickCancelButtonListener() {
						
						@Override
						public void onClick() {
							mLocusPassWordView.clearPassword();
						}
					});
				} else {
					mLocusPassWordView.clearPassword(1000);
					if(!TextUtils.isEmpty(password)){
						Toast.makeText(QuickCallMainActivity.this, R.string.no_results_found, Toast.LENGTH_LONG).show();
					}
				}
			}
			
		});
		
		mMenuButton = (ImageButton) this.findViewById(R.id.button_menu);
		mAddButton = (ImageButton) this.findViewById(R.id.button_add);
		mMenuButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(QuickCallMainActivity.this, HomeFragmentActivity.class);
				QuickCallMainActivity.this.startActivity(intent);
				QuickCallMainActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		
		mAddButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(QuickCallMainActivity.this, ContactsSelectorActivity.class);
				QuickCallMainActivity.this.startActivity(intent);
				QuickCallMainActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
		
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(mTipsView.getVisibility() == View.VISIBLE){
			mTipsView.setVisibility(View.GONE);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}}
