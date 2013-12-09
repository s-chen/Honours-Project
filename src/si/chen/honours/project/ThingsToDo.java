package si.chen.honours.project;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.basewarp.basewarp.services.UserBubbles;

public class ThingsToDo extends ActionBarActivity {

	// Test for Mark's bubbles with bubbleID = 0, UID = 154
	private UserBubbles osmBubbles = new UserBubbles(0, 154);
	private String testURL = osmBubbles.getUserBubbleURL(0, 154);

	
	// JSON Node names (Basewarp structure)
	private static final String TAG_STATUS = "status";
	private static final String TAG_RESPONSE = "response";
	private static final String TAG_ID = "id";
	private static final String TAG_TYPE_ID = "type_id";
	private static final String TAG_USER_ID = "user_id";
	private static final String TAG_LOCATION_ID = "location_id";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_IS_PINNED = "is_pinned";
	private static final String TAG_CONTENT_URL = "content_url";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";
	private static final String TAG_RADIUS = "radius";
	private static final String TAG_START_DATE_TIME = "start_datetime";
	private static final String TAG_START_END_TIME = "end_datetime";
	private static final String TAG_ADDED_DATE_TIME = "added_datetime";
	private static final String TAG_NUM_OPENS = "num_opens";
	private static final String TAG_IS_WARPABLE = "is_warpable";
	private static final String TAG_UUID = "uuid";
	private static final String TAG_TIME_DIFF = "time_diff_seconds";
	private static final String TAG_DISTANCE = "distance";
	private static final String TAG_FORENAME = "forename";
	private static final String TAG_PROFILE_IMAGE = "has_profile_image";
	private static final String TAG_STREET_ADDRESS = "street_address";
	private static final String TAG_USER_RATING = "user_rating";
	
	JSONArray response = null;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_things_to_do);
		
		setTitle("Things To Do");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	
		
		// HashMap for ListView
		ArrayList<HashMap<String,String>> bubbleList = new ArrayList<HashMap<String,String>>();
		
		JSONParser jsonParser = new JSONParser();
		
		JSONObject jsonObject = jsonParser.getJSONFromURL(testURL);
		
		System.out.println("THIS IS A TEST: " + testURL);
		
		
		try {
			// Get response JSON array (bubble)
			response = jsonObject.getJSONArray(TAG_RESPONSE);
			
			// loop over JSON array and get each JSON item 
			for(int i = 0; i < response.length(); i++) {
				
				JSONObject item = response.getJSONObject(i);
				
				String user_id = item.getString(TAG_USER_ID);
				String description = item.getString(TAG_DESCRIPTION);
				String content_url = item.getString(TAG_CONTENT_URL);
				String latitude = item.getString(TAG_LATITUDE);
				String longitude = item.getString(TAG_LONGITUDE);
				String radius = item.getString(TAG_RADIUS);
				String street_address = item.getString(TAG_STREET_ADDRESS);
				
				// Create HashMap, add key, Value pair
				HashMap<String,String> map = new HashMap<String,String>();
				
				//map.put(TAG_USER_ID, user_id);
				map.put(TAG_DESCRIPTION, description);
				map.put(TAG_CONTENT_URL, content_url);
				map.put(TAG_LATITUDE, latitude);
				map.put(TAG_LONGITUDE, longitude);
				map.put(TAG_RADIUS, radius);
				map.put(TAG_STREET_ADDRESS, street_address);
				
				bubbleList.add(map);
				
			} 
			
		} catch (JSONException e) {
			System.out.println("JSON parsing failed!");
			e.printStackTrace();
		}
		
		
		/*** Update ListView with parsed JSON data from Basewarp services 
		 * ***/
		ListView lv = (ListView) findViewById(R.id.listView_bubbles);
		
		ArrayAdapter<HashMap<String,String>> adapter = new ArrayAdapter<HashMap<String,String>>(this, android.R.layout.simple_list_item_1, bubbleList);
		lv.setAdapter(adapter);
	
		/*lv.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
				Intent intent = new Intent(getApplicationContext(), SingleBubble.class);
				intent.putExtra(TAG_USER_ID, user_id);
				
			}
		});*/
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.things_to_do, menu);
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
