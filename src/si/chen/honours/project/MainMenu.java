package si.chen.honours.project;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;





import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainMenu extends ActionBarActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        /** Populate Edinburgh POI database on Android System
        using database file from assets folder **/
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		try {
			dbHelper.createDatabase();
		} catch (IOException e) {
			System.out.println("Unable to create database");
		}
		
		
		// Show Internet Connection Settings if no Wifi/Mobile network detected
		if (!isConnectionAvailable()) {
			showInternetSettingsAlert();
		}
		
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint("Search Edinburgh");       
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_maps:
            	Toast.makeText(getApplicationContext(), "Opening Google Maps", Toast.LENGTH_SHORT).show();
            	Intent intentMap = new Intent(this, MapActivity.class);
            	startActivity(intentMap);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// Get status code
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
    	
    	/* Checks whether Google Play Services APK is up-to-date and direct user to Google Play store
    	if Google Play services is out of date or missing or to system settings if service is disabled */
    	if(resultCode != ConnectionResult.SUCCESS) {
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
    		dialog.show();
    	} 
    	Log.d("GooglePlayVersionCheck", "resultCode: " + resultCode);
    }
    
    // Called when 'Food' button is clicked
    public void restaurants(View view) {
    	
    	Intent intent = new Intent(this, Restaurants.class);
    	startActivity(intent);
    	finish();
    } 
    
    
    // Check whether Wifi/Mobile Network is available
    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
        	Log.d("INTERNET_CONNECTION_AVAILABLE", "Internet connection available");
        	return true;
        }
        Log.d("INTERNET_CONNECTION_UNAVAILABLE", "No internet connection available");
        return false;
    }
    
    
    /** Function shows settings alert dialog **/
    public void showInternetSettingsAlert() {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    
    	// Setting Dialog Title
    	alertDialog.setTitle("Internet Connection Settings");

    	// Setting Dialog Message
    	alertDialog.setMessage("Wifi or Network connection not currently enabled."
    			+ "\nThe application will not be able to provide location-based information." 
    			+ "\nDo you want to go to Settings menu to switch on Wifi/Mobile Network?");

    	// Pressing 'Settings' button
    	alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
    			startActivity(intent);
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
    
}

