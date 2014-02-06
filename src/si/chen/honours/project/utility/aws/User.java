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
	
	// Hashmap which has key = place id, value = rating for each user
	private HashMap<String,Double> user_place_rating = new HashMap<String,Double>();



	// Constructor for a User object to be stored in in Amazon SimpleDB
	public User(String user_id, String user_profile_name, String user_email) {
		this.user_id = user_id;
		this.user_profile_name = user_profile_name;
		this.user_email = user_email;
	}
	
	public User(String user_id, String place_id, double place_rating) {
		this.user_id = user_id;
		user_place_rating.put(place_id, place_rating);
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
		

}
