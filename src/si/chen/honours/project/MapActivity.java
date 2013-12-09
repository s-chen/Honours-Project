package si.chen.honours.project;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends ActionBarActivity {

	private GoogleMap map;
	final LatLng EDINBURGH = new LatLng(55.9531, -3.1889);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		setTitle("Map of Edinburgh");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Open Google Maps 
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		Marker edinburgh = map.addMarker(new MarkerOptions().position(EDINBURGH).title("Edinburgh"));
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(edinburgh.getPosition(), 12));//(EDINBURGH, 12));
		
		map.setMyLocationEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
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
}
