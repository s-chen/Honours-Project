package si.chen.honours.project;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Called when 'Restaurant' button pressed **/
public class Restaurants extends ActionBarActivity {
	
	private List<PointOfInterest> restaurant_list;
	private int[] id;
	private String[] restaurant_name;
	private String[] services;
	private float[] restaurant_latitude;
	private float[] restaurant_longitude;
	private String[] restaurant_url;
	private ArrayAdapter<PointOfInterest> restaurant_adapter;
	private ListView lv_restaurants;
	private PointOfInterest restaurant_data[];
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurants);
		
		setTitle("Restaurants and Takeaways");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		
		lv_restaurants = (ListView) findViewById(R.id.listView_restaurants);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		restaurant_list = dbHelper.getRestaurantData();
		

		// Initialise arrays for storing restaurant data
		id = new int[restaurant_list.size()];
		restaurant_name = new String[restaurant_list.size()];
		services = new String[restaurant_list.size()];
		restaurant_latitude = new float[restaurant_list.size()];
		restaurant_longitude = new float[restaurant_list.size()];
		restaurant_url = new String[restaurant_list.size()];
		restaurant_data = new PointOfInterest[restaurant_list.size()];
		
		
		
		// Store restaurant data in arrays
		for (int i = 0; i < restaurant_list.size(); i++) {
			id[i] = restaurant_list.get(i).getID();
			restaurant_name[i] = restaurant_list.get(i).getName();
			services[i] = restaurant_list.get(i).getServices();
			restaurant_latitude[i] = restaurant_list.get(i).getLatitude();
			restaurant_longitude[i] = restaurant_list.get(i).getLongitude();
			restaurant_url[i] = restaurant_list.get(i).getContentURL();
		}
		
		// Store custom PointOfInterest object (name, services)
		for (int i = 0; i < restaurant_list.size(); i++) {
			restaurant_data[i] = new PointOfInterest(restaurant_name[i], services[i]);
		}
				
		Log.i("RestaurantListView", "Adding restaurant data to list view.");
		restaurant_adapter = new ArrayAdapter<PointOfInterest>(this, android.R.layout.simple_list_item_1, restaurant_data);
		lv_restaurants.setAdapter(restaurant_adapter);
		
		
		
		
		
		
		
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurants, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
		}
		return true;
	}
	
    @Override
    public void onBackPressed() {
    	
    	Intent intent = new Intent(this, MainMenu.class);
    	startActivity(intent);
    	finish();
    }

}
