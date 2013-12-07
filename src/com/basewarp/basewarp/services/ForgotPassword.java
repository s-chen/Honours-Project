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

import android.os.AsyncTask;
import android.util.Log;

import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Util;

public class ForgotPassword extends Service {
	
	private ServiceListener listener;

	private String email = ""; 
	
	public ForgotPassword setListener(ServiceListener listener) {
		this.listener = listener;
		return this;
	}

	public ForgotPassword setEmail(String email) {
		this.email = email;
		return this;
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

		// adding parameters to send to http sever
		nameValuePairs.add(new BasicNameValuePair("email", email));
		
		String serviceURL = Constants.servicesBaseURL + "forgotPassword.php";
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
		HashMap<String, Integer> actualResponse;
		StatusCode code = (StatusCode)response.get("status");
		
		switch (code) {
		case CONNECTION_TIMEOUT:
			listener.onComplete(null,StatusCode.CONNECTION_TIMEOUT);
			break;
		case CONNECTION_FAILED:
			listener.onComplete(result, StatusCode.CONNECTION_FAILED);
			break;
		case PW_RESET_EMAIL_SENT:
			listener.onComplete(result, StatusCode.PW_RESET_EMAIL_SENT);
			break;
		case PW_RESET_INVALID_EMAIL:
			listener.onComplete(result, StatusCode.PW_RESET_INVALID_EMAIL);
			break;
		case PW_RESET_FAILED:
			listener.onComplete(result, StatusCode.PW_RESET_FAILED);
			break;
		default:
			listener.onComplete(result, StatusCode.PW_RESET_FAILED);
			break;
		}

		super.onPostExecute(result);
	}
	
}