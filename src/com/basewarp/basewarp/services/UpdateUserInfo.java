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

//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.ImageHelper;
import com.basewarp.basewarp.util.Util;

public class UpdateUserInfo extends Service {
	
	private ServiceListener listener;
//	private Context context;
	
	private String forename = ""; 
	private String surname = ""; 
	private String email = ""; 
	private String password = "";
	private String newPassword = ""; 
	private String profileImageContentPath = "";

	public UpdateUserInfo setParams(String password, String newPassword, ServiceListener listener, String forename, String surname, String email, String profileImagePath) {
		this.password = password;
		this.newPassword = newPassword;
		this.listener = listener;
		this.forename = forename;
		this.surname = surname;
		this.email = email;
		this.profileImageContentPath = profileImagePath;
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
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
		
		String hashedPass = Util.sha1(password);
		String hashedNewPass = Util.sha1(newPassword);
		
		String content = "";
		
		if (profileImageContentPath != null && !profileImageContentPath.equalsIgnoreCase("")) {
			content = ImageHelper.loadContentPathToEncodedBitmap(profileImageContentPath, Constants.profilePicWidth, Constants.profilePicHeight);
		}
		
/*		/// adding parameters to send to http sever
		nameValuePairs.add(new BasicNameValuePair("userId", String.valueOf(LoginManager.getUID())));
		nameValuePairs.add(new BasicNameValuePair("forename", forename));
		nameValuePairs.add(new BasicNameValuePair("surname", surname));
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("password", hashedPass));
		nameValuePairs.add(new BasicNameValuePair("newPassword", hashedNewPass));
		nameValuePairs.add(new BasicNameValuePair("content", content));*/
	
		String serviceURL = Constants.servicesBaseURL + "updateUserInfo.php";
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
		case SUCCESS:
			listener.onComplete(response.get("response"), StatusCode.UPDATE_INFO_OK);
			break;
		case UPDATE_INFO_PW_INCORRECT:
			listener.onComplete(response.get("response"), StatusCode.UPDATE_INFO_PW_INCORRECT);
			break;
		default:
			listener.onComplete(response.get("response"), StatusCode.UPDATE_INFO_FAILED);
			break;
		}
		super.onPostExecute(result);
	}
	
}