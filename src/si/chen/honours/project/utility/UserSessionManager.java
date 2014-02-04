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
	public void storePlaceData(String id, String name, String type, double latitude, double longitude) {
		
		ArrayList<String> place_data = new ArrayList<String>();
		
		int position = mPref.getInt("KEY_DATA_POSITION", 0);

		// Store id, name, type, latitude, longitude to ArrayList
		place_data.add(id);
		place_data.add(name);
		place_data.add(type);
		place_data.add(String.valueOf(latitude));
		place_data.add(String.valueOf(longitude));
		
			
		// Store key = position of selected ListView item, value = [id, name, type, latitude, longitude] in SharedPrefs
		mEditor.putString("USER_DATA_" + position, place_data.toString());
		mEditor.commit();
	}
	
	// Get all place data from a specific SharedPref (Attractions, Food, Drinks, Accommodation, Shopping etc.)
	public ArrayList<String> getAllPlacesData() {
		
		ArrayList<String> place_data = new ArrayList<String>();
		
		// String array for [place id, place name, place type, place latitude, place longitude]
		String[] parsed_place_value = new String[5];
				
		// Get SharedPref map
		Map<String,?> prefsMap = mPref.getAll();
		
		// Iterate over SharedPref map and get key, value pairs
		for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
			
			// Ignore position data for clicked ListView item
			if (entry.getKey().equals("KEY_DATA_POSITION")) {
				continue;
			}
			
			// Store place id, place name, place type, place latitude, place longitude from values obtained in SharedPref map
			parsed_place_value = entry.getValue().toString().split(",");

			// Obtain place id and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String id = parsed_place_value[0];
			id = id.replaceAll("\\[|\\]", "");
			id = id.trim();
			
			// Obtain place name and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String name = parsed_place_value[1];
			name = name.replaceAll("\\[|\\]", "");
			name = name.trim();
			
			// Obtain place type and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String type = parsed_place_value[2];
			type = type.replaceAll("\\[|\\]", "");
			type = type.trim();
			
			// Obtain place latitude and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String latitude = parsed_place_value[3];
			latitude = latitude.replaceAll("\\[|\\]", "");
			latitude = latitude.trim();
					
			// Obtain place longitude and remove all occurrences of '[' and ']' and leading/trailing whitespace
			String longitude = parsed_place_value[4];
			longitude = longitude.replaceAll("\\[|\\]", "");
			longitude = longitude.trim();
			
			// Obtain SharedPref key for place
			String sharedPref_key = entry.getKey();
			
			// Add place id, name, type, latitude, longitude, SharedPref key to ArrayList (separated by ## symbol)
			place_data.add(id + "##" + name + "##" + type + "##" + latitude + "##" + longitude + "##" + sharedPref_key);
			
			
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
	
	// Delete all itinerary items from a SharedPref
	public void deleteAllItineraryItems() {
		mEditor.clear();
		mEditor.commit();
	}
	
	// Delete a specific itinerary item
	public void deleteItineraryItem(String key) {
		mEditor.remove(key);
		mEditor.commit();
	}

	

}
