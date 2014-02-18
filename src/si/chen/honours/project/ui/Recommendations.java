package si.chen.honours.project.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import si.chen.honours.project.R;
import si.chen.honours.project.login.FacebookLogin;
import si.chen.honours.project.login.LoggedInFragment;
import si.chen.honours.project.utility.aws.AWSHelper;
import si.chen.honours.project.utility.aws.User;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Item;

// Computes similarity of user place ratings and provides a list of recommendations to the user
public class Recommendations extends Activity {
	
	private ArrayList<String> user_id_ratings_list = new ArrayList<String>();
	private HashMap<String,String> similarity_scores = new HashMap<String,String>();
	
	private String CURRENT_FB_USER_ID = LoggedInFragment.USER_ID;
	private String CURRENT_FB_USER_RATINGS;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommendations);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("My Recommendations");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Start new thread to obtain recommendations for a user
		new RetrieveRecommendations().execute();
	}
	
	// Retrieve recommendations for a user by computing similarity of ratings between users
	public class RetrieveRecommendations extends AsyncTask<Void, Void, ArrayList<String>> {
		
		private ProgressDialog dialog;
		
		// Show Progress Dialog
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(Recommendations.this);
	        dialog.setMessage("Retrieving recommendations..");
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(false);
	        dialog.show();
		 }
		
		
		// Get all User IDs and their corresponding place ratings
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			
			AWSHelper aws = new AWSHelper();
			List<Item> userIDs = aws.getUserIDs();
			
			// Get ratings for currently logged in user
			User FACEBOOK_USER = new User(CURRENT_FB_USER_ID);
			List<Item> FB_USER_RATINGS = aws.getPlaceRatings(FACEBOOK_USER);
			if (!FB_USER_RATINGS.isEmpty()) {
				String current_user_ratings = aws.getPlaceRatingsForItem(FB_USER_RATINGS.get(0));
				CURRENT_FB_USER_RATINGS = current_user_ratings;
			} else {
				Toast.makeText(getApplicationContext(), "Please submit some ratings in order to get recommendations!", Toast.LENGTH_SHORT).show();
			}
			
			// Get all User IDs from AWS SimpleDB
			for (int i = 0; i < userIDs.size(); i++) {
				// Current User ID
				String user_id = aws.getUserIDForItem(userIDs.get(i));
				User user = new User(user_id);
				
				// Get place ratings
				List<Item> user_ratings = aws.getPlaceRatings(user);
				// String format of HashMap of place ratings
				String ratings = aws.getPlaceRatingsForItem(user_ratings.get(0));
				
				
				/** Skip over User ID if it is the current User ID that is logged into Facebook
				 * OR
				 * If user doesn't have any place ratings
				 * (since we want to get recommendations for the currently logged in user from other user's ratings)
				**/
				if (user_id.equals(CURRENT_FB_USER_ID) || ratings.equals("")) {
					continue;
				}

				// Format of String - user_id:{place_id=place_ratings,....,}
				String user_id_ratings = user_id + ":" + ratings;
					
				// Add entry to ArrayList
				user_id_ratings_list.add(user_id_ratings);
				
			}
			
			return user_id_ratings_list;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> user_id_ratings_list) {
			dialog.dismiss();
			
			for (int i = 0; i < user_id_ratings_list.size(); i++) {
				
				computeSimilarity(user_id_ratings_list.get(i));

			}
			
		}
			
	}
	
	/**
	 * 
	 * @param another_user_id_ratings: another user's ID and ratings
	 * @return a HashMap where key = another user's ID, value = similarity score with current user logged in to Facebook
	 */
	public HashMap<String, String> computeSimilarity(String another_user_id_ratings) {
		
		User user = new User();
		
		// Temporary HashMaps for FB USER and other user's ratings respectively
		HashMap<String,String> temp_fb_user_ratings = new HashMap<String,String>();
		HashMap<String,String> temp_user_ratings = new HashMap<String,String>();
		
		// Get user's ID and ratings
		String user_id = another_user_id_ratings.split(":")[0];
		String user_ratings = another_user_id_ratings.split(":")[1];
		
		temp_fb_user_ratings = user.convertToHashMap(CURRENT_FB_USER_RATINGS);
		temp_user_ratings = user.convertToHashMap(user_ratings);
		

		for (String fb_user_key : temp_fb_user_ratings.keySet()) {
			
			for (String user_key : temp_user_ratings.keySet()) {
				
				if (fb_user_key.equals(user_key)) {
					// COMPUTE SIMILARITY (SAME PLACE ID)
				}
			}
		}
		
		return temp_user_ratings;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recommendations, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				 // Go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, FacebookLogin.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            finish();
				return true;
	        case R.id.action_home:
	        	// Go to Main Menu
	            Intent homeIntent = new Intent(this, MainMenu.class);
	            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(homeIntent);
	            finish();
			default:
			      return super.onOptionsItemSelected(item);
		}
	}
}
