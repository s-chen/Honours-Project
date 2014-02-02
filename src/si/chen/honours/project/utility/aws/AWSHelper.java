package si.chen.honours.project.utility.aws;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
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
	private static final String USER_PASSWORD_ATTRIBUTE = "user_password";
	
	
	private static final String USER_RATING_ATTRIBUTE = "user_rating";
	

	
	private String query = "select * from " + USERS_DOMAIN;

	
	
	public AWSHelper() {
	     // Initialise SimpleDB Client.
		AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY);
		this.sdbClient = new AmazonSimpleDBClient(credentials); 
	    this.sdbClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
	}
	
	
    /*
     * Creates a new User and adds it to the Users domain (USER_ID, USER_EMAIL, USER_PASSWORD) - Register using Android default login activity.
     */
	public void registerUserInfo(User user) {
		
		ReplaceableAttribute userIdAttribute = new ReplaceableAttribute(USER_ID_ATTRIBUTE, user.getUserId(), Boolean.TRUE );
		ReplaceableAttribute userEmailAttribute = new ReplaceableAttribute(USER_EMAIL_ATTRIBUTE, user.getUserEmail(), Boolean.TRUE );
		ReplaceableAttribute userPasswordAttribute = new ReplaceableAttribute(USER_PASSWORD_ATTRIBUTE, user.getUserPassword(), Boolean.TRUE );

		
		List<ReplaceableAttribute> user_attributes = new ArrayList<ReplaceableAttribute>(2);
		user_attributes.add(userIdAttribute);
		user_attributes.add(userEmailAttribute);
		user_attributes.add(userPasswordAttribute);
		
		PutAttributesRequest request = new PutAttributesRequest(USERS_DOMAIN, user.getUserId(), user_attributes);		
		try {
			this.sdbClient.putAttributes(request);
		}
		catch ( Exception exception ) {
			System.out.println( "EXCEPTION = " + exception );
		}
	}
	
	// Get user information using USER_EMAIL as key
	public List<Item> getUserInfoFromEmail(String user_email) {
		
		String test = "select user_email, user_password from " + USERS_DOMAIN + " where user_email = " + "'" + user_email + "'";
		System.out.println(test);
		
		SelectRequest selectRequest = new SelectRequest(test).withConsistentRead( true );
		SelectResult response = this.sdbClient.select(selectRequest);
		
		return response.getItems();
	}
	
	/*
	 * Extracts the "user_email" attribute from the SimpleDB Item.
	 */
	public String getUserEmailForItem(Item item) {
		return this.getStringValueForAttributeFromList(USER_EMAIL_ATTRIBUTE, item.getAttributes());
	}
	
	/*
	 * Extracts the "user_password" attribute from the SimpleDB Item.
	 */
	public String getUserPasswordForItem(Item item) {
		return this.getStringValueForAttributeFromList(USER_PASSWORD_ATTRIBUTE, item.getAttributes());
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
