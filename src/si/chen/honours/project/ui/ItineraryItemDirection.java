package si.chen.honours.project.ui;

import java.util.ArrayList;

import si.chen.honours.project.R;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class ItineraryItemDirection extends FragmentActivity implements OnNavigationListener {

	ArrayList<String> transportList = new ArrayList<String>();
	ArrayAdapter<String> transportAdapter;
	ArrayAdapter<CharSequence> spinnerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itinerary_item_direction);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Drop down list to select mode of transport: driving, walking; bicycling
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// Add modes of transport and bind to adapter
		transportList.add("Driving Directions");
		transportList.add("Walking Directions");
		transportList.add("Cycling Directions");
		
		// Bind ArrayList to adapter
		transportAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(), R.layout.spinner_list, R.id.spinner_list_item, transportList);
		// Populate spinner drop down list
		transportAdapter.setDropDownViewResource(R.layout.spinner_list);

		// Assign adapter to ActionBar
		actionBar.setListNavigationCallbacks(transportAdapter, this);
		
	}
	
	// Display specific direction information based on selected mode of transport in spinner drop down list 
	public boolean onNavigationItemSelected(int position, long id) {
		

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.itinerary_item_direction, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, ItineraryPlanner.class);
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
