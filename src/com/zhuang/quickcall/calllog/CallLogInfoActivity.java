package com.zhuang.quickcall.calllog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.activity.QuickCallActivity;
import com.zhuang.quickcall.contacts.ContactBinding;
import com.zhuang.quickcall.contacts.ContactInfo;
import com.zhuang.quickcall.contacts.ContactInfoActivity;
import com.zhuang.quickcall.contacts.ContactsUtils;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.quickcall.CreateQuickCallActivity;
import com.zhuang.quickcall.quickcall.QuickCallChangeActivity;
import com.zhuang.quickcall.quickcall.QuickCallInfo;
import com.zhuang.quickcall.quickcall.QuickCallUtils;
import com.zhuang.quickcall.utils.BitmapUtils;
import com.zhuang.quickcall.utils.DateTimeUtils;
import com.zhuang.quickcall.utils.DialUtils;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;

public class CallLogInfoActivity extends QuickCallActivity{

	private static final String TAG = "[ZHUANG]CallLogInfoActivity";
	
	private CallLogInfo mCallLogInfo;
	
	private ImageButton mBackButton;
	private TextView mNameView;
	private TextView mContactView;
	private ImageView mPhotoView;
	private ImageButton mMenuButton;
	private Button mCallButton;
	private Button mSendMessageButton;
	private ListView mHistoryListView;
	
	private ContactBinding mContactBinding;
	
	private HistoryListAdapter mAdapter;
	
	private PopupWindow mPopupWindow;
	
