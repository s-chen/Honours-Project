package si.chen.honours.project;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/** Details of the particular restaurant - called when a restaurant is selected in the ListView **/
public class RestaurantInfo extends ActionBarActivity {

	private GoogleMap restaurant_map;
	private Intent restaurantIntent;
	private int restaurant_id;
	private String restaurant_name;
	private String services;
	private double restaurant_latitude;
	private double restaurant_longitude;
	private String restaurant_url;

	private GPSListener gps;
	private Location user_location;
	private Location restaurant_location;
	private double user_latitude;
	private double user_longitude;
	private int distance;
	private StringBuilder formatted_restaurant_address;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);
		
		setTitle("View information");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		// Get restaurant data passed from Restaurants.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected restaurant data.");
		restaurantIntent = getIntent();
		restaurant_id = restaurantIntent.getIntExtra("KEY_ID", 0);
        restaurant_name = restaurantIntent.getStringExtra("KEY_NAME");
        services = restaurantIntent.getStringExtra("KEY_SERVICES");
        restaurant_latitude = restaurantIntent.getDoubleExtra("KEY_LATITUDE", 0);
        restaurant_longitude = restaurantIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		restaurant_url = restaurantIntent.getStringExtra("KEY_CONTENT_URL");
		
		
		
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
		
		
		
		// Set lat, lng coordinates for restaurant location 
		restaurant_location = new Location("restaurant_location");
		restaurant_location.setLatitude(restaurant_latitude);
		restaurant_location.setLongitude(restaurant_longitude);
		
		// Set lat, lng coordinates for user location 
		user_location = new Location("user_location");
		user_location.setLatitude(user_latitude);
		user_location.setLongitude(user_longitude);
		
		// Calculate distance from user's current location to restaurant
		distance = (int) user_location.distanceTo(restaurant_location);
		
		// Display distance information in TextView
		TextView distance_information = (TextView) findViewById(R.id.textView_distance_info_restaurant);
		if (gps.isConnectionAvailable() && gps.canGetLocation) {
			distance_information.setText("Distance to destination: " + distance + "m");
		} else {
			distance_information.setText("Distance information not available");
		}
		
		
	
		if (gps.isConnectionAvailable() && gps.canGetLocation) {
			// Uses reverse Geocoding to obtain address of restaurant from the restaurant lat, lng coordinates
			Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
			try {
        	
				List<Address> addresses = geocoder.getFromLocation(restaurant_latitude, restaurant_longitude, 1);
            
				if (addresses.size() > 0) {
            	
					Address restaurant_address = addresses.get(0);
					formatted_restaurant_address = new StringBuilder("Address:\n");
                
					// Adds each address line to the string
					for (int i = 0; i < restaurant_address.getMaxAddressLineIndex(); i++) {
						formatted_restaurant_address.append(restaurant_address.getAddressLine(i)).append("\n");
					}
                          
				} else {
					Log.d("NO_RESTAURANT_ADDRESS", "No restaurant address found");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			formatted_restaurant_address = new StringBuilder("Address information not available\n");
		}
        
        TextView restaurant_address_website_info = (TextView) findViewById(R.id.textView_restaurant_address_website_info);
        // Display restaurant address in TextView
    	restaurant_address_website_info.setText(formatted_restaurant_address);
        
		// Also display restaurant website url in TextView, if it exists.
		if (restaurant_url.equals("")) {
			// Do nothing
			Log.d("NO_RESTAURANT_URL", "Restaurant url does not exist");
		} else {
			StringBuilder restaurant_address_and_url = formatted_restaurant_address.append("\n").append("Website:").append("\n").append(restaurant_url);
			restaurant_address_website_info.setText(restaurant_address_and_url);
		}
        
        
        
		
     	// Display map fragment, with marker of restaurant location given by lat, lng coordinates
     	restaurant_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.restaurant_map)).getMap();
     	LatLng restaurant_lat_lng = new LatLng(restaurant_latitude, restaurant_longitude);
     	Marker location = restaurant_map.addMarker(new MarkerOptions()
     				.position(restaurant_lat_lng).snippet(formatted_restaurant_address.toString())
     				.title(restaurant_name + " (" + services + ")"));
     	restaurant_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's location
     	restaurant_map.setMyLocationEnabled(true);
     	
     	
     	
		
	}
	
	// Called when 'Search Web' button is clicked
	public void searchRestaurantOnline(View view) {
	
		String query = restaurant_name + " " + services + ", Edinburgh"  ;
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query); // query contains search string
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurant_info, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, Restaurants.class);
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
    	
    	Intent intent = new Intent(this, Restaurants.class);
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
