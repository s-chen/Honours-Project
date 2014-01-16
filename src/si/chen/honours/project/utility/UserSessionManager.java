package si.chen.honours.project.utility;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

// Manages user interaction with the application (including SharedPreferences)
public class UserSessionManager {
	
	SharedPreferences mPref;
	Editor mEditor;
	Context mContext;
	int PRIVATE_MODE = 0;	

	
	/**
	 * Constructor
	 * @param context - specifies the activity we are on
	 * @param pref_name - name of SharedPref for particular activity (SharedPref stored under pref_name)
	 */
	public UserSessionManager(Context context, final String pref_name) {
		
		this.mContext = context;
		mPref = context.getSharedPreferences(pref_name, PRIVATE_MODE);
		mEditor = mPref.edit();
	}
		
	// Store place data in SharedPrefs
	public void storePlaceData(String name, double latitude, double longitude) {
		
		ArrayList<String> place_data = new ArrayList<String>();
		
		int position = mPref.getInt("KEY_DATA_POSITION", 0);

		place_data.add(name);
		place_data.add(String.valueOf(latitude));
		place_data.add(String.valueOf(longitude));
			
		mEditor.putString("USER_DATA_" + position, place_data.toString());
		mEditor.commit();
	}
	
	// Get a single place data from SharedPrefs
	public String getPlaceData() {
		
		int position = mPref.getInt("KEY_DATA_POSITION", 0);
		
		return mPref.getString("USER_DATA_" + position, null);
	}
	
	// Get all place data from SharedPrefs
	public void getAllPlacesData() {
		
		// String array for {place name, place latitude, place longitude}
		String[] parsed_place_value = new String[3];
		
		ArrayList<String> place_name = new ArrayList<String>();
				
		// Get SharedPref map
		Map<String,?> prefsMap = mPref.getAll();
		
		// Iterate over SharedPref map and get key, value pairs
		for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
			
			// Don't add position to ArrayList of place names
			if (entry.getKey().equals("KEY_DATA_POSITION")) {
				continue;
			}
			
			// Store place name, place latitude, place longitude from values obtained in SharedPref map
			parsed_place_value = entry.getValue().toString().split(",");
			
			// Obtain place name and remove all occurrences of '[' and ']'
			String name = parsed_place_value[0];
			name = name.replaceAll("\\[|\\]", "");
			
			
			// Add place name to ArrayList
			place_name.add(name);
			
			
			
			Log.i("SHARED_PREFS_DATA", entry.getKey() + ": " + entry.getValue().toString());
		}
		
		for (int i = 0; i < place_name.size(); i++) {
			System.out.println(place_name.get(i));
		}
	}
	
	// Store the position of the point of interest item that was clicked in the ListView 
	public void storeItemPosition(int position) {
		mEditor.putInt("KEY_DATA_POSITION", position);
		mEditor.commit();
	}
	
	
	// Checks whether a point of interest is already in the itinerary
	public boolean existInItinerary() {
		
		int position = mPref.getInt("KEY_DATA_POSITION", 0);
		
		if (mPref.contains("USER_DATA_" + position)) {
			return true;
		} else {
			return false;
		}
	}
	
	// Remove all items added to the itinerary
	public void deleteItineraryItems() {
		mEditor.clear();
		mEditor.commit();
	}

	

}
