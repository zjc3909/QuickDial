/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.logging;

import android.util.Log;
import static android.util.Log.getStackTraceString;

public class DevLog {

	public static void d(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.d(tag, "DEBUG:" + msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.d(tag, "DEBUG:" + msg + " " + getStackTraceString(t));
        }
    }

    public static void d(String tag, Throwable t) {
    	if (null != tag && null != t) {
        	Log.d(tag, "DEBUG:" + getStackTraceString(t));
        }
    }

    public static void e(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.e(tag, "DEBUG:" + msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.e(tag, "DEBUG:" + msg + " " + getStackTraceString(t));
        }
    }

    public static void e(String tag, Throwable t) {
        if (null != tag && null != t) {
        	Log.e(tag, "DEBUG:" + getStackTraceString(t));
        }
    }

    public static void i(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.i(tag, "DEBUG:" + msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.i(tag, "DEBUG:" + msg + " " + getStackTraceString(t));
        }
    }

    public static void i(String tag, Throwable t) {
        if (null != tag && null != t) {
        	Log.i(tag, "DEBUG:" + getStackTraceString(t));
        }
    }

    public static void v(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.v(tag, "DEBUG:" + msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.v(tag, "DEBUG:" + msg + " " + getStackTraceString(t));
        }
    }

    public static void v(String tag, Throwable t) {
        if (null != tag && null != t) {
        	Log.v(tag, "DEBUG:" + getStackTraceString(t));
        }
    }

    public static void w(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.w(tag, "DEBUG:" + msg);
        }
    }

    public static void  w(String tag, String msg, Throwable t) {
        if(null != tag && null != t && null != msg) {
        	Log.w(tag, "DEBUG:" + msg + " " + getStackTraceString(t));
        }
    }

    public static void  w(String tag, Throwable t) {
        if(null != tag && null != t) {
        	Log.w(tag, "DEBUG:" + getStackTraceString(t));
        }
    }
}
