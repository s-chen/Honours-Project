package si.chen.honours.project.ui;

import org.json.JSONObject;

import si.chen.honours.project.R;
import si.chen.honours.project.utility.GoogleAPIHelper;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayNearbyPlaceInfo extends Activity {

	GoogleAPIHelper nearbyPlaceHelper;
	private JSONObject nearby_place_details;
	
	private String place_name;
	private String place_reference;
	private TextView textView_place_name;
	private TextView textView_place_address;
	private TextView textView_place_phone_number;
 	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_nearby_place_info);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("View Nearby Place information");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		// Get place name, reference passed from DisplayNearbyPlaces
		Intent intent = getIntent();
		place_name = intent.getStringExtra("KEY_NAME");
		place_reference = intent.getStringExtra("KEY_REFERENCE");
		
		
		textView_place_address = (TextView) findViewById(R.id.textView_nearby_place_address);
		textView_place_phone_number = (TextView) findViewById(R.id.textView_nearby_place_phone_number);
		
		// Set nearby place name in TextView 
		textView_place_name = (TextView) findViewById(R.id.textView_nearby_place_name);
		textView_place_name.setText(place_name);
		
		
		// Execute thread to search for nearby place details
		new getNearbyPlaceDetails().execute();
	}
	
	// Starts AsyncTask to request additional details of nearby place using reference id
	private class getNearbyPlaceDetails extends AsyncTask<String, String, JSONObject> {
		
		private ProgressDialog dialog;
		
		// Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(DisplayNearbyPlaceInfo.this);
	        dialog.setMessage("Retrieving information..");
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(false);
	        dialog.show();
		 }
		
		// Get JSON object from URL 
		@Override
        protected JSONObject doInBackground(String... args) {
            
            nearbyPlaceHelper = new GoogleAPIHelper(place_reference);
            
            nearby_place_details = nearbyPlaceHelper.getNearbyPlaceDetailsResponse();
            
            return nearby_place_details;
        }
		
		@Override
		protected void onPostExecute(JSONObject json) {
			dialog.dismiss();
			
			runOnUiThread(new Runnable(){
				public void run() {
					
					try {
						
						// Check status from JSON response
						String place_detail_status = nearby_place_details.getString("status");
						
						if (place_detail_status.equals("OK")) {
							
							String formatted_address = nearby_place_details.getJSONObject("result").getString("formatted_address");
							String formatted_phone_number = nearby_place_details.getJSONObject("result").getString("formatted_phone_number");
							
							Log.i("NEARBY_PLACE_ADDRESS", formatted_address);
							Log.i("NEARBY_PLACE_PHONE_NUMBER", formatted_phone_number);
							
							// Set place address, phone no. in TextView
							textView_place_address.setText(formatted_address);
							textView_place_phone_number.setText(formatted_phone_number);
							
						} else {
							Log.i("STATUS_INFO", place_detail_status);
							
							textView_place_address.setText("Address not available.");
							textView_place_phone_number.setText("Phone number not available.");
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_nearby_place_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, DisplayNearbyPlaces.class);
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
    	
    	Intent intent = new Intent(this, DisplayNearbyPlaces.class);
    	startActivity(intent);
    	finish();
    }
}
