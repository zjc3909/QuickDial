package com.zhuang.quickcall.utils;

import java.util.List;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

public class MemoryCache {
	
	private LruCache<String, Bitmap> mMemoryCache;
	
	public MemoryCache(){
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
	        }
	    };
	}
	
    public Bitmap get(String key){
    	if(TextUtils.isEmpty(key)){
			return null;
		}
		if(mMemoryCache != null){
			return mMemoryCache.get(key);
		} else {
			return null;
		}
    }
    
    public void put(String key, Bitmap bitmap){
    	if(TextUtils.isEmpty(key)){
			return;
		}
	    if (get(key) == null && bitmap != null) {
	        mMemoryCache.put(key, bitmap);
	    }
    }

    public void clear(List<String> keyList) {
    	recycleBitmapCaches(keyList);
    }
    
	public void removeBitmapFromMemCache(String key){
		if(TextUtils.isEmpty(key)){
			return;
		}
		if(get(key) != null){
			mMemoryCache.remove(key);
		}
	}
	
	public void recycleBitmapCaches(List<String> keyList) {
		Bitmap delBitmap = null;
		if(keyList == null || keyList.size() <= 0){
			return;
		}
		for (int del = 0; del < keyList.size(); del++) {
			String key = keyList.get(del);
			delBitmap = get(key);
			if (delBitmap != null) {
				removeBitmapFromMemCache(key);
				delBitmap.recycle();
				delBitmap = null;
			}
		}
	}
}