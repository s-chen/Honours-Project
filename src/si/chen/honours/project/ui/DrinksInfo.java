package si.chen.honours.project.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import si.chen.honours.project.R;
import si.chen.honours.project.facebook.login.LoggedInFragment;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.utility.UserSessionManager;
import si.chen.honours.project.utility.aws.AWSHelper;
import si.chen.honours.project.utility.aws.User;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Item;
import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/** Details of the particular drinks service - called when the service name is selected in the ListView **/
public class DrinksInfo extends Activity implements OnRatingBarChangeListener {

	private GoogleMap drinks_map;
	private Intent drinksIntent;
	private String drinks_id;
	private String drinks_name;
	private String services;
	private double drinks_latitude;
	private double drinks_longitude;
	private String drinks_url;
	private int drinks_item_position;
	private String drinks_type;

	private GPSListener gps;
	private Location user_location;
	private Location drinks_location;
	private double user_latitude;
	private double user_longitude;
	private LatLng user_lat_lng;
	private int distance;
	private StringBuilder formatted_drinks_address;
	
	private RatingBar drinks_rating;
	private String FB_USER_ID;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drinks_info);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("View information");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		
		// Get drinks data passed from Drinks.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected drinks data.");
		drinksIntent = getIntent();
		drinks_id = drinksIntent.getStringExtra("KEY_ID");
		drinks_name = drinksIntent.getStringExtra("KEY_NAME");
		services = drinksIntent.getStringExtra("KEY_SERVICES");
		drinks_latitude = drinksIntent.getDoubleExtra("KEY_LATITUDE", 0);
        drinks_longitude = drinksIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		drinks_url = drinksIntent.getStringExtra("KEY_CONTENT_URL");
		drinks_item_position = drinksIntent.getIntExtra("KEY_DRINKS_ITEM_POSITION", 0);
		drinks_type = drinksIntent.getStringExtra("KEY_TYPE");
		
		
		// Set Drinks name as title
		TextView textView_drinks_name = (TextView) findViewById(R.id.drinks_name);
		textView_drinks_name.setText(drinks_name);
		

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
		
		
		// Set lat, lng coordinates for drinks location 
		drinks_location = new Location("drinks_location");
		drinks_location.setLatitude(drinks_latitude);
		drinks_location.setLongitude(drinks_longitude);
				
		// Set lat, lng coordinates for user location 
		user_location = new Location("user_location");
		user_location.setLatitude(user_latitude);
		user_location.setLongitude(user_longitude);
				
		// Calculate distance from user's current location to bar/pub/cafe/club
		distance = (int) user_location.distanceTo(drinks_location);
		
		// Display distance information in TextView
		TextView distance_information = (TextView) findViewById(R.id.textView_distance_info_drinks);
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
			distance_information.setText("Distance to destination: " + distance + "m\n");
		} else {
				distance_information.setText("Distance information not available\n");
		}
		
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
			// Uses reverse Geocoding to obtain address of drinks service from the drinks lat, lng coordinates
			Geocoder geocoder = new Geocoder(getBaseContext(), Locale.ENGLISH);
			try {
        	
				List<Address> addresses = geocoder.getFromLocation(drinks_latitude, drinks_longitude, 1);
            
				if (addresses.size() > 0) {
            	
					Address drinks_address = addresses.get(0);
					formatted_drinks_address = new StringBuilder("Address:\n");
                
					// Adds each address line to the string
					for (int i = 0; i < drinks_address.getMaxAddressLineIndex(); i++) {
						formatted_drinks_address.append(drinks_address.getAddressLine(i)).append("\n");
					}
                          
				} else {
					Log.i("NO_DRINKS_ADDRESS", "No drinks service address found");
				}
			} catch (IOException e) {
				formatted_drinks_address = new StringBuilder("Geocoder service not available - please try rebooting device\n");
				Log.i("GEOCODER_FAILED", "Geocoder Service not available - reboot device or use Google Geocoding API");
				e.printStackTrace();
			}
		} else {
			formatted_drinks_address = new StringBuilder("Address information not available\n");
		}
		
		TextView drinks_address_website_info = (TextView) findViewById(R.id.textView_drinks_address_website_info);
        // Display drinks service address in TextView
    	drinks_address_website_info.setText(formatted_drinks_address);
        
		// Also display drinks service website url in TextView, if it exists.
		if (drinks_url.equals("")) {
			// Do nothing
			Log.i("NO_DRINKS_URL", "Drinks service url does not exist");
		} else {
			StringBuilder drinks_address_and_url = formatted_drinks_address.append("\n").append("Website:").append("\n").append(drinks_url);
			drinks_address_website_info.setText(drinks_address_and_url);
		}
		
		
		
		// Display map fragment, with marker of drinks service location given by lat, lng coordinates
     	drinks_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.drinks_map)).getMap();
     	LatLng drinks_lat_lng = new LatLng(drinks_latitude, drinks_longitude);
     	Marker location = drinks_map.addMarker(new MarkerOptions()
     				.position(drinks_lat_lng).snippet(formatted_drinks_address.toString())
     				.title(drinks_name + " (" + services + ")"));
     	location.showInfoWindow();
     	
     	drinks_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's marker and location
     	if (user_lat_lng != null) {
     		drinks_map.addMarker(new MarkerOptions()
     					.position(user_lat_lng)
     					.title("You Are Here")
     					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
     	}
     	drinks_map.setMyLocationEnabled(true);
     	
     	
     	
     	
     	// RatingBar listener when ratings are changed by the user
     	drinks_rating = (RatingBar) findViewById(R.id.rating_bar);
     	drinks_rating.setOnRatingBarChangeListener(this);
     	
     	/**
     	 * Check user login status
     	 * If user logged in to Facebook, attempt to retrieve and restore existing user place ratings
     	 */
     	Session session = Session.getActiveSession();
     	if (session != null && session.isOpened()) {
  
     		// Get User Facebook ID
     		FB_USER_ID = LoggedInFragment.USER_ID;
     		
         	// Start thread to try and retrieve existing user place ratings
         	new RetrieveRatingsAWS().execute();
     	}
	}
	
	// Submits drinks rating to AWS SimpleDB
 	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
 		
     	/**
     	 * Check user login status
     	 * If user logged in to Facebook, they can now add more place ratings
     	 */
     	Session session = Session.getActiveSession();
     	if (session != null && session.isOpened()) {
       		     		
     		// Add additional drinks rating to global HashMap
     		User.user_place_ratings.put(drinks_id, String.valueOf(drinks_rating.getRating()));
     		
     		// Start thread to submit rating to SimpleDB
         	new SubmitRatingsAWS().execute();
         	Toast.makeText(getApplicationContext(), "Submitted rating", Toast.LENGTH_SHORT).show();
     		
     	} else {
     		// User not logged in to Facebook/or logged out
     		Toast.makeText(getApplicationContext(), "Please login to submit rating", Toast.LENGTH_SHORT).show();
     		// Clear global HashMap user place ratings when user not logged in
     		User.user_place_ratings.clear();
     	}
 		
 	}
 	
 	// Retrieve existing place ratings for user from Amazon SimpleDB
 	public class RetrieveRatingsAWS extends AsyncTask<Void, Void, Boolean> {
 		
		// Restore user's ratings
		HashMap<String,String> restored_user_place_ratings = new HashMap<String,String>();
		
 		@Override
		protected Boolean doInBackground(Void... params) {
 			
			AWSHelper aws = new AWSHelper();
			User user = new User(FB_USER_ID);
			List<Item> place_ratings = aws.getPlaceRatings(user);
			
			
			// Try retrieving place ratings if they exist for a user
			if (!place_ratings.isEmpty()) {
				
				// String format of HashMap of user place ratings
				String ratings = aws.getPlaceRatingsForItem(place_ratings.get(0));
				
				// Conversion to HashMap when we have existing ratings
				if (!ratings.equals("")) {
					// Convert String format of HashMap (from SimpleDB) to a HashMap
					restored_user_place_ratings = User.convertToHashMap(ratings);
					// Set global place ratings HashMap to the previously saved ratings from SimpleDB
					User.user_place_ratings = restored_user_place_ratings;
				}
				
				Log.i("GLOBAL_HASHMAP_USER_RATINGS", "Facebook UserID - " + FB_USER_ID + ": " + User.user_place_ratings.toString());
				return true;
			} else {
				return false;
			}
				
		}
 		
 		@Override
 		protected void onPostExecute(final Boolean flag) {
 			if (flag) {
 				Log.i("RETRIEVING_RATINGS", "Retrieving place ratings from SimpleDB..");
 			} else {
 				Log.i("NO_RATINGS", "No place ratings for user");
 			}
 		}
 	}
 	
 	// Submit drink ratings to SimpleDB
 	public class SubmitRatingsAWS extends AsyncTask<Void, Void, Void> {
 		
 		@Override
		protected Void doInBackground(Void... params) {
 			
			AWSHelper aws = new AWSHelper();
			User user = new User(FB_USER_ID, User.user_place_ratings);
			aws.storePlaceRatings(user);
			Log.i("RATINGS_STORED_IN_SIMPLE_DB", "Ratings now stored");
			return null;
		}
 	
 	}
	
	// Called when 'Search Web' button is clicked
	public void searchDrinksOnline(View view) {
		
		String query = drinks_name + " " + services + ", Edinburgh"  ;
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query); // query contains search string
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drinks_info, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, Drinks.class);
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
    	
    	Intent intent = new Intent(this, Drinks.class);
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
    public void itineraryDrinks(View view) {
    	 	
    	UserSessionManager user_session = new UserSessionManager(this, "DRINK_PREFS");
    	
    	// Store position of drink item clicked in ListView in SharedPrefs
    	user_session.storeItemPosition(drinks_item_position);
    	
    	// If item already added to itinerary display a message, otherwise store corresponding information in SharedPrefs
    	if (user_session.existInItinerary()) {
    		Toast.makeText(getApplicationContext(), "Item already added to Itinerary", Toast.LENGTH_SHORT).show();
    	} else { 		
    		user_session.storePlaceData(drinks_id, drinks_name, drinks_type, drinks_latitude, drinks_longitude);
    		Toast.makeText(getApplicationContext(), "Added Drink location to Itinerary", Toast.LENGTH_SHORT).show();
    	}
    	
    }
}
