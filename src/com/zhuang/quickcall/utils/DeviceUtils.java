package com.zhuang.quickcall.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallApp;
import com.zhuang.quickcall.logging.DevLog;

import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public class DeviceUtils {

    private static final String TAG = "[EASIIO] DeviceUtils";
    
    public static final String SYS_FILE_MAX_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    public static final String SYS_FILE_CURR_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";

    
    public static String getLine1Number(Context ctx) {
		return ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
	}

    
	public static int getDisplayOrientation(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (LogLevel.DEV)
            DevLog.i(TAG, "getDisplayOrientation() #1: orientation = " + orientation);
        
        if (orientation == Configuration.ORIENTATION_UNDEFINED) {
            orientation = calcDisplayOrientation(context);
    
            if (LogLevel.DEV)
                DevLog.i(TAG, "getDisplayOrientation() #2: orientation = " + orientation);
        }
        return orientation;
    }

    public static int calcDisplayOrientation(Context context) {
        if (LogLevel.DEV)
            DevLog.i(TAG, "calcDisplayOrientation() #1");
    
        int orientation;
        DisplayMetrics disp_metrics = context.getResources().getDisplayMetrics();
        
        if (disp_metrics.heightPixels > disp_metrics.widthPixels)
            orientation = Configuration.ORIENTATION_PORTRAIT;
        else if (disp_metrics.heightPixels < disp_metrics.widthPixels)
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        else
            orientation = Configuration.ORIENTATION_SQUARE;
    
        if (LogLevel.DEV)
            DevLog.i(TAG, "calcDisplayOrientation() #2: orientation = " + orientation);
        return orientation;
    }
    
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		
		return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
	}
    
	public static long getAvailableExternalMemorySize() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
		} else {
			return -1;
		}
	}
	
	public static String getDeviceName() {
		String manuf = null, model = null;
		Class<?> execClass;
		try {
			execClass = Class.forName("android.os.Build");
			if (null != execClass) {
				Field manufacturerF = execClass.getField("MANUFACTURER");

				if (null != manufacturerF) {
					manuf = (String) manufacturerF.get(null);
				}
				Field modelF = execClass.getField("MODEL");
				if (null != modelF) {
					model = (String) modelF.get(null);
				}
				
				manuf = TextUtils.isEmpty(manuf) ? "" : manuf;
				model = TextUtils.isEmpty(model) ? "" : model;
				return manuf + " " + model;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String getAndroidLevel(){
		return String.valueOf(android.os.Build.VERSION.SDK_INT);
	}
	
	public static String getAndroidReleaseVersion(){
		return android.os.Build.VERSION.RELEASE;
	}
	
	public static String getAndroidSDK(){
		return android.os.Build.VERSION.SDK;
	}
	
	public static String getDeviceId(){
		Context context = QuickCallApp.getContextQuickCall();
		TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
		String deviceId = TelephonyMgr.getDeviceId(); 
		
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
		String wlanMac = wm.getConnectionInfo().getMacAddress();
		
		return deviceId + wlanMac;
	}
	
	public static boolean isChineseLangue() {  
	       Locale locale = Locale.getDefault();  
	       return locale.toString().equals(Locale.SIMPLIFIED_CHINESE.toString());
	   }  
	
	public static String getDeviceIMSI(Context ctx){
        String IMSI = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
        if (TextUtils.isEmpty(IMSI)) {
            IMSI = "NO SIM CARD";
        }
        return IMSI;
        
	}
	
}
