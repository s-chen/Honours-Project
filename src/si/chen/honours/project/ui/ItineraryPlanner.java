package si.chen.honours.project.ui;

import java.util.ArrayList;

import si.chen.honours.project.R;
import si.chen.honours.project.utility.UserSessionManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

// Itinerary Planner showing all items added to the itinerary
public class ItineraryPlanner extends Activity {

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
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Itinerary Planner");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
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
				
				// Store place name followed by place type (formatting ListView UI)
				place_names_types.add(places_data.get(i).split("##")[0] + " - " + "(" + places_data.get(i).split("##")[1] + ")");
			}
		}
		

		// Display Itinerary names in ListView
		Log.i("ItineraryListView", "Adding itinerary items to list view.");
		itinerary_adapter = new ArrayAdapter<String>(this, R.layout.point_of_interest_list, R.id.list_item, place_names_types);
		lv_itinerary.setAdapter(itinerary_adapter);
		
		
		// Display direction information for selected itinerary item by starting ItineraryItemDirection activity
		lv_itinerary.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				// Store relevant data into intent
				Intent intent = new Intent(getApplicationContext(), ItineraryItemDirection.class);
				intent.putExtra("KEY_NAME", place_names.get(position));
				intent.putExtra("KEY_TYPE", place_types.get(position));
				intent.putExtra("KEY_LATITUDE", place_latitudes.get(position));
				intent.putExtra("KEY_LONGITUDE", place_longitudes.get(position));
				
				startActivity(intent);
				finish();
			}
			
		});
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
    
    // Delete all Itinerary Items from ListView
    public void deleteAllItineraryItems(View view) {
    	
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    	
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Deletion");
        
    	// Setting Dialog message 
    	alertDialog.setMessage("Are you sure you want to delete all itinerary items listed?");
    	
    	// Set "Delete" button
    	alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				// Delete all SharedPrefs data
				Log.i("SHARED_PREFS_DELETE", "Deleted all SharedPrefs data");
				
				// Loop over SharedPref for each category of data
		    	for (String pref : USER_PREFS) {
		    		
					// Get current SharedPrefs category
					user_session = new UserSessionManager(ItineraryPlanner.this, pref);
					// Delete itinerary items for specific SharedPref
					user_session.deleteItineraryItems();
		    	}
			
				Toast.makeText(getApplicationContext(), "All Itinerary Items Deleted", Toast.LENGTH_SHORT).show();
				
				// Refresh ListView of current activity
				refresh();
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
    
    // Refresh - called when deleting itinerary items to update ListView of current activity 
    public void refresh() {
    	
    	Intent intent = getIntent();
    	overridePendingTransition(0, 0);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

   	    overridePendingTransition(0, 0);
   	    startActivity(intent);
    }
}
