package si.chen.honours.project.utility.aws;

import java.io.Serializable;
import java.io.Serializable;

// User profile data (with user attributes) to be stored in Amazon SimpleDB
public class User implements Serializable {	
	private static final long serialVersionUID = 1L;

	private String user_id;
	private String user_email;
	private String user_password;
	
	private String user_rating;


	
	// Constructor for user attributes to be stored in Amazon SimpleDB
	public User(String user_id, String user_email, String user_password) {
		this.user_id = user_id;
		this.user_email = user_email;
		this.user_password = user_password;
	}
	
		
	public String getUserId() {
		return this.user_id;
	}
		
	public String getUserEmail() {
		return this.user_email;
	}
		
	public String getUserPassword() {
		return this.user_password;
	}
}
