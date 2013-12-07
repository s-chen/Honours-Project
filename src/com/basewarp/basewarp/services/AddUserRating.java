package com.basewarp.basewarp.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;

import android.os.AsyncTask;
import android.util.Log;

public class AddUserRating extends Service {
	
	private ServiceListener listener;
	private String userId = ""; 
	private String bubbleId = ""; 
	private String ratingTypeId = ""; 
	
	public void setListener(ServiceListener listener) {
		this.listener = listener;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setBubbleId(String bubbleId) {
		this.bubbleId = bubbleId;
	}
	
	public void setRatingTypeId(String ratingTypeId) {
		this.ratingTypeId = ratingTypeId;
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
				
		// list with parameters and their values
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		
		/// adding parameters to send to http sever
		nameValuePairs.add(new BasicNameValuePair("userId", userId));
		nameValuePairs.add(new BasicNameValuePair("bubbleId", bubbleId));
		nameValuePairs.add(new BasicNameValuePair("ratingTypeId", ratingTypeId));
	
		String serviceURL = Constants.servicesBaseURL + "addUserRating.php";
		return executeHTTPRequest(serviceURL, nameValuePairs);
	}
	
	@Override
	protected void onPostExecute (Object result) {
		if(listener == null) {
			super.onPostExecute(result);
			return;
		}
		@SuppressWarnings("unchecked")
		HashMap<String, Object> response = (HashMap<String, Object>) result;
		StatusCode code = (StatusCode)response.get("status");
		
		switch (code) {
		case CONNECTION_TIMEOUT:
			listener.onComplete(response, code);
			break;
		case CONNECTION_FAILED:
			listener.onComplete(response, code);
			break;
		case AUTHENTICATION_FAILED:
			listener.onComplete(response, code);
			break;
		case SUCCESS:
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> rating = (HashMap<String, Integer>)response.get("response");
			listener.onComplete(rating.get("rating_type_id"), StatusCode.RATING_OK);
			break;
		default:
			listener.onComplete(response, StatusCode.RATING_FAILED);
			break;
		}
		super.onPostExecute(result);
	}
	
}