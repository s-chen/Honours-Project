package com.basewarp.basewarp.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.basewarp.basewarp.bubbledata.BubbleModel;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;

public class GetSingleBubble extends Service {

	private String url;
	private List<NameValuePair> params;
	
	public GetSingleBubble(String url) {
		this.url = Constants.bubbleShareURL;
		
		String id = "";
		if(url.contains("?id=")) id = url.substring(url.indexOf("=")+1); 
		
		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("format", "json"));
		
	}

	public void setListener(ServiceListener listener) {
		this.listener = listener;
	}

	@Override
	protected Object doInBackground(Void... voids) {
		return executeHTTPRequest(url, params);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute (Object result) {
		if(listener == null) {
			super.onPostExecute(result);
			return;
		}

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
			HashMap<String, Object> tempResult = (HashMap<String, Object>) result;
			Object[] tempBubbles = (Object[])tempResult.get("response");

			ArrayList<BubbleModel> bubbles = new ArrayList<BubbleModel>();
			for(Object b : tempBubbles) {
				bubbles.add(new BubbleModel((HashMap<String, Object>)b));
			}
			if(bubbles.size() > 0) listener.onComplete(bubbles.get(0), StatusCode.SUCCESS);
			else listener.onComplete(null, StatusCode.UNKNOWN);
			break;
		default:
			listener.onComplete(result, StatusCode.UNKNOWN);
			break;
		}
		super.onPostExecute(result);
	}


}