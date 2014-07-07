package com.zhuang.quickcall.quickcall;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.provider.QuickCallDataStore.QuickCallTable;
import com.zhuang.quickcall.provider.QuickCallProvider;
import com.zhuang.quickcall.provider.UriHelper;

public class QuickCallUtils {

	private static final String TAG = "[ZHUANG]QuickCallUtils";
	
	public static boolean isQuickTraceExist(Context context, String traceStr){
		if(LogLevel.DEV){
			DevLog.d(TAG, "isQuickTraceExist traceStr =  " + traceStr);
		}
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable.QUICK_CALL_TOUCH_TRACK + " = '" + traceStr + "'";
			Cursor cursor = context.getContentResolver().query(uri, new String[]{QuickCallTable._ID}, where, null, null);
			if(cursor == null || cursor.getCount() <= 0){
				return false;
			}
			return true;
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "isQuickTraceExist failed, e " + e.toString());
			}
			
			return true;
		}
	}
	
	public static boolean hasQuickTraceSet(Context context, String number){
		if(LogLevel.DEV){
			DevLog.d(TAG, "hasQuickTraceSet number =  " + number);
		}
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable.QUICK_CALL_NUMBER + " = '" + number + "'";
			Cursor cursor = context.getContentResolver().query(uri, new String[]{QuickCallTable._ID}, where, null, null);
			if(cursor == null || cursor.getCount() <= 0){
				return false;
			}
			return true;
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "hasQuickTraceSet failed, e " + e.toString());
			}
			
			return true;
		}
	}
	
	public static QuickCallInfo getQuickCallInfoByCursor(Cursor cursor){
		try{
			QuickCallInfo info = new QuickCallInfo();
			info._id = cursor.getLong(QuickCallProjection.ID_INDEX);
			info.name = cursor.getString(QuickCallProjection.NAME_INDEX);
			info.number = cursor.getString(QuickCallProjection.NUMBER_INDEX);
			info.photoId = cursor.getLong(QuickCallProjection.PHOTO_ID_INDEX);
			info.trace = cursor.getString(QuickCallProjection.TOUCH_TRACE_INDEX);
			info.createTime = cursor.getLong(QuickCallProjection.CREATE_TIME_INDEX);
			
			return info;
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "getQuickCallInfo failed, e " + e.toString());
			}
			return null;
		}	
	}
	
	public static QuickCallInfo getQuickCallInfo(Context context, String traceStr){
		if(LogLevel.DEV){
			DevLog.d(TAG, "getQuickCallInfo traceStr =  " + traceStr);
		}
		Cursor cursor = null;
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable.QUICK_CALL_TOUCH_TRACK + " = '" + traceStr + "'";
			cursor = context.getContentResolver().query(uri, QuickCallProjection.SUMMARY_PROJECTION, where, null, null);
			if(cursor == null || !cursor.moveToFirst()){
				return null;
			}
			
			return getQuickCallInfoByCursor(cursor);
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "getQuickCallInfo failed, e " + e.toString());
			}
			
			return null;
			
		} finally {
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
		}
	}
	
	
	public static QuickCallInfo getQuickCallInfoByNumber(Context context, String number){
		if(LogLevel.DEV){
			DevLog.d(TAG, "getQuickCallInfoByNumber number =  " + number);
		}
		Cursor cursor = null;
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable.QUICK_CALL_NUMBER + " = '" + number + "'";
			cursor = context.getContentResolver().query(uri, QuickCallProjection.SUMMARY_PROJECTION, where, null, null);
			if(cursor == null || !cursor.moveToFirst()){
				return null;
			}
			
			return getQuickCallInfoByCursor(cursor);
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "getQuickCallInfoByNumber failed, e " + e.toString());
			}
			
			return null;
			
		} finally {
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public static QuickCallInfo getQuickCallInfo(Context context, long _id){
		if(LogLevel.DEV){
			DevLog.d(TAG, "getQuickCallInfo _id =  " + _id);
		}
		Cursor cursor = null;
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable._ID + " = '" + _id + "'";
			cursor = context.getContentResolver().query(uri, QuickCallProjection.SUMMARY_PROJECTION, where, null, null);
			if(cursor == null || !cursor.moveToFirst()){
				return null;
			}
			
			return getQuickCallInfoByCursor(cursor);
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "getQuickCallInfo by _id failed, e " + e.toString());
			}
			
			return null;
			
		} finally {
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public static void saveQuickCall(Context context, String name, String number, long photoId, String trace){
		if(LogLevel.DEV){
			DevLog.d(TAG, "saveQuickCall name = " + name + ", number = " + number + ", traceStr =  " + trace);
		}
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			ContentValues value = new ContentValues();
			value.put(QuickCallTable.QUICK_CALL_NAME, name);
			value.put(QuickCallTable.QUICK_CALL_NUMBER, number);
			value.put(QuickCallTable.QUIKC_CALL_PHOTO_ID, photoId);
			value.put(QuickCallTable.QUICK_CALL_TOUCH_TRACK, trace);
			value.put(QuickCallTable.CREATE_TIME, System.currentTimeMillis());
			
			context.getContentResolver().insert(uri, value);
			
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "saveQuickCall failed, e " + e.toString());
			}
		} 
	}
	
	public static void updateQuickCallTrace(Context context, long _id, String trace){
		if(LogLevel.DEV){
			DevLog.d(TAG, "saveQuickCall _id = " + _id + ", traceStr =  " + trace);
		}
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable._ID + " = '" + _id + "'";
			ContentValues value = new ContentValues();
			value.put(QuickCallTable.QUICK_CALL_TOUCH_TRACK, trace);
			value.put(QuickCallTable.CREATE_TIME, System.currentTimeMillis());
			context.getContentResolver().update(uri, value, where, null);
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "saveQuickCall failed, e " + e.toString());
			}
		} 
	}
	
	public static void deleteQuickCallById(Context context, long _id){
		if(LogLevel.DEV){
			DevLog.d(TAG, "deleteQuickCallById _id =  " + _id);
		}
		try{
			Uri uri = UriHelper.getUri(QuickCallProvider.QUICK_CALL_TABLE);
			String where = QuickCallTable._ID + " = '" + _id + "'";
			context.getContentResolver().delete(uri, where, null);
		} catch (Exception e){
			if(LogLevel.MARKET){
				MarketLog.e(TAG, "deleteQuickCallById failed, e " + e.toString());
			}
		}	
	}
}
