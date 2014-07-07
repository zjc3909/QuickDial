/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package com.zhuang.quickcall.utils.widgets;

import com.zhuang.quickcall.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * ContactAlphaView
 * @author gavin.zhuang
 */
public class ContactAlphaView extends ImageView {
	
	private static final int LENGTH = 27;
	private Drawable alphaDrawable;
	private boolean showBkg; 
	private int choose; 
	private String[] ALPHAS;
	private OnAlphaChangedListener listener;

	public ContactAlphaView(Context context) {
		super(context);
		initAlphaView();
	}

	public ContactAlphaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAlphaView();
	}

	public ContactAlphaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAlphaView();
	}

	private void initAlphaView() {
		showBkg = false;
		choose = -1;
		setImageResource(R.drawable.alpha_normal);
		alphaDrawable = getDrawable();
		
		ALPHAS = new String[LENGTH];
		ALPHAS[0] = "#";
		for (int i = 1; i < LENGTH; i++) {
			ALPHAS[i] = String.valueOf((char) (65 + i - 1));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (showBkg) {
			setImageResource(R.drawable.alpha_pressed);
			alphaDrawable = getDrawable();

			alphaDrawable.setBounds(0, 0, getWidth(), getHeight());
		} else {
			setImageResource(R.drawable.alpha_normal);
			alphaDrawable = getDrawable();

			alphaDrawable.setBounds(0, 0, getWidth(), getHeight());
		}

		canvas.save();
		alphaDrawable.draw(canvas);
		canvas.restore();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * LENGTH);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < ALPHAS.length) {
					listener.OnAlphaChanged(ALPHAS[c], c);
					choose = c;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < ALPHAS.length) {
					listener.OnAlphaChanged(ALPHAS[c], c);
					choose = c;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	public void setOnAlphaChangedListener(OnAlphaChangedListener listener) {
		this.listener = listener;
	}

	public interface OnAlphaChangedListener {
		public void OnAlphaChanged(String s, int index);
	}

}
