/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils.widgets;

import com.zhuang.quickcall.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * QuickCallAlertDialog
 * @author gavin.zhuang
 */
public class QuickCallAlertDialog extends Dialog{

    public QuickCallAlertDialog(Context context) {
        super(context, R.style.QuickCallDialog);
    }
 
    public static class Builder {
 
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mPosButtonText;
        private String mNegButtonText;
        private int mIconRes;
        private View mContentView;
 
        private DialogInterface.OnClickListener mPosButtonClickListener;
        private DialogInterface.OnClickListener mNegButtonClickListener;
 
        public Builder(Context context) {
            this.mContext = context;
            this.mIconRes = 0;
        }
        
        public void setMessage(String message) {
            this.mMessage = message;
        }
 
        public void setMessage(int message) {
            this.mMessage = mContext.getResources().getString(message);
        }
 
        public void setTitle(String title) {
            this.mTitle = title;
        }
        
        public void setTitle(int title) {
            this.mTitle = mContext.getResources().getString(title);
        }
 
        public void setContentView(View v) {
            this.mContentView = v;
        }
 
        public void setIcon(int icon){
        	this.mIconRes = icon;
        }
        
        public void setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.mPosButtonText = mContext.getResources().getString(positiveButtonText);
            this.mPosButtonClickListener = listener;
        }
 
        public void setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.mPosButtonText = positiveButtonText;
            this.mPosButtonClickListener = listener;
        }
 
        public void setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.mNegButtonText = mContext.getResources().getString(negativeButtonText);
            this.mNegButtonClickListener = listener;
        }
        
        public void setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.mNegButtonText = negativeButtonText;
            this.mNegButtonClickListener = listener;
        }
        
        public QuickCallAlertDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final QuickCallAlertDialog dialog = new QuickCallAlertDialog(mContext);
            View layout = inflater.inflate(R.layout.quick_call_alert_dialog_layout, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            TextView titleView = (TextView) layout.findViewById(R.id.title);
            TextView messageView = (TextView) layout.findViewById(R.id.message);
            LinearLayout contentLayout = (LinearLayout) layout.findViewById(R.id.content);
            Button posButton = (Button) layout.findViewById(R.id.positiveButton);
            Button negButton = (Button) layout.findViewById(R.id.negativeButton);
            ImageView iconView = (ImageView) layout.findViewById(R.id.icon);
            View buttonView = layout.findViewById(R.id.dialog_button_layout);
            
            if(mIconRes > 0){
            	iconView.setVisibility(View.VISIBLE);
            	iconView.setImageResource(mIconRes);
            } else {
            	iconView.setVisibility(View.GONE);
            }
            
            if(!TextUtils.isEmpty(mTitle)){
            	layout.findViewById(R.id.dialog_title_layout).setVisibility(View.VISIBLE);
            	titleView.setText(mTitle);
            	titleView.setVisibility(View.VISIBLE);
            } else {
            	titleView.setVisibility(View.GONE);
            	layout.findViewById(R.id.line_view).setVisibility(View.GONE);
            	layout.findViewById(R.id.dialog_title_layout).setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mPosButtonText)) {
				posButton.setText(mPosButtonText);
				if (mPosButtonClickListener != null) {
					posButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mPosButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					});
				}
            } else {
            	posButton.setVisibility(View.GONE);
            }
            
            if (!TextUtils.isEmpty(mNegButtonText)) {
            	negButton.setText(mNegButtonText);
				if (mNegButtonClickListener != null) {
					negButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							mNegButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					});
				}
            } else {
            	negButton.setVisibility(View.GONE);
            }
            
            if(TextUtils.isEmpty(mPosButtonText) && TextUtils.isEmpty(mNegButtonText)){
            	buttonView.setVisibility(View.GONE);
            }
            
            if (!TextUtils.isEmpty(mMessage)) {
            	messageView.setText(mMessage);
            } else if (mContentView != null) {
            	contentLayout.removeAllViews();
            	contentLayout.addView(mContentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            dialog.setCanceledOnTouchOutside(true);		
    		dialog.setCancelable(true);
            return dialog;
        }
 
    }
 
}