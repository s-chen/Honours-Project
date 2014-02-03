package si.chen.honours.project.ui;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// Display Google Maps with current user location
public class MapActivity extends Activity {

	private GoogleMap map;
	private GPSListener gps;
	final LatLng EDINBURGH = new LatLng(55.9531, -3.1889);
	LatLng current_user_location;
	Marker user_location_marker;
	Marker edinburgh_marker;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Map");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Open Google Maps 
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
				
		// Check if internet connection is available AND that GPS is enabled
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
			
			// get user's current location
			current_user_location = new LatLng(gps.getLatitude(), gps.getLongitude());
			
			// Add marker
			user_location_marker = map.addMarker(new MarkerOptions()
					.position(current_user_location)
					.title("You Are Here")
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));	
			user_location_marker.showInfoWindow();
			
			// Zoom in to user location
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location_marker.getPosition(), 15));
			map.setMyLocationEnabled(true);
			
		// If only internet connection is available, GPS is not enabled, set marker to Edinburgh
		} else if (gps.isConnectionAvailable()) {
			
			// GPS or network not enabled, ask user to enable GPS/network in settings menu
			gps.showGPSSettingsAlert();
			
			// Add marker
			edinburgh_marker = map.addMarker(new MarkerOptions()
					.position(EDINBURGH)
					.title("Edinburgh"));
			edinburgh_marker.showInfoWindow();
			
			// Zoom in to Edinburgh
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(edinburgh_marker.getPosition(), 12));
			
		} else if (!gps.canGetLocation()){
			
			// GPS or network not enabled, ask user to enable GPS/network in settings menu
			gps.showGPSSettingsAlert();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
			return true;
		case R.id.action_refresh:
			refresh();
			return true;
        case R.id.action_home:
        	// Go to Main Menu
            Intent homeIntent = new Intent(this, MainMenu.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
		default:
		      return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    public void onBackPressed() {
    	
    	Intent intent = new Intent(this, MainMenu.class);
    	startActivity(intent);
    	finish();
    }
    
    // Refresh activity - called when 'Refresh' action button clicked
    public void refresh() {
    	
    	Intent intent = getIntent();
    	overridePendingTransition(0, 0);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

   	    overridePendingTransition(0, 0);
   	    startActivity(intent);
    }  
}
