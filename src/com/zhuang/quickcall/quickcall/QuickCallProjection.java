package com.zhuang.quickcall.quickcall;

import com.zhuang.quickcall.provider.QuickCallDataStore.QuickCallTable;

public class QuickCallProjection {

	public static final String[] SUMMARY_PROJECTION = new String[]{
		QuickCallTable._ID,
		QuickCallTable.QUICK_CALL_NAME,
		QuickCallTable.QUICK_CALL_NUMBER,
		QuickCallTable.QUIKC_CALL_PHOTO_ID,
		QuickCallTable.QUICK_CALL_TOUCH_TRACK,
		QuickCallTable.CREATE_TIME
	};
	
	public static final int ID_INDEX = 0;
	public static final int NAME_INDEX = 1;
	public static final int NUMBER_INDEX = 2;
	public static final int PHOTO_ID_INDEX = 3;
	public static final int TOUCH_TRACE_INDEX = 4;
	public static final int CREATE_TIME_INDEX = 5;
	
	public static final String DEFAULT_SORT_ORDER = QuickCallTable.CREATE_TIME + " DESC ";
	
}
