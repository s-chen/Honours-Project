package si.chen.honours.project.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.utility.UserSessionManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/** Details of the particular shop - called when the name is selected in the ListView **/
public class ShopInfo extends Activity {

	private GoogleMap shop_map;
	private Intent shopIntent;
	private int shop_id;
	private String shop_name;
	private String services;
	private double shop_latitude;
	private double shop_longitude;
	private String shop_url;
	private int shop_item_position;
	private String shop_type;

	private GPSListener gps;
	private Location user_location;
	private Location shop_location;
	private double user_latitude;
	private double user_longitude;
	private LatLng user_lat_lng;
	private int distance;
	private StringBuilder formatted_shop_address;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_info);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("View information");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		
		// Get shop data passed from Shops.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected shop data.");
		shopIntent = getIntent();
		shop_id = shopIntent.getIntExtra("KEY_ID", 0);
		shop_name = shopIntent.getStringExtra("KEY_NAME");
		services = shopIntent.getStringExtra("KEY_SERVICES");
		shop_latitude = shopIntent.getDoubleExtra("KEY_LATITUDE", 0);
        shop_longitude = shopIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		shop_url = shopIntent.getStringExtra("KEY_CONTENT_URL");
		shop_item_position = shopIntent.getIntExtra("KEY_SHOP_ITEM_POSITION", 0);
		shop_type = shopIntent.getStringExtra("KEY_TYPE");
		

		// Create instance of GPSListener
		gps = new GPSListener(this);
		
		// Check if GPS enabled
		if (gps.canGetLocation()) {
	
			// get user's current location
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
			
			// Set lat, lng coordinates to be displayed on map
			user_lat_lng = new LatLng(user_latitude, user_longitude);
			
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
					Log.i("NO_SHOP_ADDRESS", "No shop address found");
				}
			} catch (IOException e) {
				formatted_shop_address = new StringBuilder("Geocoder service not available - please try rebooting device\n");
				Log.i("GEOCODER_FAILED", "Geocoder Service not available - reboot device or use Google Geocoding API");
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
			Log.i("NO_SHOP_URL", "Shop url does not exist");
		} else {
			StringBuilder shop_address_and_url = formatted_shop_address.append("\n").append("Website:").append("\n").append(shop_url);
			shop_address_website_info.setText(shop_address_and_url);
		}
		
		
		
		// Display map fragment, with marker of shop location given by lat, lng coordinates
     	shop_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.shop_map)).getMap();
     	LatLng shop_lat_lng = new LatLng(shop_latitude, shop_longitude);
     	Marker location = shop_map.addMarker(new MarkerOptions()
     				.position(shop_lat_lng).snippet(formatted_shop_address.toString())
     				.title(shop_name + " (" + services + ")"));
     	location.showInfoWindow();
     	
     	shop_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's marker and location
     	if (user_lat_lng != null) {
     		shop_map.addMarker(new MarkerOptions()
     					.position(user_lat_lng)
     					.title("You Are Here")
     					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
     	}
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
    
    // Called when 'Add to Itinerary' button is clicked
    public void itineraryShop(View view) {
    	 	
    	UserSessionManager user_session = new UserSessionManager(this, "SHOP_PREFS");
    	
    	// Store position of shop item clicked in ListView in SharedPrefs
    	user_session.storeItemPosition(shop_item_position);
    	
    	// If item already added to itinerary display a message, otherwise store corresponding information in SharedPrefs
    	if (user_session.existInItinerary()) {
    		Toast.makeText(getApplicationContext(), "Item already added to Itinerary", Toast.LENGTH_SHORT).show();
    	} else { 		
    		user_session.storePlaceData(shop_name, shop_type, shop_latitude, shop_longitude);
    		Toast.makeText(getApplicationContext(), "Added Shop to Itinerary", Toast.LENGTH_SHORT).show();
    	}
    	
    }
}
