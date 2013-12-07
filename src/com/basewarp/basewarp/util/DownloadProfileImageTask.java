package com.basewarp.basewarp.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

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
//import com.basewarp.basewarp.login.LoginManager;

public class DownloadProfileImageTask extends AsyncTask<Void, Void, Bitmap> {
	private ImageView bmImage;
	private ProgressBar mLoadingAnim;
	private int position;
	private String url;
	private String bubbleListMangerKey;
	private boolean swapLoadingAnim;
	private String userId;
	private int width;
	private int height;
	private boolean storeToCache;

	public DownloadProfileImageTask downloadProfileImage(String userId, ImageView bmImage, int requestedWidth, int requestedHeight, ProgressBar loadingAnim) {
		downloadProfileImage(userId, bmImage, requestedWidth, requestedHeight);
		this.mLoadingAnim = loadingAnim;
		this.swapLoadingAnim = true;
		return this;

	}

	public DownloadProfileImageTask downloadProfileImage(String userId, ImageView bmImage, int requestedWidth, int requestedHeight) {
		this.swapLoadingAnim = false;
		this.userId = userId;
		this.width = requestedWidth;
		this.height = requestedHeight;
		this.bmImage = bmImage;
		this.bubbleListMangerKey = Util.getBubbleListManagerKeyForProfilePic(userId, requestedWidth, requestedHeight);
		this.storeToCache = true;
		return this;
	}

	protected Bitmap doInBackground(Void... params) {
		BubbleListManager.setImageDownloadState(bubbleListMangerKey, true);
		String host = Constants.servicesBaseURL + "profileImageForUser.php";

		// list with parameters and their values
		HttpPost httppost = new HttpPost(host);
		HttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

		if (userId == null) {
			//userId = String.valueOf(LoginManager.getUID());
		} 
		nameValuePairs.add(new BasicNameValuePair("userId", userId));
		nameValuePairs.add(new BasicNameValuePair("width", String.valueOf(width)));
		nameValuePairs.add(new BasicNameValuePair("height", String.valueOf(height)));

		Bitmap icon = null;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if(entity!=null){
				InputStream in = entity.getContent();
				icon = BitmapFactory.decodeStream(in);
			}

		} catch (Exception e) {
			// TODO: handle exception
			BubbleListManager.setImageDownloadState(bubbleListMangerKey, false);
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
			BubbleListManager.setImageDownloadState(bubbleListMangerKey, false);
			System.gc();
		}

		return icon;
	}

	protected void onPostExecute(Bitmap result) {
		BubbleListManager.setImageDownloadState(bubbleListMangerKey, false);
		bmImage.setVisibility(View.VISIBLE);
		if (result != null) {
			if(storeToCache)
				BubbleListManager.addImage(bubbleListMangerKey, result);
			bmImage.setImageBitmap(result);


			if(swapLoadingAnim) {
				mLoadingAnim.setVisibility(View.GONE);
				DisplayMetrics displaymetrics = new DisplayMetrics();
				((Activity)bmImage.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int width =(int)( displaymetrics.widthPixels / 3.0);
				ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) bmImage.getLayoutParams();

				if(bmImage.getWidth() > width) {
					params.width = width;
					bmImage.setLayoutParams(params);
				}
			}

		} else {
			if(swapLoadingAnim) 
				mLoadingAnim.setVisibility(View.GONE);
		}
	}
}
