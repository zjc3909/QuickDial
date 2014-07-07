package com.zhuang.quickcall.utils;

import com.zhuang.quickcall.LogLevel;
import com.zhuang.quickcall.QuickCallConstants;
import com.zhuang.quickcall.logging.MarketLog;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtils {
	
	private static final String TAG = "[ZHUANG]NetworkUtils";
    
    private static NetworkState previousNetworkState = NetworkState.NONE;

    public enum NetworkState {
        SERVERREQUEST, FULL, WIFI, MOBILE, NONE
    }

    public static boolean isGPRSNetwork(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType() == TelephonyManager.NETWORK_TYPE_GPRS;
    }

    public static int getCelluarCallState(Context context){
    	return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getCallState();
    }
    
    private static NetworkState isNetworkStateChanged(Context context, NetworkState state) {
        if (!state.equals(previousNetworkState)) {
            previousNetworkState = state;
            context.sendBroadcast(new Intent(QuickCallConstants.ACTION_NETWORK_STATE_CHANGED));
        }
        return state;
    }

    public static String getDetailedNetState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        String info = null;
        if (connManager != null) {
            NetworkInfo activeNet = connManager.getActiveNetworkInfo();
            if (activeNet != null) {
                info = activeNet.toString();
            }
        }
        if (info == null) {
            info = "?";
        }
        return info;
    }
    
    public static NetworkState getActiveNetworkState(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
    	if( activeNetwork != null ){
    		int type = activeNetwork.getType();
    		if (type == ConnectivityManager.TYPE_WIFI ) return NetworkState.WIFI;
    		else if(type == ConnectivityManager.TYPE_MOBILE ) return NetworkState.MOBILE;
    	}
    	
    	return NetworkState.NONE;
    }

    public static NetworkState getNetworkState(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean wifiEnabled = (wifi != null) && wifi.isAvailable() && wifi.isConnected();
        boolean mobileEnabled = (mobile != null) && mobile.isAvailable() && mobile.isConnected();
        
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnectedOrConnecting()) {
            int type = activeNetwork.getType();
            
            if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_MOBILE) {
                if (wifiEnabled) {
                    if (mobileEnabled) {
                        return isNetworkStateChanged(context, NetworkState.FULL);
                    } else {
                        return isNetworkStateChanged(context, NetworkState.WIFI);
                    }
                } else {
                    return isNetworkStateChanged(context, NetworkState.MOBILE);
                }
            }
            return isNetworkStateChanged(context, NetworkState.FULL);
        } else if ( wifiEnabled && mobileEnabled) {
            return isNetworkStateChanged(context, NetworkState.FULL);
        } else if (wifiEnabled) {
            return isNetworkStateChanged(context, NetworkState.WIFI);
        } else if (mobileEnabled) {
            return isNetworkStateChanged(context, NetworkState.MOBILE);
        } else {
            return isNetworkStateChanged(context, NetworkState.NONE);
        }
    }

    /**
     * detect current network availability based on phone state
     *
     * @param context
     * @return boolean as network accessibility
     */
    public static boolean isEasiioAvailable(Context context) {
    	
    	try {
    		NetworkState network_state = getNetworkState(context); 
    		boolean result = network_state != NetworkState.NONE;
    		if (LogLevel.MARKET) {
    			MarketLog.i(TAG, "isEasiioAvaliable():  NetworkState = " +  network_state + "; return " + result);
    		}
    		return result;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    public static String getNetworkTypeLabel(int type) {
        switch (type) {
        case 0: return "MOBILE";
        case 1: return "WIFI";
        case 2: return "MOBILE_MMS";
        case 3: return "MOBILE_SUPL";
        case 4: return "MOBILE_DUN";
        case 5: return "MOBILE_HIPRI";
        case 6: return "WIMAX";
        case 7: return "BLUETOOTH";
        case 8: return "DUMMY";
        case 9: return "ETHERNET";
        }
        return "UNKNOWN";
    }
    
    public static String getRadioNetworkTypeLabel(int type) {
        switch (type) {
        case 0:
            return "NETWORK_TYPE_UNKNOWN";
        case 1:
            return "NETWORK_TYPE_GPRS";
        case 2:
            return "NETWORK_TYPE_EDGE";
        case 3:
            return "NETWORK_TYPE_UMTS";
        case 4:
            return "NETWORK_TYPE_CDMA";
        case 5:
            return "NETWORK_TYPE_EVDO_0";
        case 6:
            return "NETWORK_TYPE_EVDO_A";
        case 7:
            return "NETWORK_TYPE_1xRTT";
        case 8:
            return "NETWORK_TYPE_HSDPA";
        case 9:
            return "NETWORK_TYPE_HSUPA";
        case 10:
            return "NETWORK_TYPE_HSPA";
        case 11:
            return "NETWORK_TYPE_IDEN";
        case 12:
            return "NETWORK_TYPE_EVDO_B";
        case 13:
            return "NETWORK_TYPE_LTE";
        case 14:
            return "NETWORK_TYPE_EHRPD";
        case 15:
            return "NETWORK_TYPE_HSPAP";
        }
        return "UNKNOWN";
    }
    
    public static String getNetworkStatusAsString(Context ctx) {
        if (ctx != null) {
            try {
                StringBuffer sb = new StringBuffer("NET [EASIIO:");
                sb.append(getNetworkState(ctx));
                sb.append("; ACTIVE:");

                String activeNetwork = null;
                int activeNetworkType = -1;
                int activeNetworkSubType = -1;
                int radioNetworkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
                ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                TelephonyManager telephonyMng = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                if (connManager != null) {
                    NetworkInfo activeNet = connManager.getActiveNetworkInfo();
                    if (activeNet != null) {
                        activeNetworkType = activeNet.getType();
                        activeNetworkSubType = activeNet.getSubtype();
                        activeNetwork = activeNet.toString();
                    }
                }
                if (telephonyMng != null) {
                    radioNetworkType = telephonyMng.getNetworkType();
                }

                sb.append(getNetworkTypeLabel(activeNetworkType));
                sb.append('(');
                sb.append(activeNetworkSubType);
                sb.append(')');
                sb.append(" RADIO:");
                sb.append(getRadioNetworkTypeLabel(radioNetworkType));
                sb.append(" {");
                if (activeNetwork != null) {
                    sb.append(activeNetwork);
                } else {
                    sb.append("NULL");
                }
                sb.append("}]");
                return sb.toString();
            } catch (java.lang.Throwable th) {

            }
        }
        return "";
    }
}
