/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils;

import com.zhuang.quickcall.R;
import com.zhuang.quickcall.utils.widgets.QuickCallAlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class DialogUtils {

	public static void showQuickCallAlertDialog(Context context, String title, String message){
		
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		setDialogWidth(context, dialog);
	}
	
	public static void showQuickCallAlertDialog(Context context, int title, int message){
		
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		setDialogWidth(context, dialog);
	}
	
	public interface OnClickConfirmButtonListener{
		public void onClick();
	}
	
	public interface OnClickCancelButtonListener{
		public void onClick();
	}
	
	public static void showQuickCallAlertDialog(Context context, int title, int message, final OnClickConfirmButtonListener confirmListner){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(confirmListner != null){
					confirmListner.onClick();
				}
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCancelable(false);
		setDialogWidth(context, dialog);
	}
	
	public static void showQuickCallAlertDialog(Context context, String title, String message, final OnClickConfirmButtonListener confirmListner){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(confirmListner != null){
					confirmListner.onClick();
				}
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		dialog.setCancelable(false);
		setDialogWidth(context, dialog);
	}
	
	public static void showQuickCallAlertDialog(Context context, int title, int message, final OnClickConfirmButtonListener confirmListner, final OnClickCancelButtonListener cancelListener){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(confirmListner != null){
					confirmListner.onClick();
				}
				
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(cancelListener != null){
					cancelListener.onClick();
				}
				
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		setDialogWidth(context, dialog);
	}
	
	public static void showQuickCallAlertDialog(Context context, int title, int message, int posBtnStr, final OnClickConfirmButtonListener confirmListner, final OnClickCancelButtonListener cancelListener){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(posBtnStr, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(confirmListner != null){
					confirmListner.onClick();
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(cancelListener != null){
					cancelListener.onClick();
				}
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		setDialogWidth(context, dialog);
	}
	
	public static void showQuickCallAlertDialog(Context context, String title, String message, String posBtnStr, final OnClickConfirmButtonListener confirmListner, final OnClickCancelButtonListener cancelListener){
		QuickCallAlertDialog.Builder builder = new QuickCallAlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.icon_dialog_title);
		builder.setPositiveButton(posBtnStr, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(confirmListner != null){
					confirmListner.onClick();
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(cancelListener != null){
					cancelListener.onClick();
				}
			}
		});
		
		QuickCallAlertDialog dialog = builder.create();
		dialog.show();
		setDialogWidth(context, dialog);
	}
	
	public static void setDialogWidth(Context context, Dialog dialog){
		Activity activity  = (Activity) context;
		if(activity == null){
			return;
		}
		WindowManager windowManager = activity.getWindowManager();  
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		int screenWidth = (int)(display.getWidth() * 0.9);
		if(screenWidth <= 0){
			screenWidth = DensityUtils.dp_px(250);
		}
		lp.width = screenWidth;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
	}
	
	public static void setDialogWidth(Activity activity, Dialog dialog){
		WindowManager windowManager = activity.getWindowManager();  
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		int screenWidth = (int)(display.getWidth() * 0.9);
		if(screenWidth <= 0){
			screenWidth = DensityUtils.dp_px(250);
		}
		lp.width = screenWidth;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
	}
	
	public static void setDialogWidthHeight(Activity activity, Dialog dialog){
		WindowManager windowManager = activity.getWindowManager();  
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		int screenWidth = (int)(display.getWidth() * 0.9);
		int screenHeight = (int)(display.getHeight() * 0.8);
		if(screenWidth <= 0){
			screenWidth = DensityUtils.dp_px(250);
		}
		if(screenHeight <= 0){
			screenHeight = DensityUtils.dp_px(400);
		}
		lp.width = screenWidth;
		lp.height = screenHeight;
		dialog.getWindow().setAttributes(lp);
	}
	
	public static void setDialogWidthHeight(Context context, Dialog dialog){
		Activity activity  = (Activity) context;
		if(activity == null){
			return;
		}
		WindowManager windowManager = activity.getWindowManager();  
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		int screenWidth = (int)(display.getWidth() * 0.9);
		int screenHeight = (int)(display.getHeight() * 0.8);
		if(screenWidth <= 0){
			screenWidth = DensityUtils.dp_px(250);
		}
		if(screenHeight <= 0){
			screenHeight = DensityUtils.dp_px(400);
		}
		lp.width = screenWidth;
		lp.height = screenHeight;
		dialog.getWindow().setAttributes(lp);
	}
	
	public static void setDialogWidthBig(Context context, Dialog dialog){
		Activity activity  = (Activity) context;
		if(activity == null){
			return;
		}
		WindowManager windowManager = activity.getWindowManager();  
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		int screenWidth = (int)(display.getWidth() * 0.9);
		if(screenWidth <= 0){
			screenWidth = DensityUtils.dp_px(250);
		}
		lp.width = screenWidth;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
	}
	
	public static void setDialogWidthBig(Activity activity, Dialog dialog){
		WindowManager windowManager = activity.getWindowManager();  
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		int screenWidth = (int)(display.getWidth() * 0.9);
		if(screenWidth <= 0){
			screenWidth = DensityUtils.dp_px(250);
		}
		lp.width = screenWidth;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
	}
	
}
