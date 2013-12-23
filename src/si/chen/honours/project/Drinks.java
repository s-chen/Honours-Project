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

public class Drinks extends ActionBarActivity {

	private List<PointOfInterest> drinks_list;
	private int[] drinks_id;
	private String[] drinks_name;
	private String[] services;
	private double[] drinks_latitude;
	private double[] drinks_longitude;
	private String[] drinks_url;
	private ArrayAdapter<PointOfInterest> drinks_adapter;
	private ListView lv_drinks;
	private PointOfInterest drinks_data[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drinks);
		
		setTitle("Drinks");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		lv_drinks = (ListView) findViewById(R.id.listView_drinks);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		drinks_list = dbHelper.getDrinksData();
		
		// Uses Comparator from PointOfInterest class to sort drinks names
		Collections.sort(drinks_list);

		
		// Initialise arrays for storing drinks data
		drinks_id = new int[drinks_list.size()];
		drinks_name = new String[drinks_list.size()];
		services = new String[drinks_list.size()];
		drinks_latitude = new double[drinks_list.size()];
		drinks_longitude = new double[drinks_list.size()];
		drinks_url = new String[drinks_list.size()];
		drinks_data = new PointOfInterest[drinks_list.size()];
		
		
		// Store drinks data in arrays
		for (int i = 0; i < drinks_list.size(); i++) {
			drinks_id[i] = drinks_list.get(i).getID();
			drinks_name[i] = drinks_list.get(i).getName();
			services[i] = drinks_list.get(i).getServices();
			drinks_latitude[i] = drinks_list.get(i).getLatitude();
			drinks_longitude[i] = drinks_list.get(i).getLongitude();
			drinks_url[i] = drinks_list.get(i).getContentURL();
		}
				
		// Store custom PointOfInterest object (name, services)
		for (int i = 0; i < drinks_list.size(); i++) {
			drinks_data[i] = new PointOfInterest(drinks_name[i], services[i]);
		}
			

		// Display drinks name, service type in ListView
		Log.i("DrinksListView", "Adding drinks data to list view.");
		drinks_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.activity_drinks, R.id.textView_drinks_info, drinks_data);
		lv_drinks.setAdapter(drinks_adapter);
		
		
		
		// Display more specific information about a particular drinks service
		lv_drinks.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				Intent drinksIntent = new Intent(getApplicationContext(), DrinksInfo.class);
				drinksIntent.putExtra("KEY_ID", drinks_id[position]);
				drinksIntent.putExtra("KEY_NAME", drinks_name[position]);
				drinksIntent.putExtra("KEY_SERVICES", services[position]);
				drinksIntent.putExtra("KEY_LATITUDE", drinks_latitude[position]);
				drinksIntent.putExtra("KEY_LONGITUDE", drinks_longitude[position]);
				drinksIntent.putExtra("KEY_CONTENT_URL", drinks_url[position]);
						
				startActivity(drinksIntent);
				finish();
			}
					
		});
		
		// Close database
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drinks, menu);
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
