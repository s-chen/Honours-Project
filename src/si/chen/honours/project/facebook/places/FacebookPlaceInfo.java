package si.chen.honours.project.facebook.places;

import org.json.JSONException;
import org.json.JSONObject;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.ui.MainMenu;
import si.chen.honours.project.utility.UserSessionManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FacebookPlaceInfo extends Activity {
	
	private GoogleMap facebook_place_map;
	
	private GPSListener gps;
	private double user_latitude;
	private double user_longitude;
	private LatLng user_lat_lng;
	private Location user_location;
	private Location facebook_place_location;
	private int distance;
	
	public static String FACEBOOK_PLACE_ID;
	private String FACEBOOK_PLACE_URL = "https://graph.facebook.com/";
	
	private String selected_place_info;
	private JSONObject facebook_place_information;
	
	
	// Constants for describing place
	private double facebook_place_latitude;
	private double facebook_place_longitude;
	private String facebook_place_name;
	private String facebook_place_category;

	
	TextView textView_facebook_place_name_category;
	TextView textView_facebook_place_address_phone_number;
	TextView textView_facebook_place_general_info;
	TextView textView_facebook_place_distance_information;
	
	private StringBuilder facebook_place_address_phone_number = new StringBuilder();
	private StringBuilder facebook_place_general_information = new StringBuilder();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_place_info);
		
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Facebook Place Information");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Set up TextView to display information
		textView_facebook_place_name_category = (TextView) findViewById(R.id.facebook_place_name_category);
		textView_facebook_place_address_phone_number = (TextView) findViewById(R.id.textView_facebook_place_address_phone_number);
		textView_facebook_place_general_info = (TextView) findViewById(R.id.textView_facebook_place_general_info);
		textView_facebook_place_distance_information = (TextView) findViewById(R.id.textView_distance_info_facebook_place);
		
		
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
				
		// Set lat, lng coordinates for user location 
		user_location = new Location("user_location");
		user_location.setLatitude(user_latitude);
		user_location.setLongitude(user_longitude);
		
		
		// Get place graph from selected place
		Intent intent = getIntent();
		selected_place_info = intent.getStringExtra("KEY_FACEBOOK_PLACE_GRAPH");
		
		
		// Try and get place ID from JSONObject
		try {
			JSONObject place_id = new JSONObject(selected_place_info);
			FACEBOOK_PLACE_ID = place_id.getString("id");
			Log.i("FACEBOOK_PLACE_ID", FACEBOOK_PLACE_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Get place details by making a request if we can get Place ID
		if (!FACEBOOK_PLACE_ID.equals("")) {
			FACEBOOK_PLACE_URL = FACEBOOK_PLACE_URL + FACEBOOK_PLACE_ID;
			
		    // Check for an open session
		    Session session = Session.getActiveSession();
		    if (session != null && session.isOpened()) {
		        // Get the place data
		    	makePlaceRequest(session, FACEBOOK_PLACE_URL);
		    } 
		}
		
	}
	
    // Make an API call to get more information about a place (using place_id)
	private void makePlaceRequest(final Session session, String place_id) {    	
	    /**
	     * Request parameters:
	     * session - current user session
	     * place_id - place ID for query Graph API
	     * Bundle parameter - null
	     * Http request using GET
	     */
	    Request request = new Request(session, place_id, null, HttpMethod.GET, new Request.Callback() {
	        public void onCompleted(Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	            	
	            	// Get place information as JSONObject
	            	facebook_place_information = response.getGraphObject().getInnerJSONObject();
	            	Log.i("FB_PLACE_GRAPH", facebook_place_information.toString());	            	
	            	
	            	try {
	            		
	            		// Place name
	            		facebook_place_name = facebook_place_information.getString("name");
	            		// Place category
	            		facebook_place_category = facebook_place_information.getString("category");
	            		// Place latitude
	            		facebook_place_latitude = facebook_place_information.getJSONObject("location").getDouble("latitude");
	            		// Place longitude
	            		facebook_place_longitude = facebook_place_information.getJSONObject("location").getDouble("longitude");
	            		
	            		// Place address
	            		facebook_place_address_phone_number.append("Address:\n")
	            			.append(facebook_place_information.getJSONObject("location").getString("street"))
	            			.append("\n")
	            			.append(facebook_place_information.getJSONObject("location").getString("city"))
	            			.append("\n")
	            			.append(facebook_place_information.getJSONObject("location").getString("country"))
	            			.append("\n")
	            			.append(facebook_place_information.getJSONObject("location").getString("zip"))
	            			.append("\n");
	            		// Also get Place phone number if it exists
	            		if (!facebook_place_information.isNull("phone")) {
	            			facebook_place_address_phone_number.append("\n").append("Phone:\n")
	            				.append(facebook_place_information.getString("phone")).append("\n");
	            		}
	         
	            		
	            		
	            		// Get relevant information about a place if information exists
	            		if (!facebook_place_information.isNull("about")) {
	            			facebook_place_general_information.append("About: " + facebook_place_information.getString("about")).append("\n\n");
	            		} 
	            		if (!facebook_place_information.isNull("mission")) {
	            			facebook_place_general_information.append("Mission: " + facebook_place_information.getString("mission")).append("\n\n");
	            		}
	            		if (!facebook_place_information.isNull("description")) {
	            			facebook_place_general_information.append("Description: " + facebook_place_information.getString("description")).append("\n\n");
	            		}
	            		if (!facebook_place_information.isNull("company_overview")) {
	            			facebook_place_general_information.append("Overview: " + facebook_place_information.getString("company_overview")).append("\n\n");
	            		}
	            		if (!facebook_place_information.isNull("general_info")) {
	            			facebook_place_general_information.append("General Information: " + facebook_place_information.getString("general_info")).append("\n\n");
	            		}
	            		if (!facebook_place_information.isNull("products")) {
	            			facebook_place_general_information.append("Extra Information: " + facebook_place_information.getString("products")).append("\n\n");
	            		}
	            		if (!facebook_place_information.isNull("website")) {
	            			facebook_place_general_information.append("Website: " + facebook_place_information.getString("website")).append("\n\n");
	            		}
	            		if (!facebook_place_information.isNull("link")) {
	            			facebook_place_general_information.append("Facebook link: " + facebook_place_information.getString("link")).append("\n\n");
	            		}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	
	            	
	        		// Set lat, lng coordinates for Facebook place location 
	        		facebook_place_location = new Location("fabook_place_location");
	        		facebook_place_location.setLatitude(facebook_place_latitude);
	        		facebook_place_location.setLongitude(facebook_place_longitude);
	        		
	        		// Calculate distance from user's current location to Facebook place location
	        		distance = (int) user_location.distanceTo(facebook_place_location);
	        		if (gps.isConnectionAvailable() && gps.canGetLocation()) {
	        			textView_facebook_place_distance_information.setText("Distance to destination: " + distance + "m\n");
	        		} else {
	        			textView_facebook_place_distance_information.setText("Distance information not available\n");
	        		}
	            	
	        		
            		// Set place name, category, address, phone number, general info
					textView_facebook_place_name_category.setText(facebook_place_name + " - " + "(" + facebook_place_category + ")");
					textView_facebook_place_address_phone_number.setText(facebook_place_address_phone_number);
					textView_facebook_place_general_info.setText(facebook_place_general_information);
					
					
					
			
					// Display map fragment, with marker of Facebook place location given by lat, lng coordinates
			     	facebook_place_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.facebook_place_map)).getMap();
			     	LatLng facebook_place_lat_lng = new LatLng(facebook_place_latitude, facebook_place_longitude);
			     	Marker location = facebook_place_map.addMarker(new MarkerOptions()
			     				.position(facebook_place_lat_lng)
			     				.title(facebook_place_name + " (" + facebook_place_category + ")"));
			     	location.showInfoWindow();
			     	
			     	facebook_place_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 15));
			     				

			     	// Display current user's marker and location
			     	if (user_lat_lng != null) {
			     		facebook_place_map.addMarker(new MarkerOptions()
			     					.position(user_lat_lng)
			     					.title("You Are Here")
			     					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			     	}
			     	facebook_place_map.setMyLocationEnabled(true);
			     	  	
	            } 
	            if (response.getError() != null) {
	                Log.d("FB_RESPONSE_ERROR", response.getError().toString());
	            }
	        }
	    });
	    request.executeAsync();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facebook_place_info, menu);
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gps.stopUsingGPS();
	}
	
    @Override
    public void onPause() {
    	super.onPause();
    	gps.stopUsingGPS();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
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
    
	// Called when 'Search Web' button is clicked
	public void searchFacebookPlaceOnline(View view) {
		
		String query = facebook_place_name + " " + facebook_place_category + ", Edinburgh"  ;
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query); // query contains search string
		startActivity(intent);
	}
	
    // Called when 'Add to Itinerary' button is clicked
    public void itineraryFacebookPlace(View view) {
    	 	
    	UserSessionManager user_session = new UserSessionManager(this, "FACEBOOK_PLACE_PREFS");
    	
    	// Store Facebook ID as identifier for SharedPrefs
    	user_session.storeItemPosition(Long.parseLong(FACEBOOK_PLACE_ID));
    	
    	// If item already added to itinerary display a message, otherwise store corresponding information in SharedPrefs
    	if (user_session.existInItinerary()) {
    		Toast.makeText(getApplicationContext(), "Item already added to Itinerary", Toast.LENGTH_SHORT).show();
    	} else { 		
    		user_session.storePlaceData(FACEBOOK_PLACE_ID, facebook_place_name.replaceAll(",", " "), facebook_place_category.replaceAll(",", " ") + "::" + "FACEBOOK", facebook_place_latitude, facebook_place_longitude);
    		Toast.makeText(getApplicationContext(), "Added Facebook Place to Itinerary", Toast.LENGTH_SHORT).show();
    	}
    	
    }
  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, FacebookPlacePicker.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            finish();
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
    	
    	Intent intent = new Intent(this, FacebookPlacePicker.class);
    	startActivity(intent);
    	finish();
    }

}
