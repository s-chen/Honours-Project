package com.basewarp.basewarp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.basewarp.basewarp.bubbledata.BubbleModel;
//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.services.ServiceListener.StatusCode;
//import com.basewarp.basewarp.ui.MapFragmentActivity;
//import com.basewarp.basewarp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Util {

	private static ProgressDialog loadingDialog = null;

	public static String getBubbleListManagerKeyForProfilePic(int userId, int width, int height) {
		return userId + "_w" + width + "_h" + height;
	}

	public static String getBubbleListManagerKeyForProfilePic(String userId, int width, int height) {
		return userId + "_w" + width + "_h" + height;
	}

	public static boolean isEmailValid(String email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public static String sha1(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("SHA-1").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {
			int i = (b & 0xFF);
			if (i < 0x10) hex.append('0');
			hex.append(Integer.toHexString(i));
		}

		return hex.toString();
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public static void simpleConfirmDialog(final Activity callingActivity, final Callable<?> functToExecutOnConfirm, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
		builder
		.setTitle(title)
		.setPositiveButton("Yes", new OnClickListener() {

		//	@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					functToExecutOnConfirm.call();
				} catch (Exception e) {
					Log.e(Util.class.getSimpleName(),"Error calling function passed as parameter for confirmDialog");
					e.printStackTrace();
				}
			}
		})
		.setNegativeButton("No", new OnClickListener() {

			//@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create()
		.show(); 
	}

/*	public static void addImageDialog(final Activity callingActivity) {
		//AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
		//builder.setTitle("Select Source").setItems(R.array.choose_image_dialogue_entries,new OnClickListener() {

			//@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = null;
				switch(which) {
				case 0: // choose from gallery
					i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					i.setType("image/*");
				//	callingActivity.startActivityForResult(i, Constants.RESULT_LOAD_IMAGE);
					break;
				case 1: // take new picture
					i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					if(Build.VERSION.SDK_INT < 14 || Build.VERSION.SDK_INT >= 17) {
						File dir = new File(Constants.photoFilePath);
						dir.mkdirs();
						File f = new File(Constants.photoFilePath,Constants.photoFileName);
						if(f.exists()) f.delete();
						i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					}
				//	callingActivity.startActivityForResult(i, Constants.RESULT_LOAD_PHOTO);
					break;
				}

			}
		});
		builder.create().show(); 
	}*/

	public static String getRealPathFromGalleryURI(Uri contentURI, Activity activity) {
		Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null); 
		if (cursor == null) // Source is Dropbox or other similar local file path
			return contentURI.getPath();
		else {
			cursor.moveToFirst(); 
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
			String path = cursor.getString(idx);
			activity.stopManagingCursor(cursor);
			cursor.close();
			return path; 
		}
	}

	public static String getRealPathFromPhotoURI(Uri contentURI, Activity activity) {
		final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
		final String imageOrderBy = MediaStore.Images.Media._ID+" DESC";
		Cursor imageCursor = activity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
		if (imageCursor == null) // Source is Dropbox or other similar local file path
			return contentURI.getPath();
		else {
			if(imageCursor.moveToFirst()){
				String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
				activity.stopManagingCursor(imageCursor);
				imageCursor.close();
				return fullPath;
			}else{
				return "";
			}
		}
	}

	public static String storeImage(Bitmap image, Context mContext) {
		File pictureFile = getOutputMediaFile(mContext);
		if (pictureFile == null) {
			Log.d("UTIL", "Error creating media file, check storage permissions: ");// e.getMessage());
			return null;
		} 
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d("UTIL", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("UTIL", "Error accessing file: " + e.getMessage());
		}
		return pictureFile.getAbsolutePath();
	}

	public static File getOutputMediaFile(Context mContext){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this. 
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + mContext.getPackageName() + "/Files"); 

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				return null;
			}
		} 

		// Create a media file name
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
		File mediaFile;
		String mImageName="Profile_"+ timeStamp +".jpg";
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  

		return mediaFile;
	} 

	public static void showBlockingLoadingDialog(Context context, String title, String message) {
		loadingDialog = ProgressDialog.show(context, title, message, true);
		loadingDialog.setCancelable(false);
	}

	public static void dismissBlockingLoadingDialog() {
		if(loadingDialog != null) loadingDialog.dismiss();
		loadingDialog = null;
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void enableKeyboard(Activity activity, EditText where) {
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.showSoftInput(where, InputMethodManager.SHOW_IMPLICIT);
	}

	// a2 over a1
	private static int compositeAlpha(int a1, int a2) {
		return 255 - ((255 - a2) * (255 - a1)) / 255;
	}

	// For a single R/G/B component. a = precomputed compositeAlpha(a1, a2)
	private static int compositeColorComponent(int c1, int a1, int c2, int a2, int a) {
		// Handle the singular case of both layers fully transparent.
		if (a == 0) {
			return 0x00;
		}
		return (((255 * c2 * a2) + (c1 * a1 * (255 - a2))) / a) / 255;
	}

	// argb2 over argb1. No range checking.
	public static int compositeColor(int argb1, int argb2) {
		final int a1 = Color.alpha(argb1);
		final int a2 = Color.alpha(argb2);

		final int a = compositeAlpha(a1, a2);

		final int r = compositeColorComponent(Color.red(argb1), a1,   
				Color.red(argb2), a2, a);
		final int g = compositeColorComponent(Color.green(argb1), a1, 
				Color.green(argb2), a2, a);
		final int b = compositeColorComponent(Color.blue(argb1), a1, 
				Color.blue(argb2), a2, a);

		return Color.argb(a, r, g, b);
	}

	public static String capitalizeDescription(String description) {
		if (description != null) {
			if ( description.length() > 2) {
				String upperString = description.substring(0,1).toUpperCase() + description.substring(1);
				return upperString;
			}
		}
		return description;
	}

	public static int roundToNearestFifty(double val) {
		return (int)(Math.ceil(val/50.0))*50;
	}

	public static String getDistanceEstimate(double dist) {

		String out = "";
		if(dist < 10) {
			out = "Right here";
		} else if(dist < 100) {
			out = "About " + ((int)Math.ceil(dist/10.0))*10 + "m away";
		} else if(dist < 950) {
			out = "About " + ((int)Math.ceil(dist/50.0))*50 + "m away";
		} else if(dist < 10000) {
			double num = (Math.ceil(dist/100.0))/10.0;
			if(num == 1.0) out = "About "  + (int)num + "km away";
			else out = num + "km away";
		} else {
			out = (int)(Math.ceil(dist/100.0)/10.0) + "km away";
		}

		return out;
	}

	/*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 * 
	 * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
	 * el2 End altitude in meters
	 */
	public static double getLatLongDistance (double lat1, double lon1, double lat2, double lon2,
			double el1, double el2) {

		final int R = 6371; // Radius of the earth

		Double latDistance = deg2rad(lat2 - lat1);
		Double lonDistance = deg2rad(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;
		distance = Math.pow(distance, 2) + Math.pow(height, 2);
		return Math.sqrt(distance);
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * This function opens the standard android sharing context to let you choose a provider to share a bubble with
	 * Performs special operations for twitter & facebook!
	 * @param bubble
	 * @param context
	 */
	public static void shareBubble(BubbleModel bubble, Context context) {
		// We need to gather all providers for our intent so that we can set texts especially for facebook & twitter in order to overcome their limitations
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		
		// "Probing" intent
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, PackageManager.MATCH_DEFAULT_ONLY); // Get all platforms which can perform this action
		
		// Set up url
		String url = Constants.bubbleShareURL + "?id=";
		
		// Go through all apps we have just gathered & set a custom intent for each of them
		// Facebook needs a custom intent because it wants devs to user their api so we *can't natively send text to the app*!
		// twitter: 140chars only
		// the rest: our full text
		for(final ResolveInfo app : activityList) {
			
			String packageName = app.activityInfo.packageName;
			String shareSubject = "I'd like to share a bubble with you!"; // Mainly for emails. Ignored by most other apps (whatsapp actually uses it)
			Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
			targetedShareIntent.setType("text/plain");
			targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
			

			if("com.facebook.katana".equalsIgnoreCase(packageName)) { // Facebook: link to bubble only
				targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, url + bubble.getBubbleUUID());
			} else if("com.twitter.android".equalsIgnoreCase(packageName)) { // Twitter: shorter text
				String name = bubble.getUserForename();
				if(name.length() > 35) name = name.substring(0, 31) + "...";
				String shareBody =
						"Have a look at this bubble by "
								+ name
								+ "!\n"
								+ url
								+ bubble.getBubbleUUID()
								+ "\n#basewarp";
				targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			} else { // Everyone else: long text
				String shareBody =
						"Hey!\n\n"
								+ bubble.getUserForename()
								+ " has left a bubble at \""
								+ bubble.getStreetAddress()
								+ "\", which I thought you may find interesting!\n\nClick here to view it:\n"
								+ url
								+ bubble.getBubbleUUID();
				targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			}

			targetedShareIntent.setPackage(packageName);
			targetedShareIntents.add(targetedShareIntent);
		}
		
		// Now we open the "normal" provider-selector, with the difference that new each selection sends out a different intent. YAY!
		Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share bubble by " + bubble.getUserForename());
	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
	    context.startActivity(chooserIntent);
	}
}
