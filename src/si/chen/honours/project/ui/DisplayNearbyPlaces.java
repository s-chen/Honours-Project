package si.chen.honours.project.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.utility.NearbyPlaces;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;



// Display a list of nearby point of interests to the user, ranked by the distance to the user
public class DisplayNearbyPlaces extends ActionBarActivity {

	private GPSListener gps;
	private double user_latitude;
	private double user_longitude;
	private double radius = 1000; // 1000m
	private String types = "airport|amusement_park|aquarium|art_gallery|atm|bakery|bar|cafe|clothing_store|convenience_store"
			+ "|establishment|food|grocery_or_supermarket|movie_theater|museum|night_club|park|restaurant|shopping_mall|zoo";

	
	private NearbyPlaces nearbyPlaceHelper;
	private JSONObject nearby_places;
	private String KEY_STATUS = "status";
	private String KEY_RESULTS = "results";
	private String KEY_GEOMETRY = "geometry";
	private String KEY_LOCATION = "location";
	private String KEY_LAT = "lat";
	private String KEY_LNG = "lng";
	private String KEY_NAME = "name";
	private String KEY_REFERENCE = "reference";
	private String KEY_VICINITY = "vicinity";
	
	private String place_name;
	private String place_lat;
	private String place_lng;
	private String place_reference;
	
	ArrayList<String> place_name_list = new ArrayList<String>();
	ArrayList<String> place_latitude_list = new ArrayList<String>();
	ArrayList<String> place_longitude_list = new ArrayList<String>();
	ArrayList<String> place_reference_list = new ArrayList<String>();
	
	private ListView lv_nearby_places;
	private Button button_show_nearby_places;
	private ArrayAdapter<String> nearby_places_adapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_nearby_places);
		
		setTitle("Nearby Places");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		lv_nearby_places = (ListView) findViewById(R.id.listView_nearby_places);
		button_show_nearby_places = (Button) findViewById(R.id.button_show_nearby_place_map);
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
			
		// Check if GPS enabled
		if (gps.canGetLocation()) {
			
			// get user's current location
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
			
			// Execute thread to search for nearby places using Google Places
			new getNearbyPlaces().execute();
							
		} else {
			// GPS or network not enabled, ask user to enable GPS/network in settings menu
			gps.showGPSSettingsAlert();
		}
			
	}
	
	// Starts AsyncTask to request Nearby Place search using Google Places API
	private class getNearbyPlaces extends AsyncTask<String, String, JSONObject> {
		
		private ProgressDialog dialog;
		
		// Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(DisplayNearbyPlaces.this);
	        dialog.setMessage("Retrieving Nearby Places..");
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(false);
	        dialog.show();
		 }
		
		// Get JSON object from URL 
		@Override
		protected JSONObject doInBackground(String... args) {
			
			nearbyPlaceHelper = new NearbyPlaces(user_latitude, user_longitude, radius, types);
			
			nearby_places = nearbyPlaceHelper.getNearbyPlacesResponse();
			
			return nearby_places;
        }
		
		
		@Override
		protected void onPostExecute(JSONObject json) {
			dialog.dismiss();
			
			runOnUiThread(new Runnable() {
				public void run() {
				
					try {
					
						// Check status from JSON response
						String places_status = nearby_places.getString(KEY_STATUS);
					
						if (places_status.equals("OK")) {
							
							// Getting JSON Array
							JSONArray places_results = nearby_places.getJSONArray(KEY_RESULTS);
						
							for (int i = 0; i < places_results.length(); i++) {
		                    	
								// Each place within "results" array
								JSONObject place = places_results.getJSONObject(i);
		                  	
								place_name = place.getString(KEY_NAME);
								place_lat = place.getJSONObject(KEY_GEOMETRY).getJSONObject(KEY_LOCATION).getString(KEY_LAT);
								place_lng = place.getJSONObject(KEY_GEOMETRY).getJSONObject(KEY_LOCATION).getString(KEY_LNG);
								place_reference = place.getString(KEY_REFERENCE);
						
								Log.i("NEARBY_PLACES_NAME", place_name);
								Log.i("NEARBY_PLACES_LAT", place_lat);
								Log.i("NEARBY_PLACES_LNG", place_lng);
						
								// Add place name, latitude, longitude, reference to ArrayLists
								place_name_list.add(place_name);
								place_latitude_list.add(place_lat);
								place_longitude_list.add(place_lng);
								place_reference_list.add(place_reference);
						
							}
						
							// Display nearby places in ListView
							Log.i("NearbyPlacesListView", "Adding nearby places to list view.");
							nearby_places_adapter = new ArrayAdapter<String>(DisplayNearbyPlaces.this, R.layout.point_of_interest_list, R.id.list_item, place_name_list);
							lv_nearby_places.setAdapter(nearby_places_adapter);
							
							
							// Called when button 'Show Nearby Places on map' is clicked
							button_show_nearby_places.setOnClickListener(new View.OnClickListener() {

								public void onClick(View arg0) {
									Intent intent = new Intent(getApplicationContext(), DisplayNearbyPlacesMap.class);
									intent.putStringArrayListExtra("KEY_PLACE_NAMES", place_name_list);
									intent.putStringArrayListExtra("KEY_PLACE_LATITUDES", place_latitude_list);
									intent.putStringArrayListExtra("KEY_PLACE_LONGITUDES", place_longitude_list);
									
									startActivity(intent);
									finish();
								}
							});
							
							
							// Display more specific information about a particular nearby place
							lv_nearby_places.setOnItemClickListener(new OnItemClickListener() {
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
													
									Intent nearbyPlaceIntent = new Intent(getApplicationContext(), DisplayNearbyPlaceInfo.class);
									nearbyPlaceIntent.putExtra("KEY_NAME", place_name_list.get(position));
									nearbyPlaceIntent.putExtra("KEY_REFERENCE", place_reference_list.get(position));
									
									startActivity(nearbyPlaceIntent);
									finish();
								}
										
							});
									
						
						} else if (places_status.equals("REQUEST_DENIED")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayNearbyPlaces.this);
							alertDialog.setTitle("Google Places Error");
							alertDialog.setMessage("An error has occurred. Request Denied.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(DisplayNearbyPlaces.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (places_status.equals("ZERO_RESULTS")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayNearbyPlaces.this);
							alertDialog.setTitle("No Nearby Places");
							alertDialog.setMessage("There are no nearby places. Try changing place types or increase radius of search.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(DisplayNearbyPlaces.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (places_status.equals("UNKNOWN_ERROR")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayNearbyPlaces.this);
							alertDialog.setTitle("Google Places Error");
							alertDialog.setMessage("An unknown error has occurred.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(DisplayNearbyPlaces.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (places_status.equals("OVER_QUERY_LIMIT")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayNearbyPlaces.this);
							alertDialog.setTitle("Google Places Request Limit Reached");
							alertDialog.setMessage("Limit of Google Places request reached. Please try again later.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(DisplayNearbyPlaces.this, MainMenu.class);
									startActivity(intent);
									finish();
								}
							});
							alertDialog.show();
							
						} else if (places_status.equals("INVALID_REQUEST")) {
							// Create and show Alert Dialog
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayNearbyPlaces.this);
							alertDialog.setTitle("Google Places Error");
							alertDialog.setMessage("An error has occurred. Invalid request.");
							alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(DisplayNearbyPlaces.this, MainMenu.class);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_nearby_places, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
			return true;
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
