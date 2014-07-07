package com.zhuang.quickcall;

import java.util.ArrayList;

import com.zhuang.quickcall.logging.DevLog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {
	
	private static final String TAG = "[ZHUANG]HomeFragmentPagerAdapter";
	
    private ArrayList<Fragment> mFragmenstList;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public HomeFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.mFragmenstList = fragments;
    }

    @Override
    public int getCount() {
        return mFragmenstList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragmenstList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
    
    @Override  
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);  
        if(LogLevel.DEV){
        	DevLog.d(TAG, "destroyItem position = " + position);
        }
    }  

}