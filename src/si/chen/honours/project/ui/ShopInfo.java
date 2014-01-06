package si.chen.honours.project.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import si.chen.honours.project.R;
import si.chen.honours.project.R.id;
import si.chen.honours.project.R.layout;
import si.chen.honours.project.R.menu;
import si.chen.honours.project.location.GPSListener;

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


/** Details of the particular shop - called when the name is selected in the ListView **/
public class ShopInfo extends ActionBarActivity {

	private GoogleMap shop_map;
	private Intent shopIntent;
	private int shop_id;
	private String shop_name;
	private String services;
	private double shop_latitude;
	private double shop_longitude;
	private String shop_url;

	private GPSListener gps;
	private Location user_location;
	private Location shop_location;
	private double user_latitude;
	private double user_longitude;
	private int distance;
	private StringBuilder formatted_shop_address;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_info);
		
		setTitle("View information");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		// Get shop data passed from Shops.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected shop data.");
		shopIntent = getIntent();
		shop_id = shopIntent.getIntExtra("KEY_ID", 0);
		shop_name = shopIntent.getStringExtra("KEY_NAME");
		services = shopIntent.getStringExtra("KEY_SERVICES");
		shop_latitude = shopIntent.getDoubleExtra("KEY_LATITUDE", 0);
        shop_longitude = shopIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		shop_url = shopIntent.getStringExtra("KEY_CONTENT_URL");
		
		

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
		
		
		// Set lat, lng coordinates for shop location 
		shop_location = new Location("shop_location");
		shop_location.setLatitude(shop_latitude);
		shop_location.setLongitude(shop_longitude);
				
		// Set lat, lng coordinates for user location 
		user_location = new Location("user_location");
		user_location.setLatitude(user_latitude);
		user_location.setLongitude(user_longitude);
				
		// Calculate distance from user's current location to shop
		distance = (int) user_location.distanceTo(shop_location);
		
		// Display distance information in TextView
		TextView distance_information = (TextView) findViewById(R.id.textView_distance_info_shop);
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
			distance_information.setText("Distance to destination: " + distance + "m\n");
		} else {
			distance_information.setText("Distance information not available\n");
		}
		
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
			// Uses reverse Geocoding to obtain address of shop from the lat, lng coordinates
			Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
			try {
        	
				List<Address> addresses = geocoder.getFromLocation(shop_latitude, shop_longitude, 1);
            
				if (addresses.size() > 0) {
            	
					Address shop_address = addresses.get(0);
					formatted_shop_address = new StringBuilder("Address:\n");
                
					// Adds each address line to the string
					for (int i = 0; i < shop_address.getMaxAddressLineIndex(); i++) {
						formatted_shop_address.append(shop_address.getAddressLine(i)).append("\n");
					}
                          
				} else {
					Log.d("NO_SHOP_ADDRESS", "No shop address found");
				}
			} catch (IOException e) {
				formatted_shop_address = new StringBuilder("Geocoder service not available - please try rebooting device\n");
				Log.d("GEOCODER_FAILED", "Geocoder Service not available - reboot device or use Google Geocoding API");
				e.printStackTrace();
			}
		} else {
			formatted_shop_address = new StringBuilder("Address information not available\n");
		}
		
		TextView shop_address_website_info = (TextView) findViewById(R.id.textView_shop_address_website_info);
        // Display shop address in TextView
    	shop_address_website_info.setText(formatted_shop_address);
        
		// Also display shop website url in TextView, if it exists.
		if (shop_url.equals("")) {
			// Do nothing
			Log.d("NO_SHOP_URL", "Shop url does not exist");
		} else {
			StringBuilder shop_address_and_url = formatted_shop_address.append("\n").append("Website:").append("\n").append(shop_url);
			shop_address_website_info.setText(shop_address_and_url);
		}
		
		
		
		// Display map fragment, with marker of shop location given by lat, lng coordinates
     	shop_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.shop_map)).getMap();
     	LatLng shop_lat_lng = new LatLng(shop_latitude, shop_longitude);
     	Marker location = shop_map.addMarker(new MarkerOptions()
     				.position(shop_lat_lng).snippet(formatted_shop_address.toString())
     				.title(shop_name + " (" + services + ")"));
     	shop_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's location
     	shop_map.setMyLocationEnabled(true);
		
	}
	
	// Called when 'Search Web' button is clicked
	public void searchShopOnline(View view) {
		
		String query = shop_name + " " + services + ", Edinburgh"  ;
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query); // query contains search string
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_info, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, Shops.class);
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
    	
    	Intent intent = new Intent(this, Shops.class);
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
    	super.onDestroy();
    	gps.stopUsingGPS();
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	
    	// Show GPS settings menu, if not detected
    	if (!gps.canGetLocation()) {
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
