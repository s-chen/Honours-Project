package com.basewarp.basewarp.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

//import com.basewarp.basewarp.login.LoginManager.LoginType;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Util;

public class Login extends Service {

	private ServiceListener listener;

	private String email = ""; 
	private String password = ""; 
	private String facebookAccount = ""; 
	//private LoginType lType = null;

/*	public void setParams(ServiceListener listener, LoginType lType, String email, String password, String facebookAccount) {
		this.listener = listener;
		this.password = password;
		this.facebookAccount = facebookAccount;
		this.email = email;
		this.lType = lType;
	}*/


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		if (listener != null) {
			listener.beforeStart();
		}
	}

	@Override
	protected Object doInBackground(Void... params) {
		String hashedPass = "";
		if(password != null && !password.equalsIgnoreCase("")) hashedPass = Util.sha1(password);

		// list with parameters and their values
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

		// adding parameters to send to http sever
		if (email != null) nameValuePairs.add(new BasicNameValuePair("email", email));
		if(!hashedPass.equalsIgnoreCase("")) nameValuePairs.add(new BasicNameValuePair("password", hashedPass));
		if (facebookAccount != null) nameValuePairs.add(new BasicNameValuePair("facebookAccount", facebookAccount));


		String serviceURL = Constants.servicesBaseURL + "login.php";
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
			listener.onComplete(response, code);
			break;
		case CONNECTION_FAILED:
			listener.onComplete(response, code);
			break;
		case LOGIN_VALID:
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> actualResponse = (HashMap<String, Integer>)response.get("response");
			listener.onComplete(actualResponse.get("user_id"), StatusCode.LOGIN_VALID);
			break;
		case LOGIN_INVALID_PASSWORD:
			listener.onComplete(result, StatusCode.LOGIN_INVALID_PASSWORD);
			break;
		case LOGIN_INVALID_USERNAME:
			listener.onComplete(result, StatusCode.LOGIN_INVALID_USERNAME);
			break;
		case ACCOUNT_UNKNOWN:
			listener.onComplete(result, StatusCode.ACCOUNT_UNKNOWN);
			break;
		case ACCOUNT_INACTIVE:
			listener.onComplete(result, StatusCode.ACCOUNT_INACTIVE);
			break;
		case UNKNOWN:
			listener.onComplete(result, StatusCode.UNKNOWN);
			break;
		default:
			break;
		}
		super.onPostExecute(result);
	}

}