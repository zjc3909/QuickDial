package com.zhuang.quickcall.fragment;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.R;
import com.zhuang.quickcall.logging.DevLog;
import com.zhuang.quickcall.logging.MarketLog;
import com.zhuang.quickcall.settings.SuggestionsActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

	private static final String TAG = "[EASIIO]SettingsFragment";
	
	private View mSuggestionsView;
	private View mRateAppView;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(LogLevel.MARKET){
			MarketLog.i(TAG, "onAttach...");
		}
        
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreate...");
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onCreateView...");
		}
		View v = inflater.inflate(R.layout.settings_fragment_layout, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onViewCreated...");
		}
		mSuggestionsView = view.findViewById(R.id.settings_suggestions_view);
		mRateAppView = view.findViewById(R.id.settings_rate_app_view);
		listenerButton();
		
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onResume(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onResume...");
		}
		super.onResume();
		
	}
	
	@Override
	public void onPause(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onPause...");
		}
		super.onPause();
		
	}
	
	@Override
	public void onDestroy(){
		if(LogLevel.MARKET){
			MarketLog.i(TAG, "onDestroy...");
		}
		super.onDestroy();
		
	}
	
	private void listenerButton(){
		if(LogLevel.DEV){
			DevLog.d(TAG, "listenerButton...");
		}
mSuggestionsView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "User click suggestions view");
				}
				
				try{
					Activity activity = getActivity();
					if(activity == null || activity.isFinishing()){
						if(LogLevel.MARKET){
							MarketLog.e(TAG, "activity is null or finishing.");
						}
						return;
					}
					Intent intent = new Intent(activity, SuggestionsActivity.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				} catch (Exception e){
					e.printStackTrace();
				}
				
			}
		});
		
		mRateAppView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(LogLevel.DEV){
					DevLog.d(TAG, "User click rate app view");
				}
				
				try{
					Activity activity = getActivity();
					if(activity == null || activity.isFinishing()){
						if(LogLevel.MARKET){
							MarketLog.e(TAG, "activity is null or finishing.");
						}
						return;
					}
					String packetName = activity.getPackageName();
					Uri uri = Uri.parse("market://details?id=" + packetName);
					Intent intent_market = new Intent(Intent.ACTION_VIEW, uri);
					getActivity().startActivity(intent_market);
				} catch (Exception e){
					if(LogLevel.MARKET){
						MarketLog.e(TAG, "rate app failed: " + e.toString());
					}
				}
			}
		});
	}
		
}
