package com.zhuang.quickcall;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.Parse;
import com.parse.ParseObject;
import com.zhuang.quickcall.activity.QuickCallActivity;
import com.zhuang.quickcall.config.Build;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.utils.DateTimeUtils;
import com.zhuang.quickcall.utils.DeviceUtils;

/**
 * GuideScreenActivity
 * @author gavin.zhuang 
 * 
 */
public class GuideScreenActivity extends QuickCallActivity{

	private static final String TAG = "[ZHUANG]GuideScreenActivity";

	private ViewPager mViewPager;  
    private ArrayList<View> mViewList;  
    private ImageView[] mImageViews;
    private ViewGroup mMainViewGroup;
    private int mViewCount;
    private int mCurSel;
    private LinearLayout mBottomDotLayout;
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreate...");
		}
		
		LayoutInflater inflater = getLayoutInflater();  
        mViewList = new ArrayList<View>();  
        mViewList.add(inflater.inflate(R.layout.first_initiate_layout_01, null));  
        mViewList.add(inflater.inflate(R.layout.first_initiate_layout_02, null));  
        mViewList.add(inflater.inflate(R.layout.first_initiate_layout_03, null));   
  
         
        mMainViewGroup = (ViewGroup) inflater.inflate(R.layout.guide_screen_layout, null);  
        
        mViewPager = (ViewPager) mMainViewGroup.findViewById(R.id.guidePages);  
        mBottomDotLayout = (LinearLayout) mMainViewGroup.findViewById(R.id.bottom_dot_layout);
        if(mBottomDotLayout == null){
        	return;
        }
        mViewCount = mViewList.size();
        mImageViews = new ImageView[mViewCount];

		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (ImageView) mBottomDotLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		
        setContentView(mMainViewGroup);  
  
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new GuidePageAdapter());  
        mViewPager.setOnPageChangeListener(new GuidePageChangeListener());
        
        Parse.initialize(this, Build.PARSE_APPLICATION_ID, Build.PARSE_CLIENT_KEY);
		ParseObject userObject = new ParseObject(QuickCallConstants.PARSE_USER_TABLE_NAME);
		userObject.put(QuickCallConstants.PARSE_USER_APP_VERSION, this.getString(R.string.app_version));
		userObject.put(QuickCallConstants.PARSE_USER_ANDROID_VERSION, DeviceUtils.getAndroidReleaseVersion());
		userObject.put(QuickCallConstants.PARSE_USER_DEVICE_IMSI, DeviceUtils.getDeviceIMSI(this));
		userObject.put(QuickCallConstants.PARSE_USER_DEVICE_NAME, DeviceUtils.getDeviceName());
		userObject.put(QuickCallConstants.PARSE_USER_LOCATION, "");
		userObject.put(QuickCallConstants.PARSE_USER_FIRST_LOGIN, DateTimeUtils.getCurrentTimeStr());
		userObject.saveInBackground();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onResume...");
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onStart...");
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onPause...");
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		
		super.leakCleanUpRootView();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;
	}

    class GuidePageAdapter extends PagerAdapter {  
    	  
        @Override  
        public int getCount() {  
            return mViewList.size();  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public int getItemPosition(Object object) {  
            return super.getItemPosition(object);  
        }  
  
        @Override  
        public void destroyItem(View view, int position, Object object) {  
            ((ViewPager) view).removeView(mViewList.get(position));  
        }  
  
        @Override  
        public Object instantiateItem(View view, int position) {  
            ((ViewPager) view).addView(mViewList.get(position));
            if(position == 2){
            	View startButton = mViewList.get(position).findViewById(R.id.start_button);
        		if(startButton != null){
        			startButton.setOnClickListener(mStartOnClickListener);
        		}
            }
            return mViewList.get(position);  
        }  
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
  
        }  
    } 
    
    class GuidePageChangeListener implements OnPageChangeListener {  
  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
  
        }  
  
        @Override  
        public void onPageSelected(int arg0) {  
        	setCurPoint(arg0);
        }  
  
    } 

    private View.OnClickListener mStartOnClickListener = new View.OnClickListener() {
	    
		@Override
		public void onClick(View arg0) {
			Intent i = new Intent(GuideScreenActivity.this, QuickCallMainActivity.class);
			GuideScreenActivity.this.startActivity(i);
			GuideScreenActivity.this.finish();
			GuideScreenActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	};
}
