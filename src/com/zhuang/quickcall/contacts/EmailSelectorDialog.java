/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.EmailSender;
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class EmailSelectorDialog {

	private static final String TAG = "[EASIIO] EmailSelectorDialog";
	
	private static final String DISPLAY_ADDRESS = "DISPLAY_ADDRESS";
	private static final String TYPE = "TYPE";
	
	private Context mContext;
	private List<EmailContact> mEmailList;
	private SimpleAdapter mEmailAdapter;
	private List<HashMap<String, String>> mFillMaps;
	
	private QuickCallAlertDialog mQuickCallDialog;
	
	public EmailSelectorDialog(Context context, List<EmailContact> list) {
		this.mContext = context;
		this.mEmailList = list;
		mFillMaps = new ArrayList<HashMap<String, String>>();
		for (EmailContact mail : list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(DISPLAY_ADDRESS, mail.emailAddress);
			map.put(TYPE, mail.emailTag);
			mFillMaps.add(map);
		}
		
		String[] from = new String[] { DISPLAY_ADDRESS, TYPE };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
		mEmailAdapter = new SimpleAdapter(context, mFillMaps, R.layout.dialog_phone_selector_item, from, to);
		
		ListView listView = new ListView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		listView.setLayoutParams(params);
		listView.setAdapter(mEmailAdapter);
		Drawable dividerDrawable = mContext.getResources().getDrawable(R.drawable.bg_listview_divider);
		listView.setDivider(dividerDrawable);
		listView.setOnItemClickListener(onItemClickListener);
		
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setIcon(R.drawable.icon_dialog_title_for_menu);
		builder.setTitle(R.string.select_email_address);
		builder.setContentView(listView);
		mQuickCallDialog = builder.create();
		mQuickCallDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mContext = null;
				mFillMaps = null;
				mEmailList = null;
			}
		});
	}
	
	public void show() {
		if (mFillMaps == null || mFillMaps.isEmpty() || mQuickCallDialog == null) {
			return;
		}
		mQuickCallDialog.show();
		DialogUtils.setDialogWidth(mContext, mQuickCallDialog);
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			mQuickCallDialog.dismiss();
			EmailContact mail = mEmailList.get(position);
			
			if(LogLevel.DEV){
				DevLog.d(TAG, "onClick mail : " + mail.emailAddress);
			}
			
			EmailSender.sendEmail(mContext, new String[]{mail.emailAddress}, null, null);
		}
	};
	
}
