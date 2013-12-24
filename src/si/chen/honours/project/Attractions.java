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

public class Attractions extends ActionBarActivity {

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attractions);
		
		setTitle("Tourist Attractions");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		lv_attractions = (ListView) findViewById(R.id.listView_attractions);
		
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
			attractions_data[i] = new PointOfInterest(attractions_name[i], services[i]);
		}
			

		// Display attractions name, service type in ListView
		Log.i("AttractionsListView", "Adding attractions data to list view.");
		attractions_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.activity_attractions, R.id.textView_attractions_info, attractions_data);
		lv_attractions.setAdapter(attractions_adapter);
		

		
		
		// Display more specific information about a particular attraction
		lv_attractions.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				Intent attractionsIntent = new Intent(getApplicationContext(), AttractionsInfo.class);
				attractionsIntent.putExtra("KEY_ID", attractions_id[position]);
				attractionsIntent.putExtra("KEY_NAME", attractions_name[position]);
				attractionsIntent.putExtra("KEY_SERVICES", services[position]);
				attractionsIntent.putExtra("KEY_LATITUDE", attractions_latitude[position]);
				attractionsIntent.putExtra("KEY_LONGITUDE", attractions_longitude[position]);
				attractionsIntent.putExtra("KEY_CONTENT_URL", attractions_url[position]);
						
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
