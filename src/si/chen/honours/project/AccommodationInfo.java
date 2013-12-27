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
import android.widget.Toast;


/** Details of the particular accommodation - called when the name is selected in the ListView **/
public class AccommodationInfo extends ActionBarActivity {

	private GoogleMap accommodation_map;
	private Intent accommodationIntent;
	private int accommodation_id;
	private String accommodation_name;
	private String services;
	private double accommodation_latitude;
	private double accommodation_longitude;
	private String accommodation_url;

	private GPSListener gps;
	private Location user_location;
	private Location accommodation_location;
	private double user_latitude;
	private double user_longitude;
	private int distance;
	private StringBuilder formatted_accommodation_address;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accommodation_info);
		
		setTitle("View information");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		// Get accommodation data passed from Accommodation.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected accommodation data.");
		accommodationIntent = getIntent();
		accommodation_id = accommodationIntent.getIntExtra("KEY_ID", 0);
		accommodation_name = accommodationIntent.getStringExtra("KEY_NAME");
		services = accommodationIntent.getStringExtra("KEY_SERVICES");
		accommodation_latitude = accommodationIntent.getDoubleExtra("KEY_LATITUDE", 0);
        accommodation_longitude = accommodationIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		accommodation_url = accommodationIntent.getStringExtra("KEY_CONTENT_URL");
		
		

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
		
		
		// Set lat, lng coordinates for accommodation location 
		accommodation_location = new Location("accommodation_location");
		accommodation_location.setLatitude(accommodation_latitude);
		accommodation_location.setLongitude(accommodation_longitude);
				
		// Set lat, lng coordinates for user location 
		user_location = new Location("user_location");
		user_location.setLatitude(user_latitude);
		user_location.setLongitude(user_longitude);
				
		// Calculate distance from user's current location to accommodation
		distance = (int) user_location.distanceTo(accommodation_location);
		
		// Display distance information in TextView
		TextView distance_information = (TextView) findViewById(R.id.textView_distance_info_accommodation);
		if (gps.isConnectionAvailable() && gps.canGetLocation) {
			distance_information.setText("Distance to destination: " + distance + "m\n");
		} else {
			distance_information.setText("Distance information not available\n");
		}
		
		if (gps.isConnectionAvailable() && gps.canGetLocation) {
			// Uses reverse Geocoding to obtain address of accommodation from the lat, lng coordinates
			Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
			try {
        	
				List<Address> addresses = geocoder.getFromLocation(accommodation_latitude, accommodation_longitude, 1);
            
				if (addresses.size() > 0) {
            	
					Address accommodation_address = addresses.get(0);
					formatted_accommodation_address = new StringBuilder("Address:\n");
                
					// Adds each address line to the string
					for (int i = 0; i < accommodation_address.getMaxAddressLineIndex(); i++) {
						formatted_accommodation_address.append(accommodation_address.getAddressLine(i)).append("\n");
					}
                          
				} else {
					Log.d("NO_ACCOMMODATION_ADDRESS", "No accommodation address found");
				}
			} catch (IOException e) {
				formatted_accommodation_address = new StringBuilder("Geocoder service not available - please try rebooting device\n");
				Log.d("GEOCODER_FAILED", "Geocoder Service not available - reboot device or use Google Geocoding API");
				e.printStackTrace();
			}
		} else {
			formatted_accommodation_address = new StringBuilder("Address information not available\n");
		}
		
		TextView accommodation_address_website_info = (TextView) findViewById(R.id.textView_accommodation_address_website_info);
        // Display accommodation address in TextView
    	accommodation_address_website_info.setText(formatted_accommodation_address);
        
		// Also display accommodation website url in TextView, if it exists.
		if (accommodation_url.equals("")) {
			// Do nothing
			Log.d("NO_ACCOMMODATION_URL", "Accommodation url does not exist");
		} else {
			StringBuilder accommodation_address_and_url = formatted_accommodation_address.append("\n").append("Website:").append("\n").append(accommodation_url);
			accommodation_address_website_info.setText(accommodation_address_and_url);
		}
		
		
		
		// Display map fragment, with marker of accommodation location given by lat, lng coordinates
     	accommodation_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.accommodation_map)).getMap();
     	LatLng accommodation_lat_lng = new LatLng(accommodation_latitude, accommodation_longitude);
     	Marker location = accommodation_map.addMarker(new MarkerOptions()
     				.position(accommodation_lat_lng).snippet(formatted_accommodation_address.toString())
     				.title(accommodation_name + " (" + services + ")"));
     	accommodation_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's location
     	accommodation_map.setMyLocationEnabled(true);
		
	}
	
	// Called when 'Search Web' button is clicked
	public void searchAccommodationOnline(View view) {
		
		String query = accommodation_name + " " + services + ", Edinburgh"  ;
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query); // query contains search string
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accommodation_info, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, Accommodation.class);
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
    	
    	Intent intent = new Intent(this, Accommodation.class);
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
