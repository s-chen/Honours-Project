package si.chen.honours.project.ui;

import java.util.Collections;
import java.util.List;

import si.chen.honours.project.R;
import si.chen.honours.project.utility.DatabaseHelper;
import si.chen.honours.project.utility.PointOfInterest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

// Display list of Accommodation
public class Accommodation extends Activity {

	private List<PointOfInterest> accommodation_list;
	private String[] accommodation_id;
	private String[] accommodation_name;
	private String[] services;
	private double[] accommodation_latitude;
	private double[] accommodation_longitude;
	private String[] accommodation_url;
	private ArrayAdapter<PointOfInterest> accommodation_adapter;
	private ListView lv_accommodation;
	private PointOfInterest accommodation_data[];
	
	private EditText search_accommodation;
	private boolean is_filtered = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accommodation);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Accommodation");
		actionBar.setDisplayHomeAsUpEnabled(true);

		
		lv_accommodation = (ListView) findViewById(R.id.listView_accommodation);
		search_accommodation = (EditText) findViewById(R.id.editText_accommodation_search);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		accommodation_list = dbHelper.getAccommodationData();
		
		// Uses Comparator from PointOfInterest class to sort accommodation
		Collections.sort(accommodation_list);

		
		// Initialise arrays for storing accommodation data
		accommodation_id = new String[accommodation_list.size()];
		accommodation_name = new String[accommodation_list.size()];
		services = new String[accommodation_list.size()];
		accommodation_latitude = new double[accommodation_list.size()];
		accommodation_longitude = new double[accommodation_list.size()];
		accommodation_url = new String[accommodation_list.size()];
		accommodation_data = new PointOfInterest[accommodation_list.size()];
		
		
		// Store accommodation data in arrays
		for (int i = 0; i < accommodation_list.size(); i++) {
			accommodation_id[i] = accommodation_list.get(i).getID();			
			accommodation_name[i] = accommodation_list.get(i).getName();
			services[i] = accommodation_list.get(i).getServices();
			accommodation_latitude[i] = accommodation_list.get(i).getLatitude();
			accommodation_longitude[i] = accommodation_list.get(i).getLongitude();
			accommodation_url[i] = accommodation_list.get(i).getContentURL();
		}
				
		// Store custom PointOfInterest object (name, services)
		for (int i = 0; i < accommodation_list.size(); i++) {
			accommodation_data[i] = new PointOfInterest(accommodation_id[i], accommodation_name[i], services[i]);
		}
			

		// Display accommodation name, service type in ListView
		Log.i("AccommodationListView", "Adding accommodation data to list view.");
		accommodation_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.point_of_interest_list, R.id.list_item, accommodation_data);
		lv_accommodation.setAdapter(accommodation_adapter);
		

		// Detect text entered when user performs search
		search_accommodation.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				Accommodation.this.accommodation_adapter.getFilter().filter(cs);
				is_filtered = true;
			}
			
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub	
			}
			
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub							
			}
			
		});

		
		// Display more specific information about a particular attraction
		lv_accommodation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// Check whether ListView is filtered (user performs search)
				if (is_filtered) {
					
					Log.i("LIST_FILTERED", "ListView is filtered");
					
					// Get current List Item position
					String currentItem = lv_accommodation.getItemAtPosition(position).toString();
				
					// Update List Item position when ListView is filtered
					for (int updatedIndex = 0; updatedIndex < accommodation_data.length; updatedIndex++) {
						if (currentItem.equals(accommodation_data[updatedIndex].toString())) {
							position = updatedIndex;
							break;
						}
					}
				}
								
				Intent accommodationIntent = new Intent(getApplicationContext(), AccommodationInfo.class);
				accommodationIntent.putExtra("KEY_ID", accommodation_id[position]);
				accommodationIntent.putExtra("KEY_NAME", accommodation_name[position]);
				accommodationIntent.putExtra("KEY_SERVICES", services[position]);
				accommodationIntent.putExtra("KEY_LATITUDE", accommodation_latitude[position]);
				accommodationIntent.putExtra("KEY_LONGITUDE", accommodation_longitude[position]);
				accommodationIntent.putExtra("KEY_CONTENT_URL", accommodation_url[position]);
				accommodationIntent.putExtra("KEY_ACCOMMODATION_ITEM_POSITION", position);
				accommodationIntent.putExtra("KEY_TYPE", "Accommodation");
				
				startActivity(accommodationIntent);
				finish();
			}
					
		});
			
		// Close database
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accommodation, menu);
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
