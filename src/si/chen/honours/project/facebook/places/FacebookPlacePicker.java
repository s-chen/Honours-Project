package si.chen.honours.project.facebook.places;

import si.chen.honours.project.R;
import si.chen.honours.project.facebook.login.FacebookLogin;
import si.chen.honours.project.location.GPSListener;
import si.chen.honours.project.ui.Accommodation;
import si.chen.honours.project.ui.MainMenu;
import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.model.GraphPlace;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;

// Uses Facebook widget PlacePickerFragment to display a list of nearby places
public class FacebookPlacePicker extends FragmentActivity {
	
	private PlacePickerFragment placePickerFragment;

	private GPSListener gps;
	private Location user_location;
	private double user_latitude;
	private double user_longitude;
	  
	  
	private GraphPlace selectedPlace;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_place_picker);

		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Facebook Places");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		// Create instance of GPSListener
		gps = new GPSListener(this);
	    
		// Get user lat,lng coordinate and define current user location if GPS is enabled
		if (gps.canGetLocation()) {
	        	
			user_latitude = gps.getLatitude();
			user_longitude = gps.getLongitude();
	        	
			user_location = new Location("user_location");
			user_location.setLatitude(user_latitude);
			user_location.setLongitude(user_longitude);
		}
	        
	        
		FragmentManager fm = getSupportFragmentManager();
		placePickerFragment = (PlacePickerFragment) fm.findFragmentById(R.id.place_picker_fragment);
		if (savedInstanceState == null) {
			// If this is the first time we have created the fragment, update its properties based on
			// any parameters we received via our Intent.
			placePickerFragment.setSettingsFromBundle(getIntent().getExtras());
		}

		placePickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
			public void onError(PickerFragment<?> fragment, FacebookException error) {
				Toast.makeText(FacebookPlacePicker.this, "Cannot obtain current location", Toast.LENGTH_SHORT).show();
			}
		});

		// Get Facebook Graph object for a place, when it is selected from list
		placePickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
			public void onSelectionChanged(PickerFragment<?> fragment) {
				if (placePickerFragment.getSelection() != null) {
	    			   
					// Get currently selected item from the list
					selectedPlace = placePickerFragment.getSelection();
	    			   
					// Pass Facebook Place ID to FacebookPlaceInfo activity to obtain more information
	    			Intent intent = new Intent(FacebookPlacePicker.this, FacebookPlaceInfo.class);   
					intent.putExtra("KEY_FACEBOOK_PLACE_GRAPH", selectedPlace.getInnerJSONObject().toString());
	    			startActivity(intent);
					
					Log.i("FACEBOOK_PLACE_GRAPH", selectedPlace.getInnerJSONObject().toString());

				}
			}
		});
	        
		// Finish activity then "Done" button clicked
		placePickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
			public void onDoneButtonClicked(PickerFragment<?> fragment) {
				finish();
			}
		});
	        
	        
	}

	@Override
	protected void onStart() {
		super.onStart();
	   
		try {
			// Specify search radius, current user location and load place data
			placePickerFragment.setRadiusInMeters(1000);
			placePickerFragment.setLocation(user_location);
			placePickerFragment.loadData(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	  
	@Override
	protected void onStop() {
		super.onStop();
		gps.stopUsingGPS();
	}
	
    @Override
    public void onPause() {
    	super.onPause();
    	gps.stopUsingGPS();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();;
    	gps.stopUsingGPS();
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	
    	// Show GPS settings menu, if not detected
    	if (!gps.canGetLocation()) {
    		gps.showGPSSettingsAlert();
    	}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facebook_place_picker, menu);
		return true;
	}
  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, FacebookLogin.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            finish();
				return true;
			case R.id.action_refresh:
				refresh();
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
	
    // Refresh activity - called when 'Refresh' action button clicked
    public void refresh() {
    	
    	Intent intent = getIntent();
    	overridePendingTransition(0, 0);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

   	    overridePendingTransition(0, 0);
   	    startActivity(intent);
    }    
	
	@Override
    public void onBackPressed() {
    	
    	gps.stopUsingGPS();
    	
    	Intent intent = new Intent(this, Accommodation.class);
    	startActivity(intent);
    	finish();
    }

}
