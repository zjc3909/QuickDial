package com.zhuang.quickcall.quickcall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.activity.QuickCallActivity;
import com.zhuang.quickcall.contacts.ContactsUtils;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.utils.BitmapUtils;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.widgets.LocusPassWordView;
import com.zhuang.quickcall.utils.widgets.LocusPassWordView.OnCompleteListener;

public class CreateQuickCallActivity extends QuickCallActivity {

	private static final String TAG = "[ZHUANG]CreateQuickCallActivity";
	
	public static final int RESULT_SUCCESS = 10010;
	
	private LocusPassWordView mLocusPassWordView;
	private ImageButton mBackButton;
	private Button mResetButton;
	private Button mSaveButton;
	
	private ImageView mPhotoView;
	private TextView mNameView;
	private TextView mNumberView;
	
	private String mDisplayName;
	private String mNumber;
	private long mPhotoId;
	
	private String mQuickCallStr;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.DEV){
			DevLog.i(TAG, "onCreate...");
		}
		this.setContentView(R.layout.quick_call_setting_layout);
		
		Intent intent = this.getIntent();
		if(intent == null){
			finish();
			return;
		}
		
		mDisplayName = intent.getStringExtra(QuickCallConstants.EXTRA_SELECT_NAME);
		mNumber = intent.getStringExtra(QuickCallConstants.EXTRA_SELECT_NUMBER);
		mPhotoId = intent.getLongExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, -1);
		
		if(TextUtils.isEmpty(mNumber)){
			finish();
			return;
		}
		
		mLocusPassWordView = (LocusPassWordView) this.findViewById(R.id.quick_call_word_view);
		mBackButton = (ImageButton) this.findViewById(R.id.button_back);
		mResetButton = (Button) this.findViewById(R.id.button_reset);
		mSaveButton = (Button) this.findViewById(R.id.button_save);
		mPhotoView = (ImageView) this.findViewById(R.id.new_quick_call_photo_view);
		mNameView = (TextView) this.findViewById(R.id.new_quick_call_name_view);
		mNumberView = (TextView) this.findViewById(R.id.new_quick_call_number_view);
		
		mNameView.setText(TextUtils.isEmpty(mDisplayName) ? mNumber : mDisplayName);
		mNumberView.setText(TextUtils.isEmpty(mDisplayName) ? "" : mNumber);
		try{
			Bitmap bitmap = BitmapUtils.toRoundBitmap(ContactsUtils.getContactInfoPhoto(this, mPhotoId));
			mPhotoView.setImageBitmap(bitmap);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
		listenerButton();
		
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
	public void finish(){
		super.finish();
		this.overridePendingTransition(R.anim.back_in_left, R.anim.back_out_right);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		
		super.leakCleanUpRootView();
	}
	
	private void listenerButton(){
		mLocusPassWordView.setOnCompleteListener(new OnCompleteListener(){

			@Override
			public void onComplete(String password) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "Trace finish, password = " + password);
				}
				
				mQuickCallStr = password;
				
			}
			
		});
		
		mBackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mResetButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLocusPassWordView.clearPassword();
				mQuickCallStr = null;
			}
		});
		
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLocusPassWordView.clearPassword();
				if(TextUtils.isEmpty(mQuickCallStr)){
					Toast.makeText(CreateQuickCallActivity.this, R.string.no_quick_call, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(QuickCallUtils.isQuickTraceExist(CreateQuickCallActivity.this, mQuickCallStr)){
					String title = CreateQuickCallActivity.this.getString(R.string.dialog_exist_title);
					QuickCallInfo info = QuickCallUtils.getQuickCallInfo(CreateQuickCallActivity.this, mQuickCallStr);
					if(info == null){
						return;
					}
					String msg = TextUtils.isEmpty(info.name) ? info.number : (info.name + ":" + info.number);
					String message = CreateQuickCallActivity.this.getString(R.string.dialog_exist_msg, msg);
					DialogUtils.showQuickCallAlertDialog(CreateQuickCallActivity.this, title, message);
				} else {
					QuickCallUtils.saveQuickCall(CreateQuickCallActivity.this, mDisplayName, mNumber, mPhotoId, mQuickCallStr);
					CreateQuickCallActivity.this.setResult(RESULT_SUCCESS);
					CreateQuickCallActivity.this.finish();
					Toast.makeText(CreateQuickCallActivity.this, R.string.create_quick_call_success, Toast.LENGTH_SHORT).show();
					
				}
			}
		});
		
	}
	
}
