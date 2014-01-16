package si.chen.honours.project.ui;

import java.io.IOException;

import si.chen.honours.project.*;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.utility.DatabaseHelper;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

// Main Menu of app
public class MainMenu extends ActionBarActivity {
	
	private GPSListener gps;
	
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
		
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
		
		// Show Internet Connection Settings if no Wifi/Mobile network detected
		if (!gps.isConnectionAvailable()) {
			gps.showInternetSettingsAlert();
		}
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
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
    	Log.i("GooglePlayVersionCheck", "resultCode: " + resultCode);
    	
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	
    	// Show Internet Connection Settings if no Wifi/Mobile network detected
    	if (!gps.isConnectionAvailable()) {
    		gps.showInternetSettingsAlert();
    	}
    }
    
    @Override
    public void onBackPressed() {
    	finish();
    	Intent homeIntent = new Intent(Intent.ACTION_MAIN);
    	homeIntent.addCategory(Intent.CATEGORY_HOME);
    	startActivity(homeIntent);
    }
    
    
    // Called when 'Food' button is clicked
    public void restaurants(View view) {
    	
    	Intent intent = new Intent(this, Restaurants.class);
    	startActivity(intent);
    	finish();
    } 
    
    // Called when 'Drinks' button is clicked
    public void drinks(View view) {
    
    	Intent intent = new Intent(this, Drinks.class);
    	startActivity(intent);
    	finish();
    }
      
    // Called when 'Tourist Attractions' button is clicked
    public void touristAttractions(View view) {
    	
    	Intent intent = new Intent(this, Attractions.class);
    	startActivity(intent);
    	finish();
    }
    
    // Called when 'Accommodation' button is clicked
    public void accommodation(View view) {
    	
    	Intent intent = new Intent(this, Accommodation.class);
    	startActivity(intent);
    	finish();
    }
    
    // Called when 'Shopping' button is clicked
    public void shops(View view) {
    	
    	Intent intent = new Intent(this, Shops.class);
    	startActivity(intent);
    	finish();
    }
    
    // Called when 'Nearby Places' button is clicked
    public void nearbyPlaces(View view) {
    	
    	Intent intent = new Intent(this, DisplayNearbyPlaces.class);
    	startActivity(intent);
    	finish();
    }
    
    // Called when 'Itinerary Planner' button is clicked
    public void itineraryPlanner(View view) {
    	
    	Intent intent = new Intent(this, ItineraryPlanner.class);
    	startActivity(intent);
    	finish();
    }
}

