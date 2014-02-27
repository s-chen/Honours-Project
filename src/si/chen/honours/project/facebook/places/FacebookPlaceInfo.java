package si.chen.honours.project.facebook.places;

import org.json.JSONObject;

import si.chen.honours.project.R;
import si.chen.honours.project.facebook.login.FacebookLogin;
import si.chen.honours.project.ui.Accommodation;
import si.chen.honours.project.ui.MainMenu;
import si.chen.honours.project.utility.GoogleAPIHelper;
import si.chen.honours.project.utility.JSONParser;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class FacebookPlaceInfo extends Activity {
	
	private String FACEBOOK_PLACE_ID;
	
	private String selected_place_info;
	private JSONObject place_information;
	
	JSONParser jParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_place_info);
		
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Facebook Place Information");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		selected_place_info = intent.getStringExtra("KEY_FACEBOOK_PLACE_GRAPH");

		
		// Try and get place ID from JSONObject
		try {
			JSONObject place_id = new JSONObject(selected_place_info);
			FACEBOOK_PLACE_ID = place_id.getString("id");
			Log.i("FACEBOOK_PLACE_ID", FACEBOOK_PLACE_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Start thread and get place details if we can get the place ID
		if (!FACEBOOK_PLACE_ID.equals("")) {
			new getFacebookPlaceDetails().execute();
		}

	}
	
	private class getFacebookPlaceDetails extends AsyncTask<String, String, JSONObject>{
		
		private ProgressDialog dialog;
		
		// Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(FacebookPlaceInfo.this);
	        dialog.setMessage("Retrieving Nearby Places..");
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(false);
	        dialog.show();
		}
		
		// Get JSON object from String 
		@Override
		protected JSONObject doInBackground(String... args) {
			
			String fb_graph_api_url = "https://graph.facebook.com/" + FACEBOOK_PLACE_ID;

			place_information = jParser.getJSONFromURL(fb_graph_api_url);
			return place_information;
        }
		
		@Override
		protected void onPostExecute(JSONObject json) {
			dialog.dismiss();
			
			try {
				System.out.println(json.toString());
				
				String status = place_information.getString("category");
				
				System.out.println(status);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facebook_place_info, menu);
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	//	gps.stopUsingGPS();
	}
	
    @Override
    public void onPause() {
    	super.onPause();
    //	gps.stopUsingGPS();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();;
    //	gps.stopUsingGPS();
    }
    
    @Override
    public void onRestart() {
    	super.onRestart();
    	
    	// Show GPS settings menu, if not detected
    //	if (!gps.canGetLocation()) {
    //		gps.showGPSSettingsAlert();
   // 	}
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
	
	@Override
    public void onBackPressed() {
    	
    //	gps.stopUsingGPS();
    	
    	Intent intent = new Intent(this, Accommodation.class);
    	startActivity(intent);
    	finish();
    }

}
