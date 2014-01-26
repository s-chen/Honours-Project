package si.chen.honours.project.utility.aws;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
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
	private static final String USER_NAME_ATTRIBUTE = "user_name";
	private static final String USER_RATING_ATTRIBUTE = "user_rating";
	
	private String query = "select * from " + USERS_DOMAIN;

	
	
	public AWSHelper() {
	     // Initialise SimpleDB Client.
		AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY);
		this.sdbClient = new AmazonSimpleDBClient(credentials); 
	    this.sdbClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
	}
	

/*    
     * Extracts the 'user_id' attribute from the SimpleDB Item.
     
	protected String getPlayerForItem( Item item ) {
		return this.getStringValueForAttributeFromList(USER_ID_ATTRIBUTE, item.getAttributes() );
	}

    
     * Extracts the 'score' attribute from the SimpleDB Item.
     
	protected int getScoreForItem( Item item ) {
		return this.getIntValueForAttributeFromList( SCORE_ATTRIBUTE, item.getAttributes() );
	}*/
	
    /*
     * Creates a new User and adds it to the Users domain.
     */
	public void addUserInfo(User user) {
		
		ReplaceableAttribute userIdAttribute = new ReplaceableAttribute(USER_ID_ATTRIBUTE, user.getUserId(), Boolean.TRUE );
		ReplaceableAttribute userNameAttribute = new ReplaceableAttribute(USER_NAME_ATTRIBUTE, user.getUserName(), Boolean.TRUE );
		ReplaceableAttribute userRatingAttribute = new ReplaceableAttribute(USER_RATING_ATTRIBUTE, user.getUserRatings(), Boolean.TRUE);
		
		List<ReplaceableAttribute> user_attributes = new ArrayList<ReplaceableAttribute>(2);
		user_attributes.add(userIdAttribute);
		user_attributes.add(userNameAttribute);
		user_attributes.add(userRatingAttribute);
		
		PutAttributesRequest request = new PutAttributesRequest(USERS_DOMAIN, user.getUserName(), user_attributes);		
		try {
			this.sdbClient.putAttributes(request);
		}
		catch ( Exception exception ) {
			System.out.println( "EXCEPTION = " + exception );
		}
	}
	
	public List<Item> getUserInfo() {
		
		SelectRequest selectRequest = new SelectRequest(query).withConsistentRead( true );
		SelectResult response = this.sdbClient.select(selectRequest);
		
		return response.getItems();
	}
}
