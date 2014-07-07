/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils;

import com.zhuang.quickcall.QuickCallApp;

public class DensityUtils {
	
	public static int dp_px(float dpValue) {  
        final float scale = QuickCallApp.getContextQuickCall().getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
	
	public static int px_dp(float pxValue) {  
        final float scale = QuickCallApp.getContextQuickCall().getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 

}
