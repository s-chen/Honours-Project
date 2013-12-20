package si.chen.honours.project;

import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
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
	private LatLng restaurant_lat_lng;

	private GPSListener gps;
	private Location user_location;
	private Location restaurant_location;
	private double user_latitude;
	private double user_longitude;
	private int distance;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);
		
		setTitle("View information");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
		
		// Check if GPS enabled
		if (gps.canGetLocation()) {
	
			// get user's current location
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
			
		} else {
			// GPS or network not enabled, ask user to enable GPS/network in settings menu
			gps.showSettingsAlert();
		}
	
		
		
		/** Get restaurant data passed from Restaurants.java class **/
		Log.i("RetrieveData", "Retrieve selected restaurant data.");
        restaurantIntent = getIntent();
        restaurant_id = restaurantIntent.getIntExtra("KEY_ID", 0);
        restaurant_name = restaurantIntent.getStringExtra("KEY_NAME");
        services = restaurantIntent.getStringExtra("KEY_SERVICES");
        restaurant_latitude = restaurantIntent.getDoubleExtra("KEY_LATITUDE", 0);
        restaurant_longitude = restaurantIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		restaurant_url = restaurantIntent.getStringExtra("KEY_CONTENT_URL");
		
		// Get restaurant lat, lng coordinates from intent
		restaurant_lat_lng = new LatLng(restaurant_latitude, restaurant_longitude);
				
		// Display map fragment, with marker of restaurant location
		restaurant_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.restaurant_map)).getMap();
		Marker location = restaurant_map.addMarker(new MarkerOptions().position(restaurant_lat_lng).title(restaurant_name + " (" + services + ")"));
		restaurant_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 17));
				
		// Display current user's location
		restaurant_map.setMyLocationEnabled(true);
		
		
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
		
		Log.d("DISTANCE_TO", Integer.toString(distance));
	
		
		// Display distance information in TextView
		TextView distance_information = (TextView) findViewById(R.id.textView_distance_info);
		distance_information.setText("Distance to destination: " + distance + "m");
		
		
		System.out.println("RESTAURANT_URL: " + restaurant_url);
		
		
		TextView restaurant_content_url = (TextView) findViewById(R.id.textView_restaurant_content_url);
		// Display restaurant website url, if it exists
		if (restaurant_url.equals("")) {
			restaurant_content_url.setVisibility(View.INVISIBLE);
		} else {
			restaurant_content_url.setVisibility(View.VISIBLE);
			restaurant_content_url.setText("Website (Click to view): " + restaurant_url);
		}
		
		
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
    public void onResume() {
    	super.onResume();
    	gps.getLocation();
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
