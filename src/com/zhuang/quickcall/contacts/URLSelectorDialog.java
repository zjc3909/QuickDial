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
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.utils.DialogUtils;
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class URLSelectorDialog {

	private static final String TAG = "[EASIIO]URLSelectorDialog";
	
	private static final String DISPLAY_ADDRESS = "DISPLAY_ADDRESS";
	private static final String TYPE = "TYPE";
	
	private Context mContext;
	private List<URLContact> mUrlList;
	private SimpleAdapter mUrlAdapter;
	private List<HashMap<String, String>> mFillMaps;
	
	private QuickCallAlertDialog mEasiioAlertDialog;
	
	public URLSelectorDialog(Context context, List<URLContact> list) {
		this.mContext = context;
		this.mUrlList = list;
		mFillMaps = new ArrayList<HashMap<String, String>>();
		for (URLContact url : list) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(DISPLAY_ADDRESS, url.url_address);
			map.put(TYPE, url.url_tag);
			mFillMaps.add(map);
		}
		
		String[] from = new String[] { DISPLAY_ADDRESS, TYPE };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
		mUrlAdapter = new SimpleAdapter(context, mFillMaps, R.layout.dialog_phone_selector_item, from, to);
		
		ListView listView = new ListView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		listView.setLayoutParams(params);
		listView.setAdapter(mUrlAdapter);
		Drawable dividerDrawable = mContext.getResources().getDrawable(R.drawable.bg_listview_divider);
		listView.setDivider(dividerDrawable);
		listView.setOnItemClickListener(onItemClickListener);
		
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setIcon(R.drawable.icon_dialog_title_for_menu);
		builder.setTitle(R.string.select_url_address);
		builder.setContentView(listView);
		mEasiioAlertDialog = builder.create();
		mEasiioAlertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mContext = null;
				mFillMaps = null;
				mUrlList = null;
			}
		});
	}
	
	public void show() {
		if (mFillMaps == null || mFillMaps.isEmpty() || mEasiioAlertDialog == null) {
			return;
		}
		mEasiioAlertDialog.show();
		DialogUtils.setDialogWidth(mContext, mEasiioAlertDialog);
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			mEasiioAlertDialog.dismiss();
			URLContact url = mUrlList.get(position);
			
			if(LogLevel.DEV){
				DevLog.d(TAG, "onClick url : " + url.url_address);
			}
			
			try{
				Uri uri = Uri.parse(formatURL(url.url_address));  
				Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				mContext.startActivity(it);
			} catch (Exception e) {
				if(LogLevel.MARKET){
					MarketLog.e(TAG, "openURLAddress failed: url = " + url.url_address);
				}
			}
			
		}
	};
	
	public static String formatURL(String str){
		if(TextUtils.isEmpty(str)){
			return null;
		}
		
		if(str.startsWith("http://") || str.startsWith("https://")){
			return str;
		} else {
			return "http://" + str;
		}
		
	}
	
}
