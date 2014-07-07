package com.zhuang.quickcall.provider;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.logging.DevLog;

import android.net.Uri;

public class UriHelper {

    private static String TAG = "[ZHUANG]UriHelper";

    public static Uri getUri(String path) {
        if (LogLevel.DEV){
        	DevLog.d(TAG, "getUri(" + path + ')');
        }
        Uri uri = prepare(path).build();
        
        if (LogLevel.DEV){
        	DevLog.d(TAG, "uri: " + uri);
        }
        return uri;
    }
    
    static Uri removeQuery(Uri uri) {
        if (LogLevel.DEV)
            DevLog.d(TAG, "removeQuery(" + uri + ")");

        Uri newUri = uri.buildUpon().query("").build();

        if (LogLevel.DEV)
            DevLog.d(TAG, "new uri: " + newUri);
        
        return newUri;
    }
    
    private static Uri.Builder prepare(String path) {
        return new Uri.Builder().scheme("content")
                                .authority(QuickCallProvider.AUTHORITY)
                                .path(path)
                                .query("")                                          
                                .fragment("");  
                                                
    }
    
}
