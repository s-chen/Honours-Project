package si.chen.honours.project;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/** Details of the particular attraction - called when the name is selected in the ListView **/
public class AttractionsInfo extends ActionBarActivity {

	private GoogleMap attractions_map;
	private Intent attractionsIntent;
	private int attractions_id;
	private String attractions_name;
	private String services;
	private double attractions_latitude;
	private double attractions_longitude;
	private String attractions_url;

	private GPSListener gps;
	private Location user_location;
	private Location attractions_location;
	private double user_latitude;
	private double user_longitude;
	private int distance;
	private StringBuilder formatted_attractions_address;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attractions_info);
		
		setTitle("View information");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		// Get attractions data passed from Attractions.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected attractions data.");
		attractionsIntent = getIntent();
		attractions_id = attractionsIntent.getIntExtra("KEY_ID", 0);
		attractions_name = attractionsIntent.getStringExtra("KEY_NAME");
		services = attractionsIntent.getStringExtra("KEY_SERVICES");
		attractions_latitude = attractionsIntent.getDoubleExtra("KEY_LATITUDE", 0);
        attractions_longitude = attractionsIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		attractions_url = attractionsIntent.getStringExtra("KEY_CONTENT_URL");
		
		

		// Create instance of GPSListener
		gps = new GPSListener(this);
		
		// Check if GPS enabled
		if (gps.canGetLocation()) {
	
			// get user's current location
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
			
		} else {
			// GPS or network not enabled, ask user to enable GPS/network in settings menu
			gps.showGPSSettingsAlert();
		}
		
		
		// Set lat, lng coordinates for attractions location 
		attractions_location = new Location("attractions_location");
		attractions_location.setLatitude(attractions_latitude);
		attractions_location.setLongitude(attractions_longitude);
				
		// Set lat, lng coordinates for user location 
		user_location = new Location("user_location");
		user_location.setLatitude(user_latitude);
		user_location.setLongitude(user_longitude);
				
		// Calculate distance from user's current location to attraction
		distance = (int) user_location.distanceTo(attractions_location);
		
		// Display distance information in TextView
		TextView distance_information = (TextView) findViewById(R.id.textView_distance_info_attractions);
		if (gps.isConnectionAvailable() && gps.canGetLocation) {
			distance_information.setText("Distance to destination: " + distance + "m");
		} else {
				distance_information.setText("Distance information not available");
		}
		
		if (gps.isConnectionAvailable() && gps.canGetLocation) {
			// Uses reverse Geocoding to obtain address of attraction from the lat, lng coordinates
			Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
			try {
        	
				List<Address> addresses = geocoder.getFromLocation(attractions_latitude, attractions_longitude, 1);
            
				if (addresses.size() > 0) {
            	
					Address attractions_address = addresses.get(0);
					formatted_attractions_address = new StringBuilder("Address:\n");
                
					// Adds each address line to the string
					for (int i = 0; i < attractions_address.getMaxAddressLineIndex(); i++) {
						formatted_attractions_address.append(attractions_address.getAddressLine(i)).append("\n");
					}
                          
				} else {
					Log.d("NO_ATTRACTIONS_ADDRESS", "No attractions address found");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			formatted_attractions_address = new StringBuilder("Address information not available\n");
		}
		
		TextView attractions_address_website_info = (TextView) findViewById(R.id.textView_attractions_address_website_info);
        // Display attractions address in TextView
    	attractions_address_website_info.setText(formatted_attractions_address);
        
		// Also display attractions website url in TextView, if it exists.
		if (attractions_url.equals("")) {
			// Do nothing
			Log.d("NO_ATTRACTIONS_URL", "Attractions url does not exist");
		} else {
			StringBuilder attractions_address_and_url = formatted_attractions_address.append("\n").append("Website:").append("\n").append(attractions_url);
			attractions_address_website_info.setText(attractions_address_and_url);
		}
		
		
		
		// Display map fragment, with marker of attractions location given by lat, lng coordinates
     	attractions_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.attractions_map)).getMap();
     	LatLng attractions_lat_lng = new LatLng(attractions_latitude, attractions_longitude);
     	Marker location = attractions_map.addMarker(new MarkerOptions()
     				.position(attractions_lat_lng).snippet(formatted_attractions_address.toString())
     				.title(attractions_name + " (" + services + ")"));
     	attractions_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's location
     	attractions_map.setMyLocationEnabled(true);
		
	}
	
	// Called when 'Search Web' button is clicked
	public void searchAttractionsOnline(View view) {
		
		String query = attractions_name + " " + services + ", Edinburgh"  ;
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query); // query contains search string
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attractions_info, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, Attractions.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            finish();
				return true;
			case R.id.action_refresh:
				refresh();
				return true;
			default:
			      return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    public void onBackPressed() {
    	
    	gps.stopUsingGPS();
    	
    	Intent intent = new Intent(this, Attractions.class);
    	startActivity(intent);
    	finish();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	gps.stopUsingGPS();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();;
    	gps.stopUsingGPS();
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	
    	// Show GPS settings menu, if not detected
    	if (!gps.canGetLocation) {
    		gps.showGPSSettingsAlert();
    	}
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
