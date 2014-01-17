package si.chen.honours.project.ui;

import java.util.ArrayList;

import si.chen.honours.project.R;
import si.chen.honours.project.utility.PointOfInterest;
import si.chen.honours.project.utility.UserSessionManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Itinerary Planner showing all items added to the itinerary
public class ItineraryPlanner extends ActionBarActivity {

	private ListView lv_itinerary;
	private ArrayAdapter<String> itinerary_adapter;
	
	// User preferences from SharedPrefs for each category of data
	private String[] USER_PREFS = new String[] {"ATTRACTION_PREFS", "RESTAURANT_PREFS", "DRINK_PREFS", "ACCOMMODATION_PREFS", "SHOP_PREFS"};
	private UserSessionManager user_session;

	private ArrayList<String> places_data = new ArrayList<String>();
	
	// ArrayLists for place name, place type, place latitude, place longitude
	private ArrayList<String> place_names = new ArrayList<String>();
	private ArrayList<String> place_types = new ArrayList<String>();
	private ArrayList<String> place_latitudes = new ArrayList<String>();
	private ArrayList<String> place_longitudes = new ArrayList<String>();
	
	// For formatting ListView UI (e.g. name - type)
	private ArrayList<String> place_names_types = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itinerary_planner);
		
		setTitle("Itinerary Planner");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		lv_itinerary = (ListView) findViewById(R.id.listView_itinerary);
		
		
		// Loop over SharedPref for each category of data
		for (String pref : USER_PREFS) {
			
			// Get current SharedPrefs category
			user_session = new UserSessionManager(this, pref);
			
			// ArrayList with data in the form: name##type##latitude##longitude
			places_data = user_session.getAllPlacesData();
			
			for (int i = 0; i < places_data.size(); i++) {
				
				// Store place name, type, latitude, longitude to ArrayLists
				place_names.add(places_data.get(i).split("##")[0]);
				place_types.add(places_data.get(i).split("##")[1]);
				place_latitudes.add(places_data.get(i).split("##")[2]);
				place_longitudes.add(places_data.get(i).split("##")[3]);
				
				// Store place name followed by place type
				place_names_types.add(places_data.get(i).split("##")[0] + " - " + "(" + places_data.get(i).split("##")[1] + ")");
			}
		}
		
		
/*		for (int i = 0; i < place_names.size(); i++) {
			
			System.out.println(place_names.get(i));
		}*/
		

		

		// Display Itinerary names in ListView
		Log.i("ItineraryListView", "Adding itinerary items to list view.");
		itinerary_adapter = new ArrayAdapter<String>(this, R.layout.point_of_interest_list, R.id.list_item, place_names_types);
		lv_itinerary.setAdapter(itinerary_adapter);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.itinerary_planner, menu);
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
