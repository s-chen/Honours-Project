package si.chen.honours.project.utility;

import java.util.ArrayList;
import java.util.Map;

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
	public void storePlaceData(String name, String type, double latitude, double longitude) {
		
		ArrayList<String> place_data = new ArrayList<String>();
		
		int position = mPref.getInt("KEY_DATA_POSITION", 0);

		// Store name, type, latitude, longitude to ArrayList
		place_data.add(name);
		place_data.add(type);
		place_data.add(String.valueOf(latitude));
		place_data.add(String.valueOf(longitude));
		
			
		// Store key = position of selected ListView item, value = [name, type, latitude, longitude] in SharedPrefs
		mEditor.putString("USER_DATA_" + position, place_data.toString());
		mEditor.commit();
	}
	
	// Get all place data from a specific SharedPref (Attractions, Food, Drinks, Accommodation, Shopping etc.)
	public ArrayList<String> getAllPlacesData() {
		
		ArrayList<String> place_data = new ArrayList<String>();
		
		// String array for [place name, place type, place latitude, place longitude]
		String[] parsed_place_value = new String[4];
				
		// Get SharedPref map
		Map<String,?> prefsMap = mPref.getAll();
		
		// Iterate over SharedPref map and get key, value pairs
		for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
			
			// Ignore position data for clicked ListView item
			if (entry.getKey().equals("KEY_DATA_POSITION")) {
				continue;
			}
			
			// Store place name, place type, place latitude, place longitude from values obtained in SharedPref map
			parsed_place_value = entry.getValue().toString().split(",");
	
			// Obtain place name and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String name = parsed_place_value[0];
			name = name.replaceAll("\\[|\\]", "");
			name = name.trim();
			
			// Obtain place type and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String type = parsed_place_value[1];
			type = type.replaceAll("\\[|\\]", "");
			type = type.trim();
			
			// Obtain place latitude and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String latitude = parsed_place_value[2];
			latitude = latitude.replaceAll("\\[|\\]", "");
			latitude = latitude.trim();
					
			// Obtain place longitude and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String longitude = parsed_place_value[3];
			longitude = longitude.replaceAll("\\[|\\]", "");
			longitude = longitude.trim();
			
			// Add place name, type, latitude, longitude to ArrayList (separated by ## symbol)
			place_data.add(name + "##" + type + "##" + latitude + "##" + longitude);
			
			
			Log.i("SHARED_PREFS_DATA", entry.getKey() + ": " + entry.getValue().toString());
		}
		
		return place_data;
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
	
	// Delete itinerary items for specific SharedPref
	public void deleteItineraryItems() {
		mEditor.clear();
		mEditor.commit();
	}

	

}
