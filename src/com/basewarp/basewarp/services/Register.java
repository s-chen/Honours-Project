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
import android.widget.Toast;

//import com.basewarp.basewarp.login.LoginManager;
//import com.basewarp.basewarp.login.LoginManager.LoginType;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.ImageHelper;
import com.basewarp.basewarp.util.Util;

public class Register extends Service {

	private ServiceListener listener;
	//	private Context context;

	private String forename = ""; 
	private String surname = ""; 
	private String password = ""; 
	private String dateOfBirth = ""; 
	private String countryName = ""; 
	private String cityName = ""; 
	private String streetAddress = ""; 
	private String facebookAccount = ""; 
	private String foursquareAccount = ""; 
	private String twitterAccount = ""; 
	private String mobileNumber = ""; 
	private String email = ""; 
	private String profileImageContentPath = "";


	public void setProfileImageContentPath(String profileImageContentPath) {
		this.profileImageContentPath = profileImageContentPath;
	}

	public void setListener(ServiceListener listener) {
		this.listener = listener;
	}

	//	public void setContext (Context context) {
	//		this.context = context;
	//	}


	public void setForename(String forename) {
		this.forename = forename;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public void setFacebookAccount(String facebookAccount) {
		this.facebookAccount = facebookAccount;
	}

	public void setFoursquareAccount(String foursquareAccount) {
		this.foursquareAccount = foursquareAccount;
	}

	public void setTwitterAccount(String twitterAccount) {
		this.twitterAccount = twitterAccount;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setEmail(String email) {
		this.email = email;
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
		String content = "";

		String hashedPass = "";
		// If facebook account blank (i.e email/pass registration)
		if (facebookAccount.equalsIgnoreCase("")) {
			hashedPass = Util.sha1(password);
		}

		if (!profileImageContentPath.equalsIgnoreCase("")) {
			content = ImageHelper.loadContentPathToEncodedBitmap(profileImageContentPath, Constants.profilePicWidth, Constants.profilePicHeight);
		}

		/// adding parameters to send to http sever
		nameValuePairs.add(new BasicNameValuePair("forename", forename));
		nameValuePairs.add(new BasicNameValuePair("surname", surname));
		nameValuePairs.add(new BasicNameValuePair("password", hashedPass));
		nameValuePairs.add(new BasicNameValuePair("dateOfBirth", dateOfBirth));
		nameValuePairs.add(new BasicNameValuePair("countryName", countryName));
		nameValuePairs.add(new BasicNameValuePair("cityName", cityName));
		nameValuePairs.add(new BasicNameValuePair("streetAddress", streetAddress));
		nameValuePairs.add(new BasicNameValuePair("facebookAccount", facebookAccount));
		nameValuePairs.add(new BasicNameValuePair("foursquareAccount", foursquareAccount));
		nameValuePairs.add(new BasicNameValuePair("twitterAccount", twitterAccount));
		nameValuePairs.add(new BasicNameValuePair("mobileNumber", mobileNumber));
		nameValuePairs.add(new BasicNameValuePair("email", email));
		nameValuePairs.add(new BasicNameValuePair("content", content));

		String serviceURL = Constants.servicesBaseURL + "register.php";
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

		switch(code) {
		case CONNECTION_TIMEOUT:
			listener.onComplete(response, code);
			break;
		case CONNECTION_FAILED:
			listener.onComplete(response, code);
			break;
		case ACCOUNT_CREATED_INACTIVE:
			listener.onComplete(response, code);
			break;
		case ACCOUNT_CREATED_FACEBOOK:
			actualResponse = (HashMap<String, Integer>) response.get("response");
			listener.onComplete(actualResponse.get("user_id"), StatusCode.LOGIN_VALID);
			break;
		case ACCOUNT_MERGE_INACTIVE:
			listener.onComplete(response, code);
			break;
		case ACCOUNT_MERGED:
			actualResponse = (HashMap<String, Integer>) response.get("response");
			listener.onComplete(actualResponse.get("user_id"), StatusCode.ACCOUNT_MERGED);
			break;
		case ACCOUNT_MERGE_FAILED:
			listener.onComplete(response, StatusCode.ACCOUNT_MERGE_FAILED);
			break;
		default:
			listener.onComplete(response, StatusCode.ACCOUNT_CREATE_FAILED);
			break;
		}

		super.onPostExecute(result);
	}

}