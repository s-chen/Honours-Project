package com.basewarp.basewarp.location;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import android.support.v7.app.ActionBarActivity;
//import com.actionbarsherlock.app.SherlockFragmentActivity;
//import com.basewarp.basewarp.ui.MainActivity;
//import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class GPSManager extends Service implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private static GPSManager instance;
	//private static SherlockFragmentActivity mContext;
	
	private static ActionBarActivity mContext;
	
	private static ArrayList<GPSListener> locationChangedlisteners;
	
	private static LocationClient mLocationClient;
	private static LocationRequest mLocationRequest;

	private static Location realLocation = null;
	private static Location warpLocation = null;
	
	private static boolean warpedLocationSet = false;
	private static boolean isConnected = false;
	
	public static void setLocationChangeListener(GPSListener listener) {
		if(!locationChangedlisteners.contains(listener)) {
			locationChangedlisteners.add(listener);
		}
	}
	
	public static void removeLocationChangeListener(GPSListener listener) {
		if(locationChangedlisteners.contains(listener)) {
			locationChangedlisteners.remove(listener);
		}
	}
	
	public static void removeLocationChangeListenersOfType(Class c) {
		for(GPSListener l : locationChangedlisteners) {
			if(l.getClass().equals(c)) locationChangedlisteners.remove(l);
		}
	}

	public static Location getLocation() {
		if(warpedLocationSet) return warpLocation;
		if(!warpedLocationSet && isConnected) realLocation = mLocationClient.getLastLocation();
		return realLocation;
	}
	
	public static Location getRealLocation() {
		return realLocation;
	}
	
	public static Location getWarpedLocation() {
		return warpLocation;
	}

	public void onLocationChanged(Location newLocation) {
		Log.w("locationupdate",String.valueOf(newLocation.getAccuracy()));

		HashMap<String,String> log = new HashMap<String,String>();
		log.put("accuracy", String.valueOf(newLocation.getAccuracy()));
		log.put("lat", String.valueOf(newLocation.getLatitude()));
		log.put("long", String.valueOf(newLocation.getLongitude()));
		//FlurryAgent.logEvent("accuracy",log);

		// Check whether we have wanted updates "quick". We can now reduce their frequency
		if(!warpedLocationSet && mLocationRequest.getPriority() == LocationRequest.PRIORITY_HIGH_ACCURACY) {
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			mLocationRequest.setFastestInterval(20000);
			mLocationClient.requestLocationUpdates(mLocationRequest, instance);
		}
		
		GPSManager.realLocation = newLocation;

		ArrayList<GPSListener> removals = new ArrayList<GPSListener>();
		for(GPSListener l : locationChangedlisteners) {
			l.onLocationFound(newLocation);
			if(l.removeAfterUpdate()) removals.add(l);
		}
		
		for(GPSListener l : removals) {
			locationChangedlisteners.remove(l);
		}
	}

	public static void init(ActionBarActivity context) {
		instance = new GPSManager();
		GPSManager.locationChangedlisteners = new ArrayList<GPSListener>();
		GPSManager.mContext = context;
		mLocationClient = new LocationClient(mContext, instance, instance);
		servicesConnected();
		GPSManager.startUpdatingLocation();
	}

	public static void startUpdatingLocation() {
		Log.w("location","starting updates");
		mLocationClient.connect();
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public static void stopUpdatingLocation(){
		Log.w("location","stopping updates");
		isConnected = false;
		mLocationClient.disconnect();
	}

	/**
	 * Set the location manually (basewarping). Have to disable updates & make sure location isn't overridden anymore
	 * @param latlong
	 */
	public static void setWarpedLocation(double[] latlong) {
		warpedLocationSet = true;
		stopUpdatingLocation();
		
		if(warpLocation == null) warpLocation = new Location("MANUAL");
		warpLocation.setLatitude(latlong[0]);
		warpLocation.setLongitude(latlong[1]);
		warpLocation.setAccuracy(0);
		
		if (locationChangedlisteners != null && locationChangedlisteners.size() > 0) {
			// Inform them we've found a location
			for(GPSListener l : locationChangedlisteners) l.onLocationFound(warpLocation);
		}
	}

	public static boolean isLocationWarped() {
		return warpedLocationSet;
	}

	public static boolean mockLocationsAllowed() {
		if (Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) return false;
		else return true;
	}

	public static boolean isLocationFromHardwareSource() {
		if( realLocation.getProvider() == LocationManager.GPS_PROVIDER ||
				realLocation.getProvider() == LocationManager.NETWORK_PROVIDER) return true;
		else return false;
	}

	public static void toggleLocationLock() {
		if(warpedLocationSet) {
			warpedLocationSet = !warpedLocationSet;
			startUpdatingLocation();
		} else {
			warpedLocationSet = !warpedLocationSet;
			stopUpdatingLocation();
		}
	}

	
	 //* Called by Location Services when the request to connect the
	 //* client finishes successfully. At this point, you can
	 //* request the current location or start periodic updates
	 
	//@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Log.w("location", "connected");
		isConnected = true;
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setInterval(1000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationClient.requestLocationUpdates(mLocationRequest, instance);
	}

	
	// * Called by Location Services if the connection to the
	// * location client drops because of an error.
	 
	//@Override
	public void onDisconnected() {
		isConnected = false;
	}

	
	// * Called by Location Services if the attempt to
	// * Location Services fails.
	 
	//@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		
		// * Google Play services can resolve some errors it detects.
		// * If the error has a resolution, try sending an Intent to
		// * start a Google Play services activity that can resolve
		// * error.
		 
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(mContext,CONNECTION_FAILURE_RESOLUTION_REQUEST);
				
				// * Thrown if Google Play services canceled the original
				// * PendingIntent
				 
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			
			// * If no resolution is available, display a dialog to the
			// * user with the error.
			 
			//         showErrorDialog(connectionResult.getErrorCode());
		}
	}

	private static boolean servicesConnected() {
		int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if (errorCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(errorCode, mContext, 0).show();
			return false;
		}
		return true;
	}

	/**
	 * Function to check whether we can get a location fix
	 * @return boolean
	 * */
	public static boolean checkCanGetLocation() {
		
		LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		boolean hwSourcesEnabled = false;
		try{
			hwSourcesEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}catch(Exception ex){}
		try{
			hwSourcesEnabled = hwSourcesEnabled || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}catch(Exception ex){}

		return (servicesConnected() && hwSourcesEnabled) || (warpedLocationSet && warpLocation != null);
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will launch Settings Options
	 * */
	public static void showSettingsAlert(){
		// Build an alert dialog to take the user to location settings
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("Location services disabled");
		alertDialog.setMessage("Location services are required for Right Here. Please go to settings and activate location services to continue.");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				//((MainActivity) mContext).finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	/**
	 * Function to show settings alert dialog from a different context!
	 * On pressing Settings button will launch Settings Options
	 * */
	public static void showSettingsAlert(final Context mContext){
		// Build an alert dialog to take the user to location settings
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("Location services disabled");
		alertDialog.setMessage("Location services are required for Right Here. Please go to settings and activate location services to continue.");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				//((MainActivity) mContext).finish();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}