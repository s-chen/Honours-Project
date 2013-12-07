package com.basewarp.basewarp.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.basewarp.basewarp.bubbledata.BubbleListManager;
import com.basewarp.basewarp.bubbledata.BubbleModel;
import com.basewarp.basewarp.location.GPSManager;
//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class BubbleListService extends Service  {

	protected ServiceListener listener;
	protected int timeout = 8000;
	protected int radius = 40;
	protected String url = "";
	protected Constants.BubbleListTypes type = null;
	protected Context context;
	protected Location loc = null;
	protected int firstBubbleNum;
	protected List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	protected BubbleListService(int firstBubbleNum) {
		this.firstBubbleNum = firstBubbleNum;
	}

	public boolean updateLocation() {
		loc = GPSManager.getLocation();
		return (loc != null);
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setListener(ServiceListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		if (listener != null) {
			listener.beforeStart();
		}
	}


	@Override
	protected Object doInBackground(Void... params) {

		int accuracy = (int) Math.round(GPSManager.getLocation().getAccuracy());
		
		if(type == BubbleListTypes.USERBUBBLES) {
			nameValuePairs.add(new BasicNameValuePair("userId", Integer.toString(((UserBubbles)this).uid)));
			return executeHTTPRequest(url, nameValuePairs, 10);
		}

/*		String uid = LoginManager.isLoggedIn() ? String.valueOf(LoginManager.getUID()) : "0";
		nameValuePairs.add(new BasicNameValuePair("userId", uid));
		nameValuePairs.add(new BasicNameValuePair("offset", Integer.toString(firstBubbleNum)));
		nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(GPSManager.getLocation().getLatitude())));
		nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(GPSManager.getLocation().getLongitude())));
		nameValuePairs.add(new BasicNameValuePair("radius", Integer.toString(this.radius+accuracy)));*/
		
		// Check if user is warped/lockation is otherwise spoofed
		boolean locationSpoofed = false;
		// NOT YET WORKING PROPERLY
//		 || GPSManager.mockLocationsAllowed() || !GPSManager.isLocationFromHardwareSource()
		if (GPSManager.isLocationWarped()) locationSpoofed = true;
		nameValuePairs.add(new BasicNameValuePair("location_spoofed", locationSpoofed ? "1" : "0"));
		
		return executeHTTPRequest(url, nameValuePairs, 10);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute (Object result) {
		if(listener == null) {
			super.onPostExecute(result);
			return;
		}

		@SuppressWarnings("unchecked")
		HashMap<String, Object> response = (HashMap<String, Object>) result;
		StatusCode code = (StatusCode)response.get("status");

		switch(code) {
		case CONNECTION_TIMEOUT:
			listener.onComplete(null,StatusCode.CONNECTION_TIMEOUT);
			break;
		case CONNECTION_FAILED:
			listener.onComplete(result, StatusCode.CONNECTION_FAILED);
			break;
		case SUCCESS:
			if(listener.getClass().equals(BubbleListManager.class)) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> tempResult = (HashMap<String, Object>) result;
				Object[] tempBubbles = (Object[])tempResult.get("response");

				ArrayList<BubbleModel> bubbles = new ArrayList<BubbleModel>();
				for(Object b : tempBubbles) {
					bubbles.add(new BubbleModel((HashMap<String, Object>)b));
				}
				((BubbleListManager)listener).onComplete(bubbles, StatusCode.BUBBLE_DATA_OK, this.type);
			}
			break;
		case BUBBLES_EMPTY:
			((BubbleListManager)listener).onComplete(null, StatusCode.BUBBLES_EMPTY, this.type);
			break;
		default:
			listener.onComplete(result, StatusCode.UNKNOWN);
			break;
		}
		super.onPostExecute(result);
	}

}