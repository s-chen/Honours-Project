package si.chen.honours.project;

import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Accommodation extends ActionBarActivity {

	private List<PointOfInterest> accommodation_list;
	private int[] accommodation_id;
	private String[] accommodation_name;
	private String[] services;
	private double[] accommodation_latitude;
	private double[] accommodation_longitude;
	private String[] accommodation_url;
	private ArrayAdapter<PointOfInterest> accommodation_adapter;
	private ListView lv_accommodation;
	private PointOfInterest accommodation_data[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accommodation);
		
		setTitle("Accommodation");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		lv_accommodation = (ListView) findViewById(R.id.listView_accommodation);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		accommodation_list = dbHelper.getAccommodationData();
		
		// Uses Comparator from PointOfInterest class to sort accommodation
		Collections.sort(accommodation_list);

		
		// Initialise arrays for storing accommodation data
		accommodation_id = new int[accommodation_list.size()];
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
			accommodation_data[i] = new PointOfInterest(accommodation_name[i], services[i]);
		}
			

		// Display accommodation name, service type in ListView
		Log.i("AccommodationListView", "Adding accommodation data to list view.");
		accommodation_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.activity_accommodation, R.id.textView_accommodation_info, accommodation_data);
		lv_accommodation.setAdapter(accommodation_adapter);
		

		
		
		// Display more specific information about a particular attraction
		lv_accommodation.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				Intent accommodationIntent = new Intent(getApplicationContext(), AccommodationInfo.class);
				accommodationIntent.putExtra("KEY_ID", accommodation_id[position]);
				accommodationIntent.putExtra("KEY_NAME", accommodation_name[position]);
				accommodationIntent.putExtra("KEY_SERVICES", services[position]);
				accommodationIntent.putExtra("KEY_LATITUDE", accommodation_latitude[position]);
				accommodationIntent.putExtra("KEY_LONGITUDE", accommodation_longitude[position]);
				accommodationIntent.putExtra("KEY_CONTENT_URL", accommodation_url[position]);
						
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
