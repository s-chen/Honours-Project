package si.chen.honours.project.utility.aws;

import java.io.Serializable;
import java.io.Serializable;

// User profile data (with user attributes) to be stored in Amazon SimpleDB
public class User implements Serializable {	
	private static final long serialVersionUID = 1L;

	private String user_id;
	private String user_name;
	private String user_rating;
		
	// Constructor for user attributes to be stored in Amazon SimpleDB
	public User(String user_id, String user_name, String user_rating) {
		this.user_id = user_id;
		this.user_name = user_name;
		this.user_rating = user_rating;
	}
		
	public String getUserId() {
		return this.user_id;
	}
		
	public String getUserName() {
		return this.user_name;
	}
	
	public String getUserRatings() {
		return this.user_rating;
	}
}
