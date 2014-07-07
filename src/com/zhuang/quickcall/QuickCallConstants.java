package com.zhuang.quickcall;

public class QuickCallConstants {

	public static final String QC_SHARED_PREFERENCES_CONSTANTS = "com.zhuang.quickcall.QC_SHARED_PREFERENCES_CONSTANTS";
	public static final String QC_IS_FIRST_OPEN = "com.zhuang.quickcall.QC_IS_FIRST_OPEN_V1";
	public static final String QC_IS_FIRST_CREATE = "com.zhuang.quickcall.QC_IS_FIRST_CREATE_V1";
	
	public static final String ACTION_SELECT_CONTACT = "com.zhuang.quickcall.action.ACTION_SELECT_CONTACT";
	public static final String ACTION_NETWORK_STATE_CHANGED = "com.zhuang.quickcall.action.ACTION_NETWORK_STATE_CHANGED";
	
	public static final String EXTRA_CONTACT_PERSON_ID = "com.zhuang.quickcall.EXTRA_CONTACT_PERSON_ID";
	public static final String EXTRA_SELECT_NUMBER = "com.zhuang.quickcall.extra.EXTRA_SELECT_NUMBER";
	public static final String EXTRA_SELECT_NAME = "com.zhuang.quickcall.extra.EXTRA_SELECT_NAME";
	public static final String EXTRA_SELECT_PHOTO_ID = "com.zhuang.quickcall.extra.EXTRA_SELECT_PHOTO_ID";
	public static final String EXTRA_SELECT_QUICK_CALL_ID = "com.zhuang.quickcall.extra.EXTRA_SELECT_QUICK_CALL_ID";
	public static final String EXTRA_CALL_LOG_NUMBER = "com.zhuang.quickcall.extra.EXTRA_CALL_LOG_NUMBER";
	
	/**
     * Parse database column 
     */
    public static final String PARSE_REPLY_TABLE_NAME = "Reply";
    public static final String PARSE_REPLY_TIME = "reply_time";
    public static final String PARSE_REPLY_CONTENT = "reply_content";
    public static final String PARSE_REPLY_CONTACT = "reply_contact";
    public static final String PARSE_DEVICE = "device";
    public static final String PARSE_ANDROID_LEVEL = "android_level";
    public static final String PARSE_ANDROID_VERSION = "android_version";
    public static final String PARSE_APP_VERSION = "app_version";
    
    /* Parse User col name */
    public static final String PARSE_USER_TABLE_NAME = "Users";
	public static final String PARSE_USER_ANDROID_VERSION = "AndroidVersion";
	public static final String PARSE_USER_APP_VERSION = "AppVersion";
	public static final String PARSE_USER_DEVICE_NAME = "DeviceName";
	public static final String PARSE_USER_DEVICE_IMSI = "DeviceIMSI";
	public static final String PARSE_USER_FIRST_LOGIN = "FirstLogin";
	public static final String PARSE_USER_LOCATION = "Location";
    
}
