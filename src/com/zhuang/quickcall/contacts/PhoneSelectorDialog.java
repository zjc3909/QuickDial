/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.quickcall.CreateQuickCallActivity;
import com.zhuang.quickcall.quickcall.QuickCallChangeActivity;
import com.zhuang.quickcall.quickcall.QuickCallInfo;
import com.zhuang.quickcall.quickcall.QuickCallUtils;
import com.zhuang.quickcall.utils.DialUtils;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PhoneSelectorDialog {

	private static final String TAG = "[EASIIO] PhoneSelectorDialog";
	
	public static final int TYPE_CALL = 0;
	public static final int TYPE_MESSAGE = 1;
	public static final int TYPE_SELECT_CONTACT = 2;
	
	private static final String DISPLAY_NUMBER = "DISPLAY_NUMBER";
	private static final String TYPE = "TYPE";
	
	private Context mContext;
	private List<TaggedContactPhoneNumber> mPhoneList;
	private String mCallName;
	private SimpleAdapter mPhonesAdapter;
	private List<HashMap<String, String>> mFillMaps;
	private int mType;
	
	private QuickCallAlertDialog mQuickAlertDialog;
	
	public PhoneSelectorDialog(Context context, List<TaggedContactPhoneNumber> list, String callName, int type){
		this.mContext = context;
		this.mPhoneList = list;
		this.mType = type;
		this.mCallName = callName;
		
		mFillMaps = new ArrayList<HashMap<String, String>>();
		for (TaggedContactPhoneNumber num : list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(DISPLAY_NUMBER, num.originalNumber);
			map.put(TYPE, num.numberTag);
			mFillMaps.add(map);
		}
		
		String[] from = new String[] { DISPLAY_NUMBER, TYPE };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
		mPhonesAdapter = new SimpleAdapter(context, mFillMaps, R.layout.dialog_phone_selector_item, from, to);
		
		ListView listView = new ListView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		listView.setLayoutParams(params);
		listView.setAdapter(mPhonesAdapter);
		Drawable dividerDrawable = mContext.getResources().getDrawable(R.drawable.bg_listview_divider);
		listView.setDivider(dividerDrawable);
		listView.setOnItemClickListener(onItemClickListener);
		
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setIcon(R.drawable.icon_dialog_title_for_menu);
		builder.setTitle(R.string.select_phone_number);
		builder.setContentView(listView);
		mQuickAlertDialog = builder.create();
		mQuickAlertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mContext = null;
				mFillMaps = null;
				mPhoneList = null;
			}
		});
	}
	
	public void show() {
		if (mFillMaps == null || mFillMaps.isEmpty() || mQuickAlertDialog == null) {
			return;
		}
		mQuickAlertDialog.show();
		Activity activity = (Activity) mContext;
		if(activity == null){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "onClick activity is null.");
			}
			return;
		}
		DialogUtils.setDialogWidth(activity, mQuickAlertDialog);
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			mQuickAlertDialog.dismiss();
			TaggedContactPhoneNumber num = mPhoneList.get(position);
			
			if(LogLevel.DEV){
				DevLog.d(TAG, "onClick num : " + num.originalNumber + ", name : " + mCallName);
			}
			
			Activity activity = (Activity) mContext;
			if(activity == null){
				if(LogLevel.MARKET){
					MarketLog.e(TAG, "onClick activity is null.");
				}
				return;
			}
			
			if(mType == TYPE_CALL){
				DialUtils.callNumber(activity, num.originalNumber);
			} else if (mType == TYPE_MESSAGE){
				DialUtils.sendMessage(activity, num.originalNumber);
			} else if (mType == TYPE_SELECT_CONTACT){
				if(QuickCallUtils.hasQuickTraceSet(activity,  num.originalNumber)){
					QuickCallInfo info = QuickCallUtils.getQuickCallInfoByNumber(activity,  num.originalNumber);
					if(info != null){
						Toast.makeText(activity, R.string.this_number_has_been_set, Toast.LENGTH_LONG).show();
						Intent intent = new Intent(activity, QuickCallChangeActivity.class);
						intent.putExtra(QuickCallConstants.EXTRA_SELECT_QUICK_CALL_ID, info._id);
						activity.startActivity(intent);
						return;
					}
				} 
					
				Intent intent = new Intent(activity, CreateQuickCallActivity.class);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_NAME, num.displayName);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_NUMBER, num.originalNumber);
				intent.putExtra(QuickCallConstants.EXTRA_SELECT_PHOTO_ID, num.photo_id);
				activity.startActivityForResult(intent, ContactsSelectorActivity.REQUEST_CODE);
			}
		}
	};
	
}
