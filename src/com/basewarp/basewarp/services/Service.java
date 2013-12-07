package com.basewarp.basewarp.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

import com.basewarp.basewarp.services.ServiceListener.StatusCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class Service extends AsyncTask<Void, Void, Object> {

	protected ServiceListener listener;

	@Override
	protected abstract Object doInBackground(Void... voids);

	@SuppressWarnings("unchecked")
	protected Object executeHTTPRequest(String url, List<NameValuePair> nameValuePairs, int timeOutInSecs) {
		String result = null;
		HashMap<String,Object> responseMap = new HashMap<String, Object>();

		try {
			// Set http parameters (timeout)
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeOutInSecs*1000);
			HttpConnectionParams.setSoTimeout(httpParameters, timeOutInSecs*1000);

			HttpClient httpclient = new DefaultHttpClient();

			// If uploading image, use post request
			boolean hasContent = false;
			for (NameValuePair pair : nameValuePairs) {
				if (pair.getName().toString().equalsIgnoreCase("content")) {
					hasContent = true;
					break;
				}
			}

			HttpResponse response  = null;
			HttpEntity entity = null;
			if (hasContent) {
				HttpPost http = new HttpPost(url);
				http.setParams(httpParameters);
				http.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
				response = httpclient.execute(http);
			} else {
				String queryString = getQuery(nameValuePairs);
				HttpGet http = new HttpGet(url + queryString);
				http.setParams(httpParameters);
				response = httpclient.execute(http);
			}

			// Send the http request
			entity = response.getEntity();

			// If we get a response, store it
			if(entity!=null){
				InputStream in = entity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				result = br.readLine();
			}

			if(result == null) {
				responseMap.put("status", StatusCode.UNKNOWN);
				return responseMap;
			}

			// Instantiate the result into an object (hashmap)
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
			gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
			Gson gson = gsonBuilder.create();
			responseMap = (HashMap<String, Object>)gson.fromJson(result, Object.class);

			// Replace the integer status code with the enumerated status code
			Integer statusCode = (Integer) responseMap.get("status");
			responseMap.put("status", ServiceListener.StatusCode.getEnumByCode(statusCode));

			// If we failed, set the status of the response
		} catch (SocketTimeoutException e) {
			responseMap.put("status", StatusCode.CONNECTION_TIMEOUT);
		} catch (SocketException e) {
			responseMap.put("status", StatusCode.CONNECTION_FAILED);
		} catch (OutOfMemoryError e) {
			responseMap.put("status", StatusCode.UNKNOWN);
			System.gc();
		}catch (Exception e) {
			responseMap.put("status", StatusCode.UNKNOWN);
		}

		return responseMap;
	}

	private String getQuery(List<NameValuePair> params) {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		try {
			for (NameValuePair pair : params) {
				if (first) first = false;
				else result.append("&");

				result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
			}
			return "?" + result.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";

	}

	protected Object executeHTTPRequest(String url, List<NameValuePair> nameValuePairs) {
		return executeHTTPRequest(url, nameValuePairs, 1);
	}

	@Override
	protected void onPostExecute (Object result) {
		super.onPostExecute(result);
	}

	private static class NaturalDeserializer implements JsonDeserializer<Object> {
		public Object deserialize(JsonElement json, Type typeOfT, 
				JsonDeserializationContext context) {
			if(json.isJsonNull()) return null;
			else if(json.isJsonPrimitive()) return handlePrimitive(json.getAsJsonPrimitive());
			else if(json.isJsonArray()) return handleArray(json.getAsJsonArray(), context);
			else return handleObject(json.getAsJsonObject(), context);
		}
		private Object handlePrimitive(JsonPrimitive json) {
			if(json.isBoolean())
				return json.getAsBoolean();
			else if(json.isString())
				return json.getAsString();
			else {
				BigDecimal bigDec = json.getAsBigDecimal();
				// Find out if it is an int type
				try {
					bigDec.toBigIntegerExact();
					try { return bigDec.intValueExact(); }
					catch(ArithmeticException e) {}
					return bigDec.longValue();
				} catch(ArithmeticException e) {}
				// Just return it as a double
				return bigDec.doubleValue();
			}
		}
		private Object handleArray(JsonArray json, JsonDeserializationContext context) {
			Object[] array = new Object[json.size()];
			for(int i = 0; i < array.length; i++)
				array[i] = context.deserialize(json.get(i), Object.class);
			return array;
		}
		private Object handleObject(JsonObject json, JsonDeserializationContext context) {
			Map<String, Object> map = new HashMap<String, Object>();
			for(Map.Entry<String, JsonElement> entry : json.entrySet())
				map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
			return map;
		}
	}
}
