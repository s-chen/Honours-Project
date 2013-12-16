package si.chen.honours.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RestaurantInfo extends ActionBarActivity {

	private GoogleMap restaurant_map;
	private Intent restaurantIntent;
	private int restaurant_id;
	private String restaurant_name;
	private String services;
	private float restaurant_latitude;
	private float restaurant_longitude;
	private String restaurant_url;
	private LatLng restaurant_location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurant_info);
		
		setTitle("View information");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		/** Get restaurant data passed from Restaurants.java class **/
		Log.i("RetrieveData", "Retrieve selected restaurant data.");
        restaurantIntent = getIntent();
        restaurant_id = restaurantIntent.getIntExtra("KEY_ID", 0);
        restaurant_name = restaurantIntent.getStringExtra("KEY_NAME");
        services = restaurantIntent.getStringExtra("KEY_SERVICES");
        restaurant_latitude = restaurantIntent.getFloatExtra("KEY_LATITUDE", 0);
        restaurant_longitude = restaurantIntent.getFloatExtra("KEY_LONGITUDE", 0);
		restaurant_url = restaurantIntent.getStringExtra("KEY_CONTENT_URL");
		
		// Get restaurant location from intent
		restaurant_location = new LatLng(restaurant_latitude, restaurant_longitude);
				
		// Display map fragment, with marker of restaurant location
		restaurant_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.restaurant_map)).getMap();
		Marker location = restaurant_map.addMarker(new MarkerOptions().position(restaurant_location).title(restaurant_name + " (" + services + ")"));
		restaurant_map.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getPosition(), 17));
				
		// Display current user's location
		restaurant_map.setMyLocationEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurant_info, menu);
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
    	
    	Intent intent = new Intent(this, Restaurants.class);
    	startActivity(intent);
    	finish();
    }
}
