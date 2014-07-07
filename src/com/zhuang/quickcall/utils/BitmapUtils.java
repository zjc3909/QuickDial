package com.zhuang.quickcall.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

public class BitmapUtils {

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(
		drawable.getIntrinsicWidth(),
		drawable.getIntrinsicHeight(),
		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}
	
	public static Bitmap getBitmapThumbnail(String path, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = Math.max((int) (opts.outHeight / (float) height), (int) (opts.outWidth / (float) width));
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(path, opts);
		return bitmap;
	}
	
	public static Bitmap getBitmapThumbnail(Bitmap bmp, int width, int height) {
		Bitmap bitmap = null;
		if (bmp != null) {
			int bmpWidth = bmp.getWidth();
			int bmpHeight = bmp.getHeight();
			if (width != 0 && height != 0) {
				Matrix matrix = new Matrix();
				float scaleWidth = ((float) width / bmpWidth);
				float scaleHeight = ((float) height / bmpHeight);
				matrix.postScale(scaleWidth, scaleHeight);
				bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
			} else {
				bitmap = bmp;
			}
		}
		return bitmap;
	}
	
	public static Bitmap getBitmapThumbnail(Bitmap bmp, float scale) {
		Bitmap bitmap = null;
		if (bmp != null) {
			int bmpWidth = bmp.getWidth();
			int bmpHeight = bmp.getHeight();
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
		}
		return bitmap;
	}
	
	/**
	 * Change bitmap conner 
	 * 
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if(bitmap == null){
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	/**
	 * Change bitmap conner 
	 * 
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx, int width, int height) {
		if(bitmap == null){
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		
		Bitmap output = Bitmap.createBitmap(w < width ? w : width, h < height ? h : height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	public static Bitmap getBigBitmapByPath(String filePath, int width, int height){
		final BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(filePath, options);  
        options.inSampleSize = calculateInSampleSize(options, width, height);  
        options.inJustDecodeBounds = false;  
          
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);  
        if(bm == null){  
            return  null;  
        }  
        int degree = readPictureDegree(filePath);  
        bm = rotateBitmap(bm,degree) ;
        
        return bm;
	}
	
	public static Bitmap getSmallBitmap(String filePath) {  
        final BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(filePath, options);  
        options.inSampleSize = calculateInSampleSize(options, 480, 800);  
        options.inJustDecodeBounds = false;  
          
        Bitmap bm = BitmapFactory.decodeFile(filePath, options);  
        if(bm == null){  
            return  null;  
        }  
        int degree = readPictureDegree(filePath);  
        bm = rotateBitmap(bm,degree) ;  
        ByteArrayOutputStream baos = null ;  
        try{  
            baos = new ByteArrayOutputStream();  
            bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);  
              
        } finally {  
            try {  
                if(baos != null)  
                    baos.close() ;  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return bm ;  
  
    }  
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {  
        final int height = options.outHeight;  
        final int width = options.outWidth;  
        int inSampleSize = 1;  
  
        if (height > reqHeight || width > reqWidth) {  
            final int heightRatio = Math.round((float) height / (float) reqHeight);  
            final int widthRatio = Math.round((float) width / (float) reqWidth);  
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;  
        }  
  
        return inSampleSize;  
    }  
	
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){  
        if(bitmap == null){
        	return null ;  
        }
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();  
  
        Matrix mtx = new Matrix();  
        mtx.postRotate(rotate);  
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);  
    }  
	
	private static int readPictureDegree(String path) {    
        int degree  = 0;    
        try {   
        	ExifInterface exifInterface = new ExifInterface(path);    
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);    
            switch (orientation) {    
            case ExifInterface.ORIENTATION_ROTATE_90:    
                    degree = 90;    
                    break;    
            case ExifInterface.ORIENTATION_ROTATE_180:    
                    degree = 180;    
                    break;    
            case ExifInterface.ORIENTATION_ROTATE_270:    
                    degree = 270;    
                    break;    
            }
                
        } catch (IOException e) {    
            e.printStackTrace();    
        }    
        return degree;    
    }   
	
	/**
	 * draw to round 
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	/**
	 * zoom
	 * 
	 * @param bitmap
	 * @param f
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float zf) {
		Matrix matrix = new Matrix();
		matrix.postScale(zf, zf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * zoom
	 * 
	 * @param bitmap
	 * @param f
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
		Matrix matrix = new Matrix();
		matrix.postScale(wf, hf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * round
	 * 
	 * @param bitmap
	 * @param roundPX
	 * @return
	 */
	public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
		// RCB means
		// Rounded
		// Corner Bitmap
		Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstbmp);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return dstbmp;
	}
}
