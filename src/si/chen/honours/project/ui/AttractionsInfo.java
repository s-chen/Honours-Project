package si.chen.honours.project.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.login.LoggedInFragment;
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


/** Details of the particular attraction - called when the name is selected in the ListView **/
public class AttractionsInfo extends Activity implements OnRatingBarChangeListener {

	private GoogleMap attractions_map;
	private Intent attractionsIntent;
	private String attractions_id;
	private String attractions_name;
	private String services;
	private double attractions_latitude;
	private double attractions_longitude;
	private String attractions_url;
	private int attraction_item_position;
	private String attractions_type;

	private GPSListener gps;
	private Location user_location;
	private Location attractions_location;
	private double user_latitude;
	private double user_longitude;
	private LatLng user_lat_lng;
	private int distance;
	private StringBuilder formatted_attractions_address;
	
	private RatingBar attraction_rating;
	private String FB_USER_ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attractions_info);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("View information");
		actionBar.setDisplayHomeAsUpEnabled(true);

		
		
		// Get attractions data passed from Attractions.java class
		Log.i("RETRIEVE_DATA", "Retrieve selected attractions data.");
		attractionsIntent = getIntent();
		attractions_id = attractionsIntent.getStringExtra("KEY_ID");
		attractions_name = attractionsIntent.getStringExtra("KEY_NAME");
		services = attractionsIntent.getStringExtra("KEY_SERVICES");
		attractions_latitude = attractionsIntent.getDoubleExtra("KEY_LATITUDE", 0);
        attractions_longitude = attractionsIntent.getDoubleExtra("KEY_LONGITUDE", 0);
		attractions_url = attractionsIntent.getStringExtra("KEY_CONTENT_URL");
		attraction_item_position = attractionsIntent.getIntExtra("KEY_ATTRACTION_ITEM_POSITION", 0);
		attractions_type = attractionsIntent.getStringExtra("KEY_TYPE");
		

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
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
			distance_information.setText("Distance to destination: " + distance + "m\n");
		} else {
				distance_information.setText("Distance information not available\n");
		}
	
			
		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
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
					Log.i("NO_ATTRACTIONS_ADDRESS", "No attractions address found");
				}
			} catch (IOException e) {
				formatted_attractions_address = new StringBuilder("Geocoder service not available - please try rebooting device\n");
				Log.i("GEOCODER_FAILED", "Geocoder Service not available - reboot device or use Google Geocoding API");
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
			Log.i("NO_ATTRACTIONS_URL", "Attractions url does not exist");
		} else {
			StringBuilder attractions_address_and_url = formatted_attractions_address.append("\n").append("Website:").append("\n").append(attractions_url);
			attractions_address_website_info.setText(attractions_address_and_url);
		}
		
		
		
		// Display map fragment, with marker of attractions location given by lat, lng coordinates
     	attractions_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.attractions_map)).getMap();
     	LatLng attractions_lat_lng = new LatLng(attractions_latitude, attractions_longitude);
     	Marker location = attractions_map.addMarker(new MarkerOptions()
     				.position(attractions_lat_lng).snippet(formatted_attractions_address.toString())
     				.title(attractions_name + " (" + services + ")"));
     	location.showInfoWindow();
     	
     	attractions_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
     				
     	// Display current user's marker and location
     	if (user_lat_lng != null) {
     		attractions_map.addMarker(new MarkerOptions()
     					.position(user_lat_lng)
     					.title("You Are Here")
     					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
     	}
     	attractions_map.setMyLocationEnabled(true);
     	
     	
     	
     	
     	// RatingBar listener when ratings are changed by the user
     	attraction_rating = (RatingBar) findViewById(R.id.rating_bar);
     	attraction_rating.setOnRatingBarChangeListener(this);
     	
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
	
	// Submits attraction rating to AWS SimpleDB
 	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
 		
     	/**
     	 * Check user login status
     	 * If user logged in to Facebook, they can now add more place ratings
     	 */
     	Session session = Session.getActiveSession();
     	if (session != null && session.isOpened()) {
       		     		
     		// Add additional attraction rating to global HashMap
     		User.user_place_ratings.put(attractions_id, String.valueOf(attraction_rating.getRating()));
     		
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
 	
 	// Submit accommodation ratings to SimpleDB
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
    public void itineraryAttraction(View view) {
    	
    	UserSessionManager user_session = new UserSessionManager(this, "ATTRACTION_PREFS");
    	
    	// Store position of attraction item clicked in ListView in SharedPrefs
    	user_session.storeItemPosition(attraction_item_position);
    	
    	// If item already added to itinerary display a message, otherwise store corresponding information in SharedPrefs
    	if (user_session.existInItinerary()) {
    		Toast.makeText(getApplicationContext(), "Item already added to Itinerary", Toast.LENGTH_SHORT).show();
    	} else { 		
    		user_session.storePlaceData(attractions_id, attractions_name, attractions_type, attractions_latitude, attractions_longitude);
    		Toast.makeText(getApplicationContext(), "Added Attraction to Itinerary", Toast.LENGTH_SHORT).show();
    	}
    	
    }
}
