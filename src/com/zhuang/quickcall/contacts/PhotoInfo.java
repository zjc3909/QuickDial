/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.contacts;

import android.widget.ImageView;

public class PhotoInfo {
	
	public static final int TYPE_CALL_LOG_LOCAL = 0;
	public static final int TYPE_CALL_LOG_BIZCARD = 1;
	
	public int type;
	public int position;
	public long photoId;
	public String bizcardId;
	public long flagId;

	public PhotoInfo(int position, long photoId) {
		this.position = position;
		this.photoId = photoId;
	}
	
	public PhotoInfo(int type, int position, long photoId, String bizcardId, long flagId) {
		this.type = type;
		this.position = position;
		this.photoId = photoId;
		this.bizcardId = bizcardId;
		this.flagId = flagId;
	}

	public ImageView photoView;
}