	private String mCallLogNumber;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.DEV){
			DevLog.i(TAG, "onCreate...");
		}
		
		mCallLogNumber = getIntent().getStringExtra(QuickCallConstants.EXTRA_CALL_LOG_NUMBER);
		if(TextUtils.isEmpty(mCallLogNumber)){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "onCreate, contact is null, finish.");
			}
			finish();
			return;
		}
		
		mCallLogInfo = CallLogUtils.queryCallLogInfoByNumber(this, mCallLogNumber);
		if(mCallLogInfo == null){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "onCreate, failed mCallLogInfo is null.");
			}
			finish();
			return;
		}
		
		this.setContentView(R.layout.history_info_layout);
		
		buildLayout();
		
		mAdapter = new HistoryListAdapter(this);
		mHistoryListView.setAdapter(mAdapter);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(LogLevel.DEV){
			DevLog.i(TAG, "onResume...");
		}
		StatService.onResume(this);
		
		mCallLogInfo = CallLogUtils.queryCallLogInfoByNumber(this, mCallLogNumber);
		if(mCallLogInfo == null){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "onCreate, failed mCallLogInfo is null.");
			}
			finish();
			return;
		}
		
		if(mPopupWindow != null){
			mPopupWindow = null;
		}
		refreshContactInfo();
		refreshCursor();
		
		initPopupWindow();
		
	}
	
	private void buildLayout(){
		mBackButton = (ImageButton) this.findViewById(R.id.button_back);
		mNameView = (TextView) this.findViewById(R.id.name_view);
		mContactView = (TextView) this.findViewById(R.id.contact_view);
		mPhotoView = (ImageView) this.findViewById(R.id.photo_view);
		mMenuButton = (ImageButton) this.findViewById(R.id.button_little_edit);
		mCallButton = (Button) this.findViewById(R.id.button_history_detail_call_contact);
		mSendMessageButton = (Button) this.findViewById(R.id.button_history_detail_send_message);
		mHistoryListView = (ListView) this.findViewById(R.id.history_listview);
		
		onUserActionListener();
	}
	
	private void onUserActionListener(){
		
		mBackButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mMenuButton.setOnClickListener(onMenuButtonClickListener);
		
		mCallButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(LogLevel.MARKET){
					MarketLog.i(TAG, "User click call button.");
				}
				DialUtils.callNumber(CallLogInfoActivity.this, mCallLogNumber);
			}
		});
		
		mSendMessageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(LogLevel.MARKET){
					MarketLog.i(TAG, "User click send message button.");
				}
				DialUtils.sendMessage(CallLogInfoActivity.this, mCallLogNumber);
			}
		});
	}
	
	private void refreshContactInfo(){
		mContactBinding = ContactsUtils.bindContactByNumber(this, mCallLogNumber);
		if(mContactBinding != null && mContactBinding.hasContact){
			mNameView.setText(mContactBinding.displayName);
			mContactView.setText(mCallLogNumber);
			try{
				Bitmap photo = ContactsUtils.getContactInfoPhoto(this, mContactBinding.photoId);
				if (photo != null) {
					photo = BitmapUtils.toRoundBitmap(photo);
					mPhotoView.setImageBitmap(photo);
				} else {
					mPhotoView.setImageResource(R.drawable.icon_contact_list_default_round_photo);
				}
			} catch (Exception e){
				MarketLog.e(TAG, "set photo failed : " + e.toString());
			}
			
		} else {
			mNameView.setText(mCallLogNumber);
			mContactView.setText(R.string.unsaved);
			mPhotoView.setImageResource(R.drawable.icon_contact_list_default_round_photo);
		}
		
		initPopupWindow();
	}
	
	private View.OnClickListener onMenuButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (LogLevel.MARKET) {
				MarketLog.d(TAG, "User click menu history button.");
			}
			if (mPopupWindow != null) {
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				} else {
					mPopupWindow.showAsDropDown(v);
				}
			}
			
		}
	};
	
	private void openContactInfo(){
		Intent intent = new Intent(this, ContactInfoActivity.class);
		intent.putExtra(QuickCallConstants.EXTRA_CONTACT_PERSON_ID, mContactBinding.contact_id);
		this.startActivity(intent);
	}
	
	private void refreshCursor(){
		Cursor cursor = CallLogUtils.getCallLogCursorByNumber(this, mCallLogNumber);
		mAdapter.changeCursor(cursor);
		
	}
	
	private class HistoryItem{
		ImageView typeView;
		TextView timeView;
		TextView lengthView;
		
	}
	
	private class HistoryListAdapter extends ResourceCursorAdapter{

		public HistoryListAdapter(Context context) {
			super(context, R.layout.history_detail_item_layout, null);
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = super.newView(context, cursor, parent);
			HistoryItem cache = new HistoryItem();
			cache.typeView = (ImageView) view.findViewById(R.id.type_img);
			cache.timeView = (TextView) view.findViewById(R.id.time_view);
			cache.lengthView = (TextView) view.findViewById(R.id.content_view);
			view.setTag(cache);
			
			return view;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			try {
				HistoryItem itemView = (HistoryItem) view.getTag();
				CallLogInfo info = CallLogUtils.readCallLogInfoByCursor(cursor);
				itemView.timeView.setText(DateTimeUtils.getDateTimeLabel(info.date));
				itemView.lengthView.setText(getLength(info.duration));
				if(info.type == CallLog.Calls.INCOMING_TYPE){
					itemView.typeView.setImageResource(R.drawable.icon_history_incoming);
				} else if (info.type == CallLog.Calls.MISSED_TYPE){
					itemView.typeView.setImageResource(R.drawable.icon_history_missed);
				} else if (info.type == CallLog.Calls.OUTGOING_TYPE){
					itemView.typeView.setImageResource(R.drawable.icon_history_outbound);
				} else {
					itemView.typeView.setImageResource(R.drawable.icon_history_cancel);
				}
			} catch (java.lang.Throwable error) {
				if (LogLevel.MARKET) {
					MarketLog.e(TAG, "bindView() error", error);
				}
			}
		}
		
	}
	
	private String getLength(long duration){
		long sec = 1;
        long min = sec * 60;
        long hour = min * 60;
          
        long hours = duration / hour;
        long mins = (duration - hours * hour) / min;
        long secs = (duration - hours * hour - mins * min) / sec;
          
        String strHour = hours < 10 ? "0"+hours : ""+hours;
        String strMin = mins < 10 ? "0"+mins : ""+mins;
        String strSec = secs < 10 ? "0"+secs : ""+secs;
          
        return (strHour +":" + strMin + ":" + strSec);
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
	
		if(mContactBinding != null){
			mContactBinding = null;
		}
		
		if(mAdapter != null){
			mAdapter = null;
		}
		
		leakCleanUpRootView();
	}
	
	private void initPopupWindow(){
		LayoutInflater inflater = LayoutInflater.from(this); 
		View view = inflater.inflate(R.layout.popup_of_history_info, null); 
		View buttonCreateQuick = view.findViewById(R.id.button_popup_create_new_quick_call);
		View buttonView = view.findViewById(R.id.button_popup_view);
		View buttonCreate = view.findViewById(R.id.button_popup_create);
		View buttonAdd = view.findViewById(R.id.button_popup_add);
		View buttonClear = view.findViewById(R.id.button_popup_clear_all);
		
		if(mContactBinding != null && mContactBinding.hasContact){
			buttonView.setVisibility(View.VISIBLE);
			buttonCreate.setVisibility(View.GONE);
			buttonAdd.setVisibility(View.GONE);
		} else {
			buttonView.setVisibility(View.GONE);
			buttonCreate.setVisibility(View.VISIBLE);
			buttonAdd.setVisibility(View.VISIBLE);
		}
		
		buttonCreateQuick.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				createNewQuickCall();
			}
		});
		
		buttonView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				openContactInfo();
			}
		});
		
		buttonCreate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				if(LogLevel.DEV){
					DevLog.d(TAG, "user click create new contact.");
				}
				try {
					startActivity(ContactsUtils.getCreateNewContactIntent(CallLogInfoActivity.this, null, mCallLogNumber));
				} catch (ActivityNotFoundException e) {
					if (LogLevel.MARKET) {
						MarketLog.e(TAG, "create new contact start activity error: " + e.toString());
					}
				}
			}
		});
		
		buttonAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				if(LogLevel.DEV){
					DevLog.d(TAG, "user click add to existing contact.");
				}
				try {
					startActivity(ContactsUtils.getAddToContactIntent(CallLogInfoActivity.this, mCallLogNumber));
				} catch (ActivityNotFoundException e) {
					if (LogLevel.MARKET) {
						MarketLog.e(TAG, "add to exsiting contact start activity error: " + e.toString());
					}
				}
			}
		});
		buttonClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				showDeleteDialog();
			}
		});
		
		mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false); 
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable()); 
		mPopupWindow.setOutsideTouchable(true); 
		mPopupWindow.setFocusable(true); 	
	}
	
	private void createNewQuickCall(){
		if(QuickCallUtils.hasQuickTraceSet(this, mCallLogNumber)){
			QuickCallInfo info = QuickCallUtils.getQuickCallInfoByNumber(this, mCallLogNumber);
			if(info != null){
				Toast.makeText(this, R.string.this_number_has_been_set, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(this, QuickCallChangeActivity.class);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_QUICK_CALL_ID, info._id);
				this.startActivity(intent);
				return;
			}
		} 
		
		Intent intent = new Intent(this, CreateQuickCallActivity.class);
		intent.putExtra(QuickCallConstants.EXTRA_SELECT_NAME, mCallLogInfo.name);
		intent.putExtra(QuickCallConstants.EXTRA_SELECT_NUMBER, mCallLogNumber);
		long contactId = ContactsUtils.lookupContactIDByPhoneNumber(mCallLogNumber, this);
		ContactInfo contactInfo = ContactsUtils.getContactInfoByContactId(this, contactId);
		if(contactInfo == null){
			Toast.makeText(this, R.string.save_first, Toast.LENGTH_LONG).show();
			return;
		}
		intent.putExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, contactInfo.photoId);
		this.startActivity(intent);
	}
	
	private void showDeleteDialog(){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setTitle(R.string.delete_history);
		builder.setMessage(R.string.delete_history_by_contact);
		builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				CallLogUtils.deleteCallLogOfThisNumber(CallLogInfoActivity.this, mCallLogNumber);
				finish();
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
