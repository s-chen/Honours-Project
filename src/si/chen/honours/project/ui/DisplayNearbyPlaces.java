package si.chen.honours.project.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import si.chen.honours.project.R;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.utility.NearbyPlaces;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.util.json.JSONException;

// Display a list of nearby point of interests to the user, ranked by the distance to the user
public class DisplayNearbyPlaces extends ActionBarActivity {

	private NearbyPlaces places;
	private GPSListener gps;
	private double user_latitude;
	private double user_longitude;
	private double radius = 1000; // 1000m
	private String types = "airport|amusement_park|aquarium|art_gallery|atm|bakery|bar|cafe|clothing_store|convenience_store"
			+ "|establishment|food|grocery_or_supermarket|movie_theater|museum|night_club|park|restaurant|shopping_mall|zoo";
	
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
	ArrayList<HashMap<String, String>> place_list = new ArrayList<HashMap<String, String>>();
	
	private ListView lv_nearby_places;
	private ArrayAdapter<HashMap<String, String>> nearby_places_adapter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_nearby_places);
		
		setTitle("Nearby Places");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		lv_nearby_places = (ListView) findViewById(R.id.listView_nearby_places);
		
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
	        dialog.setMessage("Getting Nearby Places..");
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(true);
	        dialog.show();
		 }
		
		// Get JSON object from URL 
		@Override
		protected JSONObject doInBackground(String... args) {
			
			places = new NearbyPlaces(user_latitude, user_longitude, radius, types);
			
			nearby_places = places.getNearbyPlaceResponse();
			
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
		                  	
							String place_name = place.getString(KEY_NAME);
							String place_lat = place.getJSONObject(KEY_GEOMETRY).getJSONObject(KEY_LOCATION).getString(KEY_LAT);
							String place_lng = place.getJSONObject(KEY_GEOMETRY).getJSONObject(KEY_LOCATION).getString(KEY_LNG);
							String place_reference = place.getString(KEY_REFERENCE);
						
							Log.d("NEARBY_PLACES_NAME", place_name);
							Log.d("NEARBY_PLACES_LAT", place_lat);
							Log.d("NEARBY_PLACES_LNG", place_lng);
						
							// Add name, lat, lng, reference to map
							HashMap<String, String> map = new HashMap<String, String>();
							map.put(KEY_NAME, place_name);
							map.put(KEY_LAT, place_lat);
							map.put(KEY_LNG, place_lng);
							map.put(KEY_REFERENCE, place_reference);
							
							// Add map to ArrayList
							place_list.add(map);
						
						}
	/*					
						
						for (int i = 0; i < place_list.size(); i++) {
							System.out.println("ARRAYLIST TEST: " + place_list.get(i).get(KEY_NAME));
						}*/
						
						// Display nearby places in ListView
						Log.i("NearbyPlacesListView", "Adding nearby places to list view.");
						nearby_places_adapter = new ArrayAdapter<HashMap<String, String>>(DisplayNearbyPlaces.this, R.layout.point_of_interest_list, R.id.list_item, place_list);
						lv_nearby_places.setAdapter(nearby_places_adapter);
						
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
