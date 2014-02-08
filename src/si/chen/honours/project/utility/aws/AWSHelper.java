package si.chen.honours.project.utility.aws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;

public class AWSHelper {

	private static final String ACCESS_KEY_ID = "AKIAJX5QC35XRKVF3XNQ";
	private static final String SECRET_KEY = "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";
	protected AmazonSimpleDBClient sdbClient;
	
	// Domain name
	private static final String USERS_DOMAIN = "Users";
	
	// Attributes
	private static final String USER_ID_ATTRIBUTE = "user_id";
	private static final String USER_EMAIL_ATTRIBUTE = "user_email";
	private static final String USER_PROFILE_NAME_ATTRIBUTE = "user_profile_name";
	private static final String USER_RATINGS_ATTRIBUTE = "user_ratings";
	

	
	// Constructor
	public AWSHelper() {
	     // Initialise SimpleDB Client.
		AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY);
		this.sdbClient = new AmazonSimpleDBClient(credentials); 
	    this.sdbClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
	}
	
	
    /*
     * Creates a new User and store it to the Users domain - Register using user ID from Facebook
     */
	public void storeUserInfo(User user) {
		
		ReplaceableAttribute userIdAttribute = new ReplaceableAttribute(USER_ID_ATTRIBUTE, user.getUserId(), Boolean.TRUE );
		ReplaceableAttribute userEmailAttribute = new ReplaceableAttribute(USER_EMAIL_ATTRIBUTE, user.getUserEmail(), Boolean.TRUE );
		ReplaceableAttribute userProfileNameAttribute = new ReplaceableAttribute(USER_PROFILE_NAME_ATTRIBUTE, user.getUserProfileName(), Boolean.TRUE );
		
		List<ReplaceableAttribute> user_attributes = new ArrayList<ReplaceableAttribute>(2);
		user_attributes.add(userIdAttribute);
		user_attributes.add(userEmailAttribute);
		user_attributes.add(userProfileNameAttribute);
		
		PutAttributesRequest request = new PutAttributesRequest(USERS_DOMAIN, user.getUserId(), user_attributes);		
		try {
			this.sdbClient.putAttributes(request);
		}
		catch ( Exception exception ) {
			System.out.println( "EXCEPTION = " + exception );
		}
	}
	
	// Get user information using from Facebook UserID
	public List<Item> getUserInfo(User user) {
		
		String getUserInfoQuery = "select user_email, user_profile_name from " + USERS_DOMAIN + " where user_id = " + "'" + user.getUserId() + "'";
		System.out.println(getUserInfoQuery);
		
		SelectRequest selectRequest = new SelectRequest(getUserInfoQuery).withConsistentRead(true);
		SelectResult response = this.sdbClient.select(selectRequest);
		
		return response.getItems();
	}
	
	// Store user place ratings to SimpleDB
	public void storePlaceRatings(User user) {
		
		ReplaceableAttribute userRatingAttribute = new ReplaceableAttribute(USER_RATINGS_ATTRIBUTE, user.getUserRatings().toString(), Boolean.TRUE );
		
		List<ReplaceableAttribute> user_rating = new ArrayList<ReplaceableAttribute>(2);
		user_rating.add(userRatingAttribute);
		
		PutAttributesRequest request = new PutAttributesRequest(USERS_DOMAIN, user.getUserId(), user_rating);		
		try {
			this.sdbClient.putAttributes(request);
		}
		catch ( Exception exception ) {
			System.out.println( "EXCEPTION = " + exception );
		}	
	}
	
	// Get user place ratings from SimpleDB
	public List<Item> getPlaceRatings(User user) {
		
		String getPlaceRatingsQuery = "select user_ratings from " + USERS_DOMAIN + " where user_id = " + "'" + user.getUserId() + "'";
		
		SelectRequest selectRequest = new SelectRequest(getPlaceRatingsQuery).withConsistentRead(true);
		SelectResult response = this.sdbClient.select(selectRequest);
		
		return response.getItems();
	}
	
	// Extracts "user_ratings" attribute from SimpleDB Item
	public String getPlaceRatingsForItem(Item item) {
		return this.getStringValueForAttributeFromList(USER_RATINGS_ATTRIBUTE, item.getAttributes());
	}
	
	
	/*
	 * Extracts the "user_email" attribute from the SimpleDB Item.
	 */
	public String getUserEmailForItem(Item item) {
		return this.getStringValueForAttributeFromList(USER_EMAIL_ATTRIBUTE, item.getAttributes());
	}
	

    /*
     * Removes user from USERS_DOMAIN in SimpleDB
     */
	public void deleteUser(User user) {
		DeleteAttributesRequest deleteRequest = new DeleteAttributesRequest(USERS_DOMAIN, user.getUserId());
		this.sdbClient.deleteAttributes(deleteRequest);
	}
	
    /*
     * Extracts the value for the given attribute from the list of attributes.
     * Extracted value is returned as a String.
     */
	protected String getStringValueForAttributeFromList( String attributeName, List<Attribute> attributes ) {
		for ( Attribute attribute : attributes ) {
			if ( attribute.getName().equals( attributeName ) ) {
				return attribute.getValue();
			}
		}
		
		return "";		
	}
}
