package com.zhuang.quickcall.activity;

import android.app.ListActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class QuickCallListActivity extends ListActivity {

	@Override
	public void setContentView(int layoutResID) {
		ViewGroup rootView = (ViewGroup) LayoutInflater.from(this).inflate(layoutResID, null);
		setContentView(rootView);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		leakCleanUpRootView();
		mLeakContentView = (ViewGroup) view;
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		leakCleanUpRootView();
		mLeakContentView = (ViewGroup) view;
	}

	/**
	 * Cleanup root view to reduce memory leaks.
	 */
	protected void leakCleanUpRootView() {
		if (mLeakContentView != null) {
			ViewGroup v = mLeakContentView;
			mLeakContentView = null;
			mStackRecursions = 0;
			leakCleanUpChildsDrawables(v);
			System.gc();
		}
	}

	/**
	 * Clean-up Drawables in the view including child.
	 * 
	 * @param v
	 */
	private void leakCleanUpChildsDrawables(View v) {
		if (v != null) {
			try {
				ViewGroup group = (ViewGroup) v;
				int childs = group.getChildCount();
				for (int i = 0; i < childs; i++) {
					if (LEAKS_CLEAN_UP_STACK_RECURSIONS_LIMIT > mStackRecursions) {
						mStackRecursions++;
						leakCleanUpChildsDrawables(group.getChildAt(i));
						mStackRecursions--;
					} else {
						break;
					}
				}
			} catch (java.lang.Throwable th) {
			}
			leakCleanUpDrawables(v);
		}
	}

	/**
	 * Keeps recursions number for memory clean-ups.
	 */
	private int mStackRecursions;

	/**
	 * Limit of leaks clean-ups stack recursions.
	 */
	private static final int LEAKS_CLEAN_UP_STACK_RECURSIONS_LIMIT = 256;

	/**
	 * Cleans drawables of in the view.
	 * 
	 * @param v
	 *            the view to clean-up
	 */
	private void leakCleanUpDrawables(View v) {
		if (v == null) {
			return;
		}

		try {
			if (v.getBackground() != null) {
				v.getBackground().setCallback(null);
			}
		} catch (java.lang.Throwable th) {
		}

		try {
			v.setBackgroundDrawable(null);
		} catch (java.lang.Throwable th) {
		}

		try {
			ImageView imageView = (ImageView) v;
			imageView.setImageDrawable(null);
			imageView.setBackgroundDrawable(null);
		} catch (java.lang.Throwable th) {
		}

	}

	/**
	 * Keeps current root view.
	 */
	private ViewGroup mLeakContentView = null;
	
}
