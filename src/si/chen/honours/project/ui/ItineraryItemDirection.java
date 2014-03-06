package si.chen.honours.project.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import si.chen.honours.project.R;
import si.chen.honours.project.facebook.places.FacebookPlaceInfo;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.utility.GoogleAPIHelper;
import si.chen.honours.project.utility.UserSessionManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ItineraryItemDirection extends Activity implements OnNavigationListener {

	ArrayList<String> transportList = new ArrayList<String>();
	ArrayAdapter<String> transportAdapter;
	
	private UserSessionManager user_session;
	
	private GoogleAPIHelper directionsHelper;
	private GoogleMap directions_map;
	
	private GPSListener gps;
	private double user_latitude;
	private double user_longitude;
	private String transport_mode;
	private String place_id;
	private String place_name;
	private String place_type;
	private double place_latitude;
	private double place_longitude;
	private String sharedPref_key;
	
	private JSONObject direction_information;
	private JSONArray warnings;
	private JSONObject routes;
	private JSONArray legs;
	private JSONObject steps;
	private JSONArray steps_directions;
	private String start_address;
	private String end_address;
	private String total_distance;
	private String total_duration;
	private String step_instruction;
	private String overview_polyline_points;
	private List<LatLng> lat_lng_list;

	private TextView textView_current_location;
	private TextView textView_destination_name;
	private TextView textView_destination_location;
	private TextView textView_total_distance;
	private TextView textView_total_duration;
	private TextView textView_direction_instructions;
	private TextView textView_warning_message;
	private StringBuilder direction_instructions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itinerary_item_direction);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		textView_current_location = (TextView) findViewById(R.id.textView_current_location);
		textView_destination_name = (TextView) findViewById(R.id.textView_destination_name);
		textView_destination_location = (TextView) findViewById(R.id.textView_destination_location);
		textView_total_distance = (TextView) findViewById(R.id.textView_total_distance);
		textView_total_duration = (TextView) findViewById(R.id.textView_total_duration);
		textView_direction_instructions = (TextView) findViewById(R.id.textView_direction_instructions);
		textView_warning_message = (TextView) findViewById(R.id.textView_warning_message);
		
		// Drop down list to select mode of transport: driving, walking; bicycling
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// Add modes of transport and bind to adapter
		transportList.add("Driving Directions");
		transportList.add("Walking Directions");
		transportList.add("Cycling Directions");
		
		// Bind ArrayList to adapter
		transportAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.spinner_list, R.id.spinner_list_item, transportList);
		// Populate spinner drop down list
		transportAdapter.setDropDownViewResource(R.layout.spinner_list);

		// Assign adapter to ActionBar
		actionBar.setListNavigationCallbacks(transportAdapter, this);
		
		
		
		// Get data from intent passed by ItineraryPlanner
		Intent intent = getIntent();
		place_id = intent.getStringExtra("KEY_ID");
		place_name = intent.getStringExtra("KEY_NAME");
		place_type = intent.getStringExtra("KEY_TYPE");
		place_latitude = Double.valueOf(intent.getStringExtra("KEY_LATITUDE"));
		place_longitude = Double.valueOf(intent.getStringExtra("KEY_LONGITUDE"));
		sharedPref_key = intent.getStringExtra("SHARED_PREF_KEY");
		
		// Set destination name
		textView_destination_name.setText("Destination: " + place_name);
		
	}
	
	// Display specific direction information based on selected mode of transport in spinner drop down list 
	public boolean onNavigationItemSelected(int position, long id) {
		
		// Set transport mode selected by user
		if (transportList.get(position).equals("Driving Directions")) {
			transport_mode = "driving";
		} else if (transportList.get(position).equals("Walking Directions")) {
			transport_mode = "walking";
		} else if (transportList.get(position).equals("Cycling Directions")) {
			transport_mode = "bicycling";
		}
		
		Log.i("TRANSPORT_MODE", transport_mode);

		
		// Reset polyline points on transport mode change
		overview_polyline_points = "";
		// Reset TextView messages on transport mode change
		textView_current_location.setText("");
		textView_destination_location.setText("");
		textView_total_distance.setText("");
		textView_total_duration.setText("");
		textView_direction_instructions.setText("");
		textView_warning_message.setText("");
		
		// Clear map on transport mode change
		directions_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.directions_map)).getMap();
		directions_map.clear();
		
		
		
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
	
				
		// Check if GPS enabled
		if (gps.canGetLocation()) {
				
			// get user's current location
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
				
			// Execute thread to get directions from Google Directions API
			new getDirections().execute();
								
		} else {
			// GPS or network not enabled, ask user to enable GPS/network in settings menu
			gps.showGPSSettingsAlert();
		}
			
		return true;
	}
	
	// Starts AsyncTask to request direction information using Google Directions API
	private class getDirections extends AsyncTask<String, String, JSONObject> {
		
		private ProgressDialog dialog;
		
		// Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(ItineraryItemDirection.this);
	        dialog.setMessage("Retrieving Direction information..");
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(false);
	        dialog.show();
		 }
		
		// Get JSON object from URL 
		@Override
		protected JSONObject doInBackground(String... args) {
			
			directionsHelper = new GoogleAPIHelper(user_latitude, user_longitude, place_latitude, place_longitude, transport_mode);

			direction_information = directionsHelper.getDirectionsResponse();
			
			return direction_information;
        }
		
		@Override
		protected void onPostExecute(JSONObject json) {
			dialog.dismiss();
			
			runOnUiThread(new Runnable() {
				public void run() {
					
				
					// Set up Google Maps to show user marker and destination place marker
					directions_map = ((MapFragment) getFragmentManager().findFragmentById(R.id.directions_map)).getMap();
					LatLng user_lat_lng = new LatLng(user_latitude, user_longitude);
					LatLng place_lat_lng = new LatLng(place_latitude, place_longitude);
					Marker user_location_marker = directions_map.addMarker(new MarkerOptions()
							.position(user_lat_lng)
							.title("You Are Here")
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
					Marker place_location_marker = directions_map.addMarker(new MarkerOptions()
							.position(place_lat_lng)
							.title(place_name + " - " + "(" + place_type + ")"));
					user_location_marker.showInfoWindow();

					
					// Create LatLng Bounds Builder to ensure all markers fit in bounding box
					LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
					// Add markers to bounding box
					boundsBuilder.include(user_lat_lng);
					boundsBuilder.include(place_lat_lng);
					// Build bounding box
					LatLngBounds bounds = boundsBuilder.build();
					
					
					// Move camera to display all markers within bounding box
					directions_map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300, 250, 5));
				
					directions_map.setMyLocationEnabled(true);
					
					try {
					
						// Check status from JSON response
						String direction_status = direction_information.getString("status");
					
						if (direction_status.equals("OK")) {
							
							// Getting JSON Array
							JSONArray routes_results = direction_information.getJSONArray("routes");
							
							// Loop over "routes" array
							for (int i = 0; i < routes_results.length(); i++) {
								
								// Get each JSONObject within "routes" array
								routes = routes_results.getJSONObject(i);
								
								// Get JSONArray "legs"
								legs = routes.getJSONArray("legs");		
								
								// Get encoded polyline points from "overview_polyline"
								overview_polyline_points = routes.getJSONObject("overview_polyline").getString("points");
								
								// Get warnings message (JSONArray "warnings")
								warnings = routes.getJSONArray("warnings");
							}
							
							// Loop over "legs" array
							for (int i = 0; i < legs.length(); i++) {
								
								// Get address, distance, duration information
								start_address = legs.getJSONObject(i).getString("start_address");
								end_address = legs.getJSONObject(i).getString("end_address");
								total_distance = legs.getJSONObject(i).getJSONObject("distance").getString("text");
								total_duration = legs.getJSONObject(i).getJSONObject("duration").getString("text");
								
								// Get JSONObject 
								steps = legs.getJSONObject(i);
								// Get JSONArray - "steps" array
								steps_directions = steps.getJSONArray("steps");
								
							}
							
							
							// Initialise StringBuilder
							direction_instructions = new StringBuilder("Step-by-step directions:\n");
							
							// Loop over "steps" array and add html_instructions to StringBuilder
							for (int i = 0; i < steps_directions.length(); i++) {
								step_instruction = steps_directions.getJSONObject(i).getString("html_instructions");
								step_instruction = Html.fromHtml(step_instruction).toString();
								direction_instructions.append(i+1 + ". " + step_instruction).append("\n").append("\n");
	
							}
			
							// Display direction information to user
							textView_current_location.setText("Current Location (Approximate):  " + start_address + "\n");
							textView_destination_location.setText("Destination Location (Approximate):  " + end_address + "\n");
							textView_total_distance.setText("Estimate Total Distance:  " + total_distance + "\n");
							textView_total_duration.setText("Estimated Total Duration:  " + total_duration + "\n");
							textView_direction_instructions.setText(direction_instructions);
							
					
							// Display warning message for walking/cycling in TextView
							if (!warnings.toString().equals("[]")) {
								if (transport_mode.equals("walking")) {
									textView_warning_message.setText("Walking directions are in beta. Use caution – This route may be missing sidewalks or pedestrian paths.");
								} else if (transport_mode.equals("bicycling")) {
									textView_warning_message.setText("Bicycling directions are in beta. Use caution – This route may contain streets that aren't suited for bicycling.");
								}
							}
							
							
							
							// Decode polyline points and get List of LatLng points
							lat_lng_list = directionsHelper.decodePolyline(overview_polyline_points);
							
							// Loop over List of LatLng points to create line segments joining origin location to destination location
							for (int i = 0; i < lat_lng_list.size() - 1; i++) {
								// Get pairs of LatLng points
								LatLng start_point = lat_lng_list.get(i);
								LatLng end_point = lat_lng_list.get(i+1);
								
								// Form line segment using pair of LatLng points
								Polyline line_segment = directions_map.addPolyline(new PolylineOptions()
										.add(start_point, end_point)
										.width(5).color(Color.RED).geodesic(true));
							}
							
							
						} else if (direction_status.equals("REQUEST_DENIED")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItineraryItemDirection.this);
							alertDialog.setTitle("Google Places Error");
							alertDialog.setMessage("An error has occurred. Request Denied.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(ItineraryItemDirection.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (direction_status.equals("ZERO_RESULTS")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItineraryItemDirection.this);
							alertDialog.setTitle("No Nearby Places");
							alertDialog.setMessage("There are no nearby places. Try changing place types or increase radius of search.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(ItineraryItemDirection.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (direction_status.equals("UNKNOWN_ERROR")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItineraryItemDirection.this);
							alertDialog.setTitle("Google Places Error");
							alertDialog.setMessage("An unknown error has occurred.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(ItineraryItemDirection.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (direction_status.equals("OVER_QUERY_LIMIT")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItineraryItemDirection.this);
							alertDialog.setTitle("Google Places Request Limit Reached");
							alertDialog.setMessage("Limit of Google Places request reached. Please try again later.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(ItineraryItemDirection.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (direction_status.equals("INVALID_REQUEST")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(ItineraryItemDirection.this);
							alertDialog.setTitle("Google Places Error");
							alertDialog.setMessage("An error has occurred. Invalid request.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(ItineraryItemDirection.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
						}
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	
    // Delete a specific itinerary item by SharedPref key
    public void deleteItineraryItem(View view) {
    	
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    	
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Deletion");
        
    	// Setting Dialog message 
    	alertDialog.setMessage("Are you sure you want to delete this itinerary item?");
    	
    	// Set "Delete" button
    	alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				Log.i("SHARED_PREFS_DELETED_ITEM", "Deleted item with SharedPref key: " + sharedPref_key);
				
				// Instantiate user_session to category of SharedPrefs based on place type
				if (place_type.equals("Attraction")) {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "ATTRACTION_PREFS");
				} else if (place_type.equals("Food")) {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "RESTAURANT_PREFS");
				} else if (place_type.equals("Drink")) {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "DRINK_PREFS");
				} else if (place_type.equals("Accommodation")) {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "ACCOMMODATION_PREFS");
				} else if (place_type.equals("Shop")) {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "SHOP_PREFS");
				} else if (place_type.equals("FACEBOOK")) {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "FACEBOOK_PLACE_PREFS");
				} else {
					user_session = new UserSessionManager(ItineraryItemDirection.this, "RECOMMENDATION_PREFS");
				}
					
				// Delete itinerary item by SharedPref key
				user_session.deleteItineraryItem(sharedPref_key);
		    	
				Toast.makeText(getApplicationContext(), "Itinerary Item Deleted", Toast.LENGTH_SHORT).show();
				
				// Go back to ItineraryPlanner class
				Intent intent = new Intent(ItineraryItemDirection.this, ItineraryPlanner.class);
				startActivity(intent);
				finish();
			}
		});
    	
    	// Set "Cancel" button 
    	alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	
    	// Show alert message 
    	alertDialog.show();

    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.itinerary_item_direction, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, ItineraryPlanner.class);
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
    	
    	Intent intent = new Intent(this, MainMenu.class);
    	startActivity(intent);
    	finish();
    }
   
}
