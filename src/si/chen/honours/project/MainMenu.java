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
       
}

