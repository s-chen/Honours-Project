package si.chen.honours.project.utility.aws;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// User profile data (with user attributes) to be stored in Amazon SimpleDB
public class User implements Serializable {	
	private static final long serialVersionUID = 1L;

	private String user_id;
	private String user_email;
	private String user_profile_name;
	private HashMap<String,String> user_ratings;
	
	/**
	 * Global HashMap to store user ratings for points of interest (key = place id, value = place rating)
	 */
	public static HashMap<String,String> user_place_ratings = new HashMap<String,String>();


	// Constructor for a User object to be stored in in Amazon SimpleDB
	public User(String user_id, String user_profile_name, String user_email) {
		this.user_id = user_id;
		this.user_profile_name = user_profile_name;
		this.user_email = user_email;
	}
	
	// Constructor for updating user ratings
	public User(String user_id, HashMap<String,String> user_ratings) {
		this.user_id = user_id;
		this.user_ratings = user_ratings;
	}
	
	public User(String user_id) {
		this.user_id = user_id;
	}
	
	
	public String getUserId() {
		return this.user_id;
	}
		
	public String getUserProfileName() {
		return this.user_profile_name;
	}
		
	public String getUserEmail() {
		return this.user_email;
	}
	
	public HashMap<String,String> getUserRatings() {
		return this.user_ratings;
	}
	
	/**
	 *  Converts String format of HashMap retrieved from Amazon SimpleDB into a HashMap
	 *  (Since SimpleDB cannot store HashMap as an attribute directly)
	**/ 
	public HashMap<String,String> convertToHashMap(String hashMap) {
		
		HashMap<String, String> restored_user_place_ratings = new HashMap<String,String>();
		
		String formatted_place_ratings = hashMap.replaceAll("\\{|\\}", "");
		String[] formatted_place_ratings_array = formatted_place_ratings.split(",");
	
		for (int i = 0; i < formatted_place_ratings_array.length; i++) {
			
			formatted_place_ratings = formatted_place_ratings_array[i].trim().replaceAll("=", " ");
			restored_user_place_ratings.put(formatted_place_ratings.split(" ")[0], formatted_place_ratings.split(" ")[1]);
		}
		
		return restored_user_place_ratings;

	}
		

}
