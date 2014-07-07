package com.zhuang.quickcall.quickcall;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
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
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;
import com.zhuang.quickcall.utils.widgets.LocusPassWordView.OnCompleteListener;

public class QuickCallChangeActivity extends QuickCallActivity {

	private static final String TAG = "[ZHUANG]QuickCallChangeActivity";
	
	private LocusPassWordView mLocusPassWordView;
	private ImageButton mBackButton;
	private ImageButton mDeleteButton;
	private Button mResetButton;
	private Button mUpdateButton;
	
	private ImageView mPhotoView;
	private TextView mNameView;
	private TextView mNumberView;
	
	private QuickCallInfo mQuickCallInfo;
	private long mQuickCallId;
	private String mQuickCallStr;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.DEV){
			DevLog.i(TAG, "onCreate...");
		}
		this.setContentView(R.layout.quick_call_change_layout);
		
		Intent intent = this.getIntent();
		if(intent == null){
			finish();
			return;
		}
		
		mQuickCallId  = intent.getLongExtra(QuickCallConstants.EXTRA_SELECT_QUICK_CALL_ID, -1);
		if(mQuickCallId < 0){
			finish();
			return;
		}
		mQuickCallInfo = QuickCallUtils.getQuickCallInfo(this, mQuickCallId);
		if(mQuickCallInfo == null || TextUtils.isEmpty(mQuickCallInfo.trace)){
			finish();
			return;
		}
		
		if(LogLevel.DEV){
			DevLog.i(TAG, "mQuickCallInfo = " + mQuickCallInfo.toString());
		}
		
		mQuickCallStr = mQuickCallInfo.trace;
		mLocusPassWordView = (LocusPassWordView) this.findViewById(R.id.quick_call_word_view);
		mBackButton = (ImageButton) this.findViewById(R.id.button_back);
		mDeleteButton = (ImageButton) this.findViewById(R.id.button_delete);
		mResetButton = (Button) this.findViewById(R.id.button_reset);
		mUpdateButton = (Button) this.findViewById(R.id.button_update);
		mPhotoView = (ImageView) this.findViewById(R.id.new_quick_call_photo_view);
		mNameView = (TextView) this.findViewById(R.id.new_quick_call_name_view);
		mNumberView = (TextView) this.findViewById(R.id.new_quick_call_number_view);
		
		mNameView.setText(TextUtils.isEmpty(mQuickCallInfo.name) ? mQuickCallInfo.number : mQuickCallInfo.name);
		mNumberView.setText(TextUtils.isEmpty(mQuickCallInfo.name) ? "" : mQuickCallInfo.number);
		try{
			Bitmap bitmap = BitmapUtils.toRoundBitmap(ContactsUtils.getContactInfoPhoto(this, mQuickCallInfo.photoId));
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
		
		mLocusPassWordView.drawTrace(mQuickCallInfo.trace);
		
		mBackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDeleteDialog(mQuickCallId);
			}
		});
		
		mResetButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mLocusPassWordView.resetPassword();
				mQuickCallStr = null;
			}
		});
		
		mUpdateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(mQuickCallStr)){
					Toast.makeText(QuickCallChangeActivity.this, R.string.no_quick_call, Toast.LENGTH_SHORT).show();
					return;
				}
				if(mQuickCallStr.equals(mQuickCallInfo.trace)){
					Toast.makeText(QuickCallChangeActivity.this, R.string.quick_call_no_change, Toast.LENGTH_SHORT).show();
					return;
				}
				mLocusPassWordView.clearPassword();
				if(QuickCallUtils.isQuickTraceExist(QuickCallChangeActivity.this, mQuickCallStr)){
					String title = QuickCallChangeActivity.this.getString(R.string.dialog_exist_title);
					QuickCallInfo info = QuickCallUtils.getQuickCallInfo(QuickCallChangeActivity.this, mQuickCallStr);
					if(info == null){
						return;
					}
					String msg = TextUtils.isEmpty(info.name) ? info.number : (info.name + ":" + info.number);
					String message = QuickCallChangeActivity.this.getString(R.string.dialog_exist_msg, msg);
					DialogUtils.showQuickCallAlertDialog(QuickCallChangeActivity.this, title, message);
				} else {
					QuickCallUtils.updateQuickCallTrace(QuickCallChangeActivity.this, mQuickCallId, mQuickCallStr);
					QuickCallChangeActivity.this.finish();
					Toast.makeText(QuickCallChangeActivity.this, R.string.update_quick_call_success, Toast.LENGTH_SHORT).show();
					
				}
			}
		});
		
	}
	
	private void showDeleteDialog(final long _id){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setTitle(R.string.delete_contact);
		builder.setMessage(R.string.delete_quick_message);
		builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				try {
					QuickCallUtils.deleteQuickCallById(QuickCallChangeActivity.this, _id);
					finish();
				} catch (SQLiteException e) {
					if (LogLevel.DEV) {
						DevLog.e(TAG, "Error deleting quick call.", e);
					} else if (LogLevel.MARKET) {
						MarketLog.e(TAG, "Cannot delete quick call: SQLite error.");
					}
					DialogUtils.showQuickCallAlertDialog(QuickCallChangeActivity.this, R.string.delete_contact_failed_title, R.string.delete_contact_failed);
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		DialogUtils.setDialogWidth(this, dialog);
	}
	
}
