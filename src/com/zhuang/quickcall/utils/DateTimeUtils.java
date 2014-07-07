/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.SystemClock;
import android.text.format.Time;

public class DateTimeUtils {
	
    private static String TAG = "[EASIIO] DateUtils";
    private static String TIME_FORMAT = "HH:mm";
    private static String DATE_FORMAT = "yyyy-MM-dd";
    private static String DATE_WEEK_FORMAT = "yyyy-MM-dd E";
    private static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    
    public static String getDateTimeLabel(long time) {
    	SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        return formatter.format(new Date(time));
    }

    public static String getUTCandRelativeDateFromElapsedTime(long elapsedTime) {
        try {
        	SimpleDateFormat formatter = new SimpleDateFormat("dd/HH:mm:ss");
            int flags = android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
            long curTime = System.currentTimeMillis();
            long time = curTime - (SystemClock.elapsedRealtime() - elapsedTime);
            StringBuffer sb = new StringBuffer();
            sb.append(formatter.format(new Date(time)));
            if (time <= curTime) {
                sb.append('{');
                sb.append(android.text.format.DateUtils.getRelativeTimeSpanString(time, curTime, android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        flags).toString());
                sb.append('}');
            }
            return sb.toString();
        } catch (java.lang.Throwable th) {
            return "TIME_ERROR";
        }
    }
    
    public static String getRelativeDateLabel(long time) {
		long curTime = System.currentTimeMillis();
		long startTimeOfToday = getStartTimeOfDay(curTime);
		
		if (time > curTime) {
			return getDateLabel(time);
		} else if (time > startTimeOfToday) {
			return getTimeLabel(time);
		} else{
			return getDateLabel(time);
		} 
		
	}
    
    public static long getStartTimeOfDay(long timeInLong){
		Time time = new Time(); 
		time.set(timeInLong);
		
		Time dayStartTime = new Time(); 
		dayStartTime.year = time.year;
		dayStartTime.month = time.month;
		dayStartTime.monthDay = time.monthDay;
		
		return dayStartTime.toMillis(true);
	}
    
    public static String getDateLabel(long date) {
    	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
    
    public static String getDateWeekLabel(long date){
    	SimpleDateFormat sdf = new SimpleDateFormat(DATE_WEEK_FORMAT);
        return sdf.format(date);
    }
    
    public static String getTimeLabel(long date) {
    	SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(date);
    }
    
    private static final String PARSE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String getCurrentTimeStr(){
    	SimpleDateFormat dateFormat = new SimpleDateFormat(PARSE_DATE_FORMAT);
    	Date d = new Date(System.currentTimeMillis());
    	return dateFormat.format(d);
    }
    
    public static long getTimeByStr(String str){
    	SimpleDateFormat sdf= new SimpleDateFormat(PARSE_DATE_FORMAT);
    	Date date;
		try {
			date = sdf.parse(str);
			return date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return System.currentTimeMillis();
		}
    	
    }
    
}
