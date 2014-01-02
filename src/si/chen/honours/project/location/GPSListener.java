package si.chen.honours.project.location;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

// Check GPS status and internet connection status
public class GPSListener extends Service implements LocationListener {

	private Context mContext;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	double latitude;
	double longitude; 
	
    // Minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 metres

    // Minimum time between updates in milliseconds
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60; // 60s
    
    protected LocationManager locationManager;
    
    // Constructor
    public GPSListener(Context context) {
        this.mContext = context;
        getLocation();
    }
    
    // Get user's current location
    public Location getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // check GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // check network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            	Log.d("GPS_NETWORK", "No GPS or Network detected.");
            } else {
            	
                this.canGetLocation = true;
                
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    
                    Log.d("NETWORK_ENABLED", "Network connection enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        
                        Log.d("GPS_ENABLED", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Log.d("CURRENT_USER_LAT_LNG", "\nLat:" + latitude + "\nLng:" + longitude);

        return location;
    }
    
    /**
     Stop using GPS listener
     This function will stop using GPS **/
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSListener.this);
        }       
    }
   
    // Get user location latitude
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    // Get user location longitude
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
   
    
    /** Function to check GPS/Location Services enabled **/
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
   
    
    // Check whether Wifi/Mobile Network is available
    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
        	Log.d("INTERNET_CONNECTION_AVAILABLE", "Internet connection available");
        	return true;
        }
        Log.d("INTERNET_CONNECTION_UNAVAILABLE", "No internet connection available");
        return false;
    }
    
    
    /** Function shows GPS settings alert dialog **/
    public void showGPSSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled."
        		+ " The application will not be able to provide location-based information."
        		+ "\nDo you want to go to settings menu to enable GPS/Location Services?");

        // Pressing 'Settings' button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // Pressing 'Cancel' button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    
    
    /** Function shows Internet Connection settings alert dialog **/
    public void showInternetSettingsAlert() {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
    
    	// Setting Dialog Title
    	alertDialog.setTitle("Internet Connection Settings");

    	// Setting Dialog Message
    	alertDialog.setMessage("Wifi or Network connection not enabled."
    			+ " The application will not be able to provide location-based information." 
    			+ "\nDo you want to go to Settings menu to enable Wifi/Mobile Network?");

    	// Pressing 'Settings' button
    	alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
    			mContext.startActivity(intent);
    		}
    	});

    	// Pressing 'Cancel' button
    	alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.cancel();
    		}
    	});

    	// Showing Alert Message
    	alertDialog.show();
    }
  


    
    // Update user location (called automatically)
    public void onLocationChanged(Location location) {
    	Log.d("LOCATION_UPDATE", "User location updated");
    	//getLocation();
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
}
