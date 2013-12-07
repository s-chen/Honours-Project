package com.basewarp.basewarp.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.basewarp.basewarp.bubbledata.BubbleListManager;
import com.basewarp.basewarp.bubbledata.BubbleModel;

public class DownloadBubbleImageTask extends AsyncTask<Void, Void, Bitmap> {
	private ImageView bmImage;
	private ProgressBar mLoadingAnim;
	private int position;
	private String url;
	private String bubbleListMangerKey;
	private String userId;
	private int width;
	private int height;
	private boolean storeToCache;

	public DownloadBubbleImageTask downloadThumbnailImage(BubbleModel bubble, ImageView bmImage, ProgressBar loadingAnim) {
		
		// Remove "images/" from the content url to just get filename
		String imageURL = bubble.getContentUrl().substring(bubble.getContentUrl().indexOf("/")+1);
		
		String thumbnailURL = Constants.imageThumbnailServiceURL + imageURL;
		downloadImage(thumbnailURL, bmImage, loadingAnim);

		this.bubbleListMangerKey = bubble.getContentUrl();
		this.storeToCache = true;
		return this;

	}
	public DownloadBubbleImageTask downloadContentImage(BubbleModel bubble, ImageView bmImage, ProgressBar loadingAnim) {
		String bubbleImageURL = Constants.imagesBaseURL + bubble.getContentUrl();
		downloadImage(bubbleImageURL, bmImage, loadingAnim);

		this.bubbleListMangerKey = bubble.getContentUrl();
		this.storeToCache = true;
		return this;
	}


	public DownloadBubbleImageTask downloadImage(String url, ImageView bmImage, ProgressBar loadingAnim) {
		this.url = url;
		this.bmImage = bmImage;
		this.mLoadingAnim = loadingAnim;
		return this;
	}


	protected Bitmap doInBackground(Void... params) {
		BubbleListManager.setImageDownloadState(bubbleListMangerKey, true);
		Bitmap mIcon = null;
		try {
			int timeOutInSecs = 10;
			// Set http parameters (timeout)
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeOutInSecs*1000);
			HttpConnectionParams.setSoTimeout(httpParameters, timeOutInSecs*1000);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setParams(httpParameters);

			// Send the http request
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			// If we get a response, store it
			if(entity!=null){
				InputStream in = entity.getContent();
				mIcon = decodeEntity(in);
			}
		} catch (Exception e) {
			BubbleListManager.setImageDownloadState(bubbleListMangerKey, false);
			Log.e("Error", e.toString());
			e.printStackTrace();
		}
		return mIcon;
	}
	
	private synchronized Bitmap decodeEntity(InputStream in) {
		Bitmap out = null;
		try {
			out = BitmapFactory.decodeStream(in);
		} catch (Error e) {
			e.printStackTrace();
			System.gc();
			out = BitmapFactory.decodeStream(in);
		}
		return out;
	}

	protected void onPostExecute(Bitmap result) {
		BubbleListManager.setImageDownloadState(bubbleListMangerKey, false);
		if (result != null) {
			if(storeToCache) BubbleListManager.addImage(bubbleListMangerKey, result);
			bmImage.setImageBitmap(result);
			mLoadingAnim.setVisibility(View.GONE);
			bmImage.setVisibility(View.VISIBLE);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			((Activity)bmImage.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int width =(int)( displaymetrics.widthPixels / 3.0);
			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) bmImage.getLayoutParams();

			if(bmImage.getWidth() > width) {
				params.width = width;
				bmImage.setLayoutParams(params);
			}

		} else {
			bmImage.setVisibility(View.VISIBLE);
			mLoadingAnim.setVisibility(View.GONE);
		}
	}
}
