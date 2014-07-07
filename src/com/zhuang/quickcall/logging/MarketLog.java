/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.logging;

import static android.util.Log.getStackTraceString;
import android.util.Log;

public class MarketLog {

	public static void d(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.d(tag, msg + " " + getStackTraceString(t));
        }
    }

    public static void d(String tag, Throwable t) {
    	if (null != tag && null != t) {
        	Log.d(tag, getStackTraceString(t));
        }
    }

    public static void e(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.e(tag, msg + " " + getStackTraceString(t));
        }
    }

    public static void e(String tag, Throwable t) {
        if (null != tag && null != t) {
        	Log.e(tag, getStackTraceString(t));
        }
    }

    public static void i(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.i(tag, msg + " " + getStackTraceString(t));
        }
    }

    public static void i(String tag, Throwable t) {
        if (null != tag && null != t) {
        	Log.i(tag, getStackTraceString(t));
        }
    }

    public static void v(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (null != tag && null != t && null != msg) {
        	Log.v(tag, msg + " " + getStackTraceString(t));
        }
    }

    public static void v(String tag, Throwable t) {
        if (null != tag && null != t) {
        	Log.v(tag, getStackTraceString(t));
        }
    }

    public static void w(String tag, String msg) {
        if (null != tag && null != msg) {
        	Log.w(tag, msg);
        }
    }

    public static void  w(String tag, String msg, Throwable t) {
        if(null != tag && null != t && null != msg) {
        	Log.w(tag, msg + " " + getStackTraceString(t));
        }
    }

    public static void  w(String tag, Throwable t) {
        if(null != tag && null != t) {
        	Log.w(tag, getStackTraceString(t));
        }
    }
}
