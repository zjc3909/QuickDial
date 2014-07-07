package com.zhuang.quickcall.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.contacts.ContactsUtils;
import com.zhuang.quickcall.logging.DevLog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

public class ImageLoader {

	public static final String TAG = "[ZHUANG]ImageLoader";
	
	public static final int PHOTO_WIDTH_HEIGHT = DensityUtils.dp_px(50);
	
	public static final int TYPE_CONTACTS = 0;
	public static final int TYPE_CALL_LOG = 1;
	
	private Context mContext;
	private int mType;
	private MemoryCache mMemoryCache = new MemoryCache();
	private Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService mExecutorService;
	
	private int default_image_res_id = R.drawable.icon_contact_list_default_round_photo;
	
	public ImageLoader(Context context, int type) {
		this.mContext = context;
		this.mType = type;
		mExecutorService = Executors.newFixedThreadPool(5);
	}
	
	public void displayImage(String photoId, ImageView imageView) {
		mImageViews.put(imageView, photoId);
		Bitmap bitmap = mMemoryCache.get(photoId);
		if (bitmap != null){
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(photoId, imageView);
			imageView.setImageResource(default_image_res_id);
		}
	}
	
	
	private void queuePhoto(String photoId, ImageView imageView) {
		final PhotoToLoad p = new PhotoToLoad(photoId, imageView);
		mExecutorService.submit(new PhotosLoader(p));
	}
	
	private class PhotoToLoad {
		public String mPhotoId;
		public ImageView mImageView;

		public PhotoToLoad(String photoId, ImageView image_view) {
			this.mPhotoId = photoId;
			this.mImageView = image_view;
		}
		
	}

	private class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad)){
				if(LogLevel.DEV){
					DevLog.e(TAG, "It is reused.");
				}
				return;
			}
			
			Bitmap bmp = null;
			if(mType == TYPE_CONTACTS){
				bmp = ContactsUtils.getContactInfoPhoto(mContext, Long.valueOf(photoToLoad.mPhotoId));
			} else if (mType == TYPE_CALL_LOG){
				long contactId = ContactsUtils.lookupContactIDByPhoneNumber(photoToLoad.mPhotoId, mContext);
				bmp = ContactsUtils.getContactInfoPhotoByContactId(mContext, contactId);
			}
			
			if(bmp != null){
				bmp = BitmapUtils.toRoundBitmap(bmp);
			}
			
			mMemoryCache.put(photoToLoad.mPhotoId, bmp);
			if (imageViewReused(photoToLoad)){
				if(LogLevel.DEV){
					DevLog.e(TAG, "It is reused 2.");
				}
				return;
			}
			final BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.mImageView.getContext();
			a.runOnUiThread(bd);
		}
	}
	
	private boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = mImageViews.get(photoToLoad.mImageView);
		if (TextUtils.isEmpty(tag) || !tag.equals(photoToLoad.mPhotoId)){
			return true;
		}
		return false;
	}
	
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)){
				return;
			}
			if (bitmap != null){
				photoToLoad.mImageView.setImageBitmap(bitmap);
			} else {
				photoToLoad.mImageView.setImageResource(default_image_res_id);
			}
		}
	}
	
	public Bitmap getBitmapFromCache(String key){
		return mMemoryCache.get(key);
	}

	public void clearCache(List<String> keyList) {
		mMemoryCache.clear(keyList);
	}

}
