package com.basewarp.basewarp.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;

public class GetUserInfo extends Service {

	private ServiceListener listener;

	public void setListener(ServiceListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		if (listener != null) {
			listener.beforeStart();
		}
	}

	@Override
	protected Object doInBackground(Void... params) {

		// list with parameters and their values
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		//String userId = String.valueOf(LoginManager.getUID());

		// adding parameters to send to http sever
		//nameValuePairs.add(new BasicNameValuePair("userId", userId));

		String serviceURL = Constants.servicesBaseURL + "userInfo.php";
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

		switch(code) {
		case CONNECTION_TIMEOUT:
			listener.onComplete(result, code);
			break;
		case CONNECTION_FAILED:
			listener.onComplete(result, code);
			break;
		case USER_DATA_OK:
			listener.onComplete(response.get("response"), StatusCode.USER_DATA_OK);
			break;
		case USER_DATA_FAILED:
			listener.onComplete(response.get("response"), StatusCode.USER_DATA_FAILED);
			break;
		default:
			listener.onComplete(response.get("response"), StatusCode.USER_DATA_FAILED);
			break;
		} 
		super.onPostExecute(result);
	}
}