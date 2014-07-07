/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils.widgets;

import com.zhuang.quickcall.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ContactInfoItem
 * @author gavin.zhuang
 */
public class ContactInfoItem extends LinearLayout{

	public Context mContext;
	
	public static final int TYPE_PHONE = 0;
	public static final int TYPE_EMAIL = 1;
	
	private TextView mTypeView;
	private TextView mContentView;
	private ImageButton mCallButton;
	private ImageButton mMessageButton;
	
	private int mType;
	
	private OnClickItemButtonListener mOnClickItemButtonListener;
	
	public ContactInfoItem(Context context){
		super(context);
		
	}
	
	public ContactInfoItem(Context context, int type, OnClickItemButtonListener listener) {
		super(context);
		mContext = context;
		mType = type;
		mOnClickItemButtonListener = listener;
		init();
	}
	
	public void setContentText(String str){
		if(!TextUtils.isEmpty(str)){
			mContentView.setText(str);
		}
	}
	
	public void setTypeText(String str){
		if(!TextUtils.isEmpty(str)){
			mTypeView.setText(str + ":");
		}
	}
	
	private void init(){
		if(mContext == null){
			return;
		}
		inflate(mContext, R.layout.contact_info_list_item_layout, this);
		mTypeView = (TextView) this.findViewById(R.id.type_textview);
		mContentView = (TextView) this.findViewById(R.id.content_view);
		mCallButton = (ImageButton) this.findViewById(R.id.button_contact_info_call);
		mMessageButton = (ImageButton) this.findViewById(R.id.button_contact_info_message);
		
		if(mType == TYPE_PHONE){
			mCallButton.setVisibility(View.VISIBLE);
			mMessageButton.setVisibility(View.VISIBLE);
			mCallButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mOnClickItemButtonListener != null){
						mOnClickItemButtonListener.onClickCallButton();
					}
				}
			});
			
			mMessageButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mOnClickItemButtonListener != null){
						mOnClickItemButtonListener.onClickMessageButton();
					}
				}
			});
			
		} else {
			mCallButton.setVisibility(View.GONE);
			mMessageButton.setVisibility(View.GONE);
		}
		
	}

	public interface OnClickItemButtonListener{
		public void onClickCallButton();
		public void onClickMessageButton();
	}
	
}
