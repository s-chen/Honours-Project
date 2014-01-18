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

//Display list of Attractions
public class Attractions extends Activity {

	private List<PointOfInterest> attractions_list;
	private int[] attractions_id;
	private String[] attractions_name;
	private String[] services;
	private double[] attractions_latitude;
	private double[] attractions_longitude;
	private String[] attractions_url;
	private ArrayAdapter<PointOfInterest> attractions_adapter;
	private ListView lv_attractions;
	private PointOfInterest attractions_data[];
	
	private EditText search_attractions;
	private boolean is_filtered = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attractions);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Tourist Attractions");
		actionBar.setDisplayHomeAsUpEnabled(true);

		
		lv_attractions = (ListView) findViewById(R.id.listView_attractions);
		search_attractions = (EditText) findViewById(R.id.editText_attractions_search);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		attractions_list = dbHelper.getAttractionsData();
		
		// Uses Comparator from PointOfInterest class to sort attractions
		Collections.sort(attractions_list);

		
		// Initialise arrays for storing attractions data
		attractions_id = new int[attractions_list.size()];
		attractions_name = new String[attractions_list.size()];
		services = new String[attractions_list.size()];
		attractions_latitude = new double[attractions_list.size()];
		attractions_longitude = new double[attractions_list.size()];
		attractions_url = new String[attractions_list.size()];
		attractions_data = new PointOfInterest[attractions_list.size()];
		
		
		// Store attractions data in arrays
		for (int i = 0; i < attractions_list.size(); i++) {
			attractions_id[i] = attractions_list.get(i).getID();
			attractions_name[i] = attractions_list.get(i).getName();
			services[i] = attractions_list.get(i).getServices();
			attractions_latitude[i] = attractions_list.get(i).getLatitude();
			attractions_longitude[i] = attractions_list.get(i).getLongitude();
			attractions_url[i] = attractions_list.get(i).getContentURL();
		}
				
		// Store custom PointOfInterest object (name, services)
		for (int i = 0; i < attractions_list.size(); i++) {
			attractions_data[i] = new PointOfInterest(i+1, attractions_name[i], services[i]);
		}
			

		// Display attractions name, service type in ListView
		Log.i("AttractionsListView", "Adding attractions data to list view.");
		attractions_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.point_of_interest_list, R.id.list_item, attractions_data);
		lv_attractions.setAdapter(attractions_adapter);
		
		
		// Detect text entered when user performs search
		search_attractions.addTextChangedListener(new TextWatcher() {
							
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				Attractions.this.attractions_adapter.getFilter().filter(cs);
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
		lv_attractions.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				// Check whether ListView is filtered (user performs search)
				if (is_filtered) {
					
					Log.i("LIST_FILTERED", "ListView is filtered");
					
					// Get current List Item position
					String currentItem = lv_attractions.getItemAtPosition(position).toString();
					
					// Update List Item position when ListView is filtered
					for (int updatedIndex = 0; updatedIndex < attractions_data.length; updatedIndex++) {
						if (currentItem.equals(attractions_data[updatedIndex].toString())) {
							position = updatedIndex;
							break;
						}
					}
				}
				
				Intent attractionsIntent = new Intent(getApplicationContext(), AttractionsInfo.class);
				attractionsIntent.putExtra("KEY_ID", attractions_id[position]);
				attractionsIntent.putExtra("KEY_NAME", attractions_name[position]);
				attractionsIntent.putExtra("KEY_SERVICES", services[position]);
				attractionsIntent.putExtra("KEY_LATITUDE", attractions_latitude[position]);
				attractionsIntent.putExtra("KEY_LONGITUDE", attractions_longitude[position]);
				attractionsIntent.putExtra("KEY_CONTENT_URL", attractions_url[position]);
				attractionsIntent.putExtra("KEY_ATTRACTION_ITEM_POSITION", position);
				attractionsIntent.putExtra("KEY_TYPE", "Attraction");
						
				startActivity(attractionsIntent);
				finish();
			}
					
		});
		
		// Close database
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attractions, menu);
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
