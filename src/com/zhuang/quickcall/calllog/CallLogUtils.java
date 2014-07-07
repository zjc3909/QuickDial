package com.zhuang.quickcall.calllog;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.DevLog;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public class CallLogUtils {

	private static final String TAG = "[ZHUANG]CallLogUtils";
	
	public static final Uri CALL_LOG_CONTENT_URI = CallLog.Calls.CONTENT_URI;
	
	public static final String[] SUMMARY_PROJECTION = new String[]{
		CallLog.Calls._ID,
		CallLog.Calls.CACHED_NAME,
		CallLog.Calls.CACHED_NUMBER_LABEL,
		CallLog.Calls.CACHED_NUMBER_TYPE,
		CallLog.Calls.NUMBER,
		CallLog.Calls.TYPE,
		CallLog.Calls.DURATION,
		CallLog.Calls.DATE,
	};
	
	public static final int ID_INDEX = 0;
	public static final int CACHED_NAME_INDEX = 1;
	public static final int CACHED_NUMBER_LABEL_INDEX = 2;
	public static final int CACHED_NUMBER_TYPE_INDEX = 3;
	public static final int NUMBER_INDEX = 4;
	public static final int TYPE_INDEX = 5;
	public static final int DURATION_INDEX = 6;
	public static final int DATE_INDEX = 7;
	
	public static final String DEFAULT_SORT_ORDER = CallLog.Calls.DEFAULT_SORT_ORDER;
	
	public static CallLogInfo readCallLogInfoByCursor(Cursor cursor){
	
		if(cursor == null || cursor.isClosed()){
			return null;
		}
		
		try {
			CallLogInfo info = new CallLogInfo();
			info._id = cursor.getLong(ID_INDEX);
			info.name = cursor.getString(CACHED_NAME_INDEX);
			info.number_label = cursor.getString(CACHED_NUMBER_LABEL_INDEX);
			info.number_type = cursor.getString(CACHED_NUMBER_TYPE_INDEX);
			info.number = cursor.getString(NUMBER_INDEX);
			info.type = cursor.getInt(TYPE_INDEX);
			info.duration = cursor.getLong(DURATION_INDEX);
			info.date = cursor.getLong(DATE_INDEX);
			
			return info;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static CallLogInfo queryCallLogInfoByNumber(Context context, String number){
		if(LogLevel.DEV){
			DevLog.d(TAG, "queryCallLogInfoByNumber number = " + number);
		}
		Cursor cursor = null;
		try {
			String where = CallLog.Calls.NUMBER + " = '" + number + "'";
			cursor = context.getContentResolver().query(CALL_LOG_CONTENT_URI, SUMMARY_PROJECTION, null, null, DEFAULT_SORT_ORDER);
			if(cursor == null || !cursor.moveToFirst()){
				return null;
			}
			return readCallLogInfoByCursor(cursor);
		} catch (Exception e){
			e.printStackTrace();
			return null;
		} finally {
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
		}
		
	}
	
	public static Cursor getCallLogCursorByNumber(Context context, String number){
		if(LogLevel.DEV){
			DevLog.d(TAG, "getCallLogCursorByNumber number = " + number);
		}
		try {
			String where = CallLog.Calls.NUMBER + " = '" + number + "'";
			Cursor cursor = context.getContentResolver().query(CALL_LOG_CONTENT_URI, SUMMARY_PROJECTION, where, null, DEFAULT_SORT_ORDER);
			if(cursor == null || !cursor.moveToFirst()){
				return null;
			}
			return cursor;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		} 
		
	}
	
	public static void deleteSingleCallLog(Context context, long _id){
		if(LogLevel.DEV){
			DevLog.d(TAG, "deleteSingleCallLog _id = " + _id);
		}
		try {
			String where = CallLog.Calls._ID + " = '" + _id + "'";
			int rows = context.getContentResolver().delete(CALL_LOG_CONTENT_URI, where, null);
			if(LogLevel.DEV){
				DevLog.d(TAG, "deleteSingleCallLog rows = " + rows);
			}
		} catch (Exception e){
			e.printStackTrace();
		} 
	}
	
	public static void deleteCallLogOfThisNumber(Context context, String number){
		if(LogLevel.DEV){
			DevLog.d(TAG, "deleteCallLogOfThisNumber number = " + number);
		}
		try {
			String where = CallLog.Calls.NUMBER + " = '" + number + "'";
			int rows = context.getContentResolver().delete(CALL_LOG_CONTENT_URI, where, null);
			if(LogLevel.DEV){
				DevLog.d(TAG, "deleteCallLogOfThisNumber rows = " + rows);
			}
		} catch (Exception e){
			e.printStackTrace();
		} 
	}
	
}
