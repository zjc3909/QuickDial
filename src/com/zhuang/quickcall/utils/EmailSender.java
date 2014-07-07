/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils;

import java.io.File;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.logging.MarketLog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailSender {
	private static final String TAG = "[ZHUANG]EmailSender";
    private static final String PLAIN_TEXT = "plain/text";
    private Context mContext;

    public EmailSender(Context ctx) {
        mContext = ctx;
    }

    public static void sendEmail(Context context, String to[], String subject, String body){
    	 final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
         emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
         emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
         emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
         emailIntent.setType(PLAIN_TEXT);
         context.startActivity(Intent.createChooser(emailIntent, context.getResources().getString(R.string.menu_sendEmail)));
    }
    
    public void sendEmail(String to[], String subject, String body, String attachementFilePath) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        if (attachementFilePath != null) {
            Uri attachmentUri = null;
            try {
                File file = new File(attachementFilePath);
                if (file == null) {
                    if (LogLevel.MARKET) {
                        MarketLog.w(TAG, "File error: " + attachementFilePath);
                    }
                } else if (!file.exists()) {
                    if (LogLevel.MARKET) {
                    	MarketLog.w(TAG, "File does not exist: " + attachementFilePath);
                    }
                } else if (!file.canRead()) {
                    if (LogLevel.MARKET) {
                    	MarketLog.w(TAG, "File can't be read: " + attachementFilePath);
                    }
                } else if (!file.isFile()) {
                    if (LogLevel.MARKET) {
                    	MarketLog.w(TAG, "Invalid file: " + attachementFilePath);
                    }
                } else {
                    attachmentUri = Uri.fromFile(file);
                    if (LogLevel.MARKET) {
                    	MarketLog.i(TAG, "Attachement path[size=" + file.length() + "]: " + attachementFilePath);
                    	MarketLog.i(TAG, "Attachement URI: " + attachmentUri.toString());
                    }
                }
            } catch (java.lang.Throwable ex) {
                if (LogLevel.MARKET) {
                	MarketLog.w(TAG, "Error: " + ex.toString());
                }
            }

            if (attachmentUri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
            }
        }
        emailIntent.setType(PLAIN_TEXT);
        mContext.startActivity(Intent.createChooser(emailIntent, mContext.getResources().getString(R.string.menu_sendEmail)));
    }
}
