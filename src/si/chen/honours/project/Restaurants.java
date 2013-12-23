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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Called when 'Food' button pressed **/
public class Restaurants extends ActionBarActivity {
	
	private List<PointOfInterest> restaurant_list;
	private int[] restaurant_id;
	private String[] restaurant_name;
	private String[] services;
	private double[] restaurant_latitude;
	private double[] restaurant_longitude;
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
		
		
		// Uses Comparator from PointOfInterest class to sort restaurant names
		Collections.sort(restaurant_list);

		// Initialise arrays for storing restaurant data
		restaurant_id = new int[restaurant_list.size()];
		restaurant_name = new String[restaurant_list.size()];
		services = new String[restaurant_list.size()];
		restaurant_latitude = new double[restaurant_list.size()];
		restaurant_longitude = new double[restaurant_list.size()];
		restaurant_url = new String[restaurant_list.size()];
		restaurant_data = new PointOfInterest[restaurant_list.size()];
		
		
		
		// Store restaurant data in arrays
		for (int i = 0; i < restaurant_list.size(); i++) {
			restaurant_id[i] = restaurant_list.get(i).getID();
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

		
		// Display restaurant name, service type in ListView
		Log.i("RestaurantListView", "Adding restaurant data to list view.");
		restaurant_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.activity_restaurants, R.id.textView_restaurant_info, restaurant_data);
		lv_restaurants.setAdapter(restaurant_adapter);
	

		// Display more specific information about a particular restaurant
		lv_restaurants.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent restaurantIntent = new Intent(getApplicationContext(), RestaurantInfo.class);
				restaurantIntent.putExtra("KEY_ID", restaurant_id[position]);
				restaurantIntent.putExtra("KEY_NAME", restaurant_name[position]);
				restaurantIntent.putExtra("KEY_SERVICES", services[position]);
				restaurantIntent.putExtra("KEY_LATITUDE", restaurant_latitude[position]);
				restaurantIntent.putExtra("KEY_LONGITUDE", restaurant_longitude[position]);
				restaurantIntent.putExtra("KEY_CONTENT_URL", restaurant_url[position]);
				
				startActivity(restaurantIntent);
				finish();
			}
			
		});
		
		// Close database
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
