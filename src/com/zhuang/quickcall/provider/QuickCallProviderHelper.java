/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.provider;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.DevLog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * QuickCallProviderHelper
 * @author gavin.zhuang
 */
public class QuickCallProviderHelper {

	private static final String TAG = "[ZHUANG]QuickCallProviderHelper";
	
	public static int updateSingleValue(Context context, String uri_path, String column, String value, String where) {
        Uri uri = UriHelper.getUri(uri_path);
        ContentValues values = new ContentValues();
        values.put(column, value);
        return context.getContentResolver().update(uri, values, where, null);   //returns the number of updated rows
    }
	
    public static String simpleQuery(Context context, String uri_path, String column ) {
        Uri uri = UriHelper.getUri(uri_path);
        return simpleQuery(context, uri, column, null);
    }

    private static String simpleQuery(Context context, Uri uri, String column, String selection) {
    	if (context == null) {
    		return "";    		
    	}
    	
        if (LogLevel.DEV) {
        	DevLog.d(TAG, "simpleQuery(" + uri + ") " + ( selection == null ? "" : " selection: " + selection ) );
        }

        try{
        	Cursor cursor = context.getContentResolver().query(uri, new String[]{column}, selection, null, null);
            if (cursor == null) {
                if (LogLevel.DEV) {
                	DevLog.d(TAG, "simpleQuery(): null cursor received; return \"\"");
                }
                return "";
            }

            if (!cursor.moveToFirst()) {
                if (LogLevel.DEV) {
                    DevLog.d(TAG, "simpleQuery(): empty cursor received; return \"\", count: " + cursor.getCount());
                }
                cursor.close();
                return "";
            }

            String result = cursor.getString(0);
            if (result == null) {
                if (LogLevel.DEV){
                	DevLog.d(TAG, "simpleQuery(): cursor returned null; return \"\"");
                }
                result = "";
            }

            cursor.close();
            return result;
        } catch(Exception e){
        	if(LogLevel.DEV){
        		DevLog.e(TAG, "simpleQuery ERROR : " + e.toString());
        	}
        	return null;
        }
        
    }
}
