package com.basewarp.basewarp.util;

//import com.basewarp.basewarp.ui.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
	
	private static String tag = "NetworkChangeReceiver";
	private static boolean hasWifi = false;
	private static boolean hasMobile = false;
	private static boolean hasInternet = false;

    /** Network connectivity information */
    private static NetworkInfo mNetworkInfo;

	public NetworkChangeReceiver() {
	}
	
	public static boolean getHasInternet() {
		return hasInternet;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean noConnectivity = intent.getBooleanExtra( ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        mNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			hasWifi = !noConnectivity;
		} else if (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			hasMobile = !noConnectivity;
		}
		hasInternet = hasWifi || hasMobile;
		if (!hasInternet) {
			//MainActivity main = (MainActivity)context;
			//main.onResumeLogic();
		}

	}
}