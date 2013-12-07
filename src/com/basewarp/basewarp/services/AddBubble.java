package com.basewarp.basewarp.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.location.Address;

import com.basewarp.basewarp.bubbledata.BubbleModel;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.ImageHelper;

public class AddBubble extends Service {

	private ServiceListener listener;
	private BubbleModel bubble;
	private Context context;

	public void setListener(ServiceListener newBubbleActivity) {
		this.listener = newBubbleActivity;
	}

	public void setBubble(BubbleModel bubble) {
		this.bubble = bubble;
	}

	public void setContext (Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		if (listener != null) {
			listener.beforeStart();
		}
	}

	@Override
	protected Object doInBackground(Void... params) {
		String userId = String.valueOf(bubble.getUserId());
		String typeId = String.valueOf(bubble.getTypeId());
		String locationId = String.valueOf(bubble.getLocationId());
		if (locationId.equalsIgnoreCase("0")) locationId = "";
		String description = bubble.getDescription();
		String latitude = String.valueOf(bubble.getLatitude());
		String longitude = String.valueOf(bubble.getLongitude());
		String radius = String.valueOf(bubble.getRadius());

		String streetAddress = bubble.getStreetAddress();
		String city = bubble.getCity();
		String country = bubble.getCountry();
		String diffToUTC = String.valueOf(bubble.getDiffToUTC());
		String dst = String.valueOf(bubble.getDayLightSaving());
		String warpable = String.valueOf(bubble.getIsWarpable());

		SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
		String startDatetime = sqlFormat.format(bubble.getStartDatetime());
		String endDatetime = sqlFormat.format(bubble.getEndDatetime());

		String content = "";
		// If the bubble is an image
		if (bubble.getTypeId() == 2) {
			content = ImageHelper.loadContentPathToEncodedBitmap(bubble.getImageContentPath(), Constants.uploadImageMaxWidth, Constants.uploadImageMaxHeight);
		} else if (bubble.getTypeId() == 4) {
			content = bubble.getWebContentPath();
		}

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);

		/// adding parameters to send to http sever
		nameValuePairs.add(new BasicNameValuePair("userId", userId));
		nameValuePairs.add(new BasicNameValuePair("typeId", typeId));
		nameValuePairs.add(new BasicNameValuePair("description", description));
		nameValuePairs.add(new BasicNameValuePair("content", content));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
		nameValuePairs.add(new BasicNameValuePair("radius", radius));
		nameValuePairs.add(new BasicNameValuePair("startDatetime", startDatetime));
		nameValuePairs.add(new BasicNameValuePair("endDatetime", endDatetime));

		nameValuePairs.add(new BasicNameValuePair("locationId", locationId));
		nameValuePairs.add(new BasicNameValuePair("streetAddress", streetAddress));
		nameValuePairs.add(new BasicNameValuePair("city", city));
		nameValuePairs.add(new BasicNameValuePair("country", country));
		nameValuePairs.add(new BasicNameValuePair("diffToUtc", diffToUTC));
		nameValuePairs.add(new BasicNameValuePair("dayLightSaving", dst));
		nameValuePairs.add(new BasicNameValuePair("isWarpable", warpable));
		
		String serviceURL = Constants.servicesBaseURL + "addBubble.php";
		return executeHTTPRequest(serviceURL, nameValuePairs, 1000);
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
			listener.onComplete(response, StatusCode.ADD_BUBBLE_OK);
			break;
		case ADD_BUBBLE_FAILED:
			listener.onComplete(response, StatusCode.ADD_BUBBLE_FAILED);
			break;
		case ADD_BUBBLE_IMAGE_FAILED:
			listener.onComplete(response, StatusCode.ADD_BUBBLE_IMAGE_FAILED);
			break;
		default:
			listener.onComplete(response, StatusCode.ADD_BUBBLE_FAILED);
			break;
		}
		super.onPostExecute(result);
		System.gc(); // Cleanup
	}
}