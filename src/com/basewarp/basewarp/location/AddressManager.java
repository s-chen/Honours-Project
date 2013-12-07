package com.basewarp.basewarp.location;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.basewarp.basewarp.services.ServiceListener;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;

public class AddressManager extends AsyncTask<Void, Void, Object> {

	ServiceListener mListener;

	private Context mContext;
	private Location queryLocation;
	private double queryLatitude;
	private double queryLongitude;
	private String queryAddressText;
	private long queryTime;
	private QueryType qType;

	private static enum QueryType {
		LOCTOADDRESS,
		LATLONGTOADDRESS,
		TEXTTOADDRESSLIST,
		LATLONGTIMETOTIMEZONE
	}

	private Address getAddressFromLocation(Context context, Location location) {
		return getAddressFromLocation(context, location.getLatitude(),location.getLongitude());
	}

	private Address getAddressFromLocation(Context context, double lat, double lon) {
		List<Address> addresses = null;
		Geocoder geocoder = new Geocoder(context);
		if(!Geocoder.isPresent()) 
			return null;

		try {
			addresses = geocoder.getFromLocation(lat, lon, 5);
		} catch (IOException e) {
			Log.w("Address Translation - Getting address from latlon", "Using backup service");
			addresses = getLocationInfoBackup(lat,lon,5);
		}

		try {
			if(addresses.size() > 0) {
				return addresses.get(0);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		return null; 
	}

	private ArrayList<Address> queryLocationText(Context context, String text) {
		ArrayList<Address> out = null;
		if(Geocoder.isPresent()) {
			Geocoder geocoder = new Geocoder(context);
			try {
				List<Address> results = geocoder.getFromLocationName(text, 10);
				out = new ArrayList<Address>();
				for(Address a : results) {
					out.add(a);
				}
			} catch (IOException e) {
				Log.w("Address Translation - Getting address from text", "Using backup service");
				String addressURL = "";
				try {
					addressURL = URLEncoder.encode(text, "utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				List<Address> results = getLocationInfoBackup(addressURL,10);
				out = new ArrayList<Address>();
				for(Address a : results) {
					out.add(a);
				}
			}
		}
		return out;
	}
	
	public void setQueryParams(Context context, double latitude, double longitude, long timeInMillis) {
		mContext = context;
		queryLatitude = latitude;
		queryLongitude = longitude;
		queryTime = timeInMillis;
		qType = QueryType.LATLONGTIMETOTIMEZONE;
		
	}

	public void setQueryParams(Context context, Location location) {
		mContext = context;
		queryLocation = location;
		qType = QueryType.LOCTOADDRESS;
	}

	public void setQueryParams(Context context, double latitude, double longitude) {
		// Right here or map
		mContext = context;
		queryLongitude = longitude;
		queryLatitude = latitude;
		qType = QueryType.LATLONGTOADDRESS;
	}

	public void setQueryParams(Context context, String address) {
		mContext = context;
		queryAddressText = address;
		qType = QueryType.TEXTTOADDRESSLIST;
	}

	private void clearQueryVars() {
		mContext = null;
		queryLocation = null;
		queryLatitude = Double.NaN;
		queryLongitude = Double.NaN;
		queryAddressText = null;
		qType = null;
	}

	@Override
	protected Object doInBackground(Void... args) {
		switch(qType) {
		case LOCTOADDRESS:
			return getAddressFromLocation(mContext, queryLocation);
		case LATLONGTOADDRESS:
			return getAddressFromLocation(mContext, queryLatitude, queryLongitude);
		case TEXTTOADDRESSLIST:
			return queryLocationText(mContext, queryAddressText);
		case LATLONGTIMETOTIMEZONE:
			return getLocationTimezone(queryLatitude, queryLongitude, queryTime);
		default:
			return null;
		}
	}

	@Override
	protected void onPostExecute (Object result) {
		super.onPostExecute(result);
		if (mListener != null) {
			if(result == null) {
				if(qType == QueryType.LATLONGTIMETOTIMEZONE) mListener.onComplete(result, StatusCode.ADDRESS_TIMEZONE_FAILED);
				else mListener.onComplete(result, StatusCode.ADDRESS_CONNECTION_TIMEOUT);
			} else if(qType == QueryType.LOCTOADDRESS || qType == QueryType.LATLONGTOADDRESS) {
				mListener.onComplete(result, StatusCode.ADDRESS_OK);
			} else if(qType == QueryType.TEXTTOADDRESSLIST) {
				mListener.onComplete(result, StatusCode.ADDRESS_LIST_OK);
			} else if(qType == QueryType.LATLONGTIMETOTIMEZONE) {
				mListener.onComplete(result, StatusCode.ADDRESS_TIMEZONE_OK);
			}
		}		
		clearQueryVars();
	}

	public void setListener(ServiceListener listener) {
		this.mListener = listener;
	}

	private ArrayList<Address> getLocationInfoBackup(double lat, double lon, int results) {

		String query = "https://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&oe=utf8&sensor=false";
		Address addr = null;
		ArrayList<Address> addresses = new ArrayList<Address>();
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, "utf-8");
		HttpClient client = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(query);

		HttpResponse response;

		try {
			response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {

				HttpEntity entity = response.getEntity();
			    String jsonText = EntityUtils.toString(entity, HTTP.UTF_8);
				try {
					JSONObject jsonObject = new JSONObject(jsonText);
					for (int i = 0; i < results; i++) {
						addr = new Address(Locale.getDefault());

						JSONArray addrComp = ((JSONArray)jsonObject.get("results")).getJSONObject(i).getJSONArray("address_components");
						for(int j = 0; j < addrComp.length(); j++) {
							String component = ((JSONArray)((JSONObject)addrComp.get(j)).get("types")).getString(0);
							if (component.compareTo("route") == 0) {
								component = ((JSONObject)addrComp.get(j)).getString("long_name");
								addr.setAddressLine(0, component);
							} else if (component.compareTo("locality") == 0) {
								component = ((JSONObject)addrComp.get(j)).getString("long_name");
								addr.setAddressLine(1, component);
							} else if (component.compareTo("country") == 0) {
								component = ((JSONObject)addrComp.get(j)).getString("long_name");
								addr.setAddressLine(2, component);
							}
						}
						
						Double longitude = Double.valueOf(0);
						Double latitude = Double.valueOf(0);
						
						longitude = ((JSONArray)jsonObject.get("results")).getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
						latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");

						addr.setLatitude(latitude);
						addr.setLongitude(longitude);

						addresses.add(addr);
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addresses;
	}
	
	private ArrayList<Address> getLocationInfoBackup(String address, int results) {

		String query = "https://maps.google.com/maps/api/geocode/json?address=" + address + "&oe=utf8&sensor=false";
		Address addr = null;
		ArrayList<Address> addresses = new ArrayList<Address>();		
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, "utf-8");
		HttpClient client = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(query);

		HttpResponse response;

		try {
			response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {

				HttpEntity entity = response.getEntity();
			    String jsonText = EntityUtils.toString(entity, HTTP.UTF_8);

				try {
					JSONObject jsonObject = new JSONObject(jsonText);
					for (int i = 0; i < results; i++) {
						addr = new Address(Locale.getDefault());

						JSONArray addrComp = ((JSONArray)jsonObject.get("results")).getJSONObject(i).getJSONArray("address_components");
						for(int j = 0; j < addrComp.length(); j++) {
							String component = ((JSONArray)((JSONObject)addrComp.get(j)).get("types")).getString(0);
							if (component.compareTo("route") == 0) {
								component = ((JSONObject)addrComp.get(j)).getString("long_name");
								addr.setAddressLine(0, component);
							} else if (component.compareTo("locality") == 0) {
								component = ((JSONObject)addrComp.get(j)).getString("long_name");
								addr.setAddressLine(1, component);
							} else if (component.compareTo("country") == 0) {
								component = ((JSONObject)addrComp.get(j)).getString("long_name");
								addr.setAddressLine(2, component);
							}
						}
						
						Double longitude = Double.valueOf(0);
						Double latitude = Double.valueOf(0);
						
						longitude = ((JSONArray)jsonObject.get("results")).getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
						latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");

						addr.setLatitude(latitude);
						addr.setLongitude(longitude);

						addresses.add(addr);
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addresses;
	}
	
	private TimeZone getLocationTimezone(double lat, double lon, long time) {

		int t = (int)(time/1000);
			
		String query = "https://maps.googleapis.com/maps/api/timezone/json?location="+lat+","+lon+"&timestamp="+t+"&sensor=false";
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(query);

		TimeZone timeZone = null;
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {

				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
				
				try {
					JSONObject jsonObject = new JSONObject(stringBuilder.toString());
					//for (int i = 0; i < results; i++) {
					if(jsonObject.getString("status").equalsIgnoreCase("ok")) {
						timeZone = TimeZone.getTimeZone(jsonObject.getString("timeZoneId"));
					}
						
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeZone;
	}

}
