package si.chen.honours.project.ui;

import java.util.ArrayList;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// Show Nearby Places markers on Google Maps
public class DisplayNearbyPlacesMap extends ActionBarActivity {

	private GoogleMap nearby_places_map;
	private GPSListener gps;
	private double user_latitude;
	private double user_longitude;
	private LatLng current_user_location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_nearby_places_map);
		
		setTitle("Showing Nearby Places on map");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
		
		// Check gps enabled
		if (gps.canGetLocation()) {
			
			// get user's current location
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
			current_user_location = new LatLng(user_latitude, user_longitude);
			
		} else {
			gps.showGPSSettingsAlert();
		}
		
		// Open Google Maps 
		nearby_places_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearby_places_map)).getMap();
		
		// Add user location marker
		nearby_places_map.addMarker(new MarkerOptions()
			.position(current_user_location)
			.title("You Are Here")
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

		
		
		// Get place name, latitude, longitude ArrayLists from passed intent
		Intent intent = getIntent();
		ArrayList<String> place_names = intent.getStringArrayListExtra("KEY_PLACE_NAMES");
		ArrayList<String> place_latitudes = intent.getStringArrayListExtra("KEY_PLACE_LATITUDES");
		ArrayList<String> place_longitudes = intent.getStringArrayListExtra("KEY_PLACE_LONGITUDES");
			

		// Create LatLng Bounds Builder to ensure all markers fit in bounding box
		LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
		
		// Loop over ArrayList (all have same size) - add place name, location to a marker then add marker to map 
		for (int i = 0; i < place_names.size(); i++) {
			
			Log.d("PLACE_NAMES_MAP", place_names.get(i));
			Log.d("PLACE_LOCATIONS_MAP", place_latitudes.get(i) + " " + place_longitudes.get(i));
			
			// Get place location from current entry in ArrayList of latitudes, longitudes
			LatLng place_location = new LatLng(Double.valueOf(place_latitudes.get(i)), Double.valueOf(place_longitudes.get(i)));
			
			// Create marker with specified location, name
			Marker place_location_marker = nearby_places_map.addMarker(new MarkerOptions()
				.position(place_location)
				.title(place_names.get(i)));
			
			// Add marker to bounding box
			boundsBuilder.include(place_location_marker.getPosition());
		}
		// Build bounding box
		LatLngBounds bounds = boundsBuilder.build();
	
		// Move camera to display all markers within bounding box
		nearby_places_map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, getDisplayWidth(), getDisplayHeight(), 25));
	
		nearby_places_map.setMyLocationEnabled(true);
	}
	
	// Calculate screen display height in pixels
	public int getDisplayHeight() {
		
		DisplayMetrics dimension = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dimension);
	    
	    return dimension.heightPixels;
	}
	
	// Calculate screen display width in pixels
	public int getDisplayWidth() {
		
		DisplayMetrics dimension = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimension);
		
		return dimension.widthPixels;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_nearby_places_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, DisplayNearbyPlaces.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
			return true;
		default:
		      return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    public void onBackPressed() {
    	
    	Intent intent = new Intent(this, DisplayNearbyPlaces.class);
    	startActivity(intent);
    	finish();
    }
}
