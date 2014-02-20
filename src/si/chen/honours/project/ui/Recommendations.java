package si.chen.honours.project.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import si.chen.honours.project.R;
import si.chen.honours.project.login.FacebookLogin;
import si.chen.honours.project.login.LoggedInFragment;
import si.chen.honours.project.utility.DatabaseHelper;
import si.chen.honours.project.utility.PointOfInterest;
import si.chen.honours.project.utility.aws.AWSHelper;
import si.chen.honours.project.utility.aws.User;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.services.simpledb.model.Item;

// Computes similarity of user place ratings and provides a list of recommendations to the user
public class Recommendations extends Activity {
	
	/**
	 *  HashMap of similarity scores between the user currently logged in to Facebook and another user's ratings
	 *  Key = another user's id
	 *  Value = similarity score between that user and the Facebook user (currently logged in)
	 */
	private HashMap<String,String> similarity_scores = new HashMap<String,String>();
	
	/**
	 * HashMap of place_id and place ratings from a user that has the highest similarity score with currently logged in user (Facebook user)
	 * Currently logged in user (Facebook user) has not visited these places
	 * Provide these as recommendations
	 */
	private HashMap<String,String> recommended_place_id_ratings = new HashMap<String,String>();
	
	// Format of ArrayList - UserID:{place_id=place_ratings,....,}
	private ArrayList<String> user_id_ratings_list = new ArrayList<String>();
		
	private String CURRENT_FB_USER_ID = LoggedInFragment.USER_ID;
	private String CURRENT_FB_USER_RATINGS;
	
	private DatabaseHelper dbHelper;
	private ListView lv_recommendations;
	
	private PointOfInterest point_of_interest_data;
	private ArrayList<PointOfInterest> point_of_interest_data_list = new ArrayList<PointOfInterest>();
	private ArrayAdapter<PointOfInterest> point_of_interest_data_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommendations);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("My Recommendations");
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		dbHelper = new DatabaseHelper(this);
		
		// Set up ListView
		lv_recommendations = (ListView) findViewById(R.id.listView_recommendations);
		
		// Start new thread to obtain recommendations for a user
		new RetrieveRecommendations().execute();
	}
	
	// Retrieve recommendations for a user by computing similarity of ratings between users
	public class RetrieveRecommendations extends AsyncTask<Void, Void, HashMap<String,String>> {
		
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
		
		
		/**
		 *  - Get all User IDs and their corresponding place ratings
		 *  (skipping over empty place ratings or user ID (if currently logged in to Facebook)
		 *  - Compute similarity scores between ratings from user currently logged in to Facebook and another user's ratings (Added to HashMap)
		 *  - Find candidate user with largest similarity score in relation with currently logged in user (shares similar tastes, potential for recommendation)
		 *  - Find all place_id of candidate user which the currently logged in user hasn't visited
		 *  - Sort list of place ratings in descending order
		 */
		@Override
		protected HashMap<String,String> doInBackground(Void... params) {
			
			AWSHelper aws = new AWSHelper();
			List<Item> userIDs = aws.getUserIDs();
			
			// Get ratings for user currently logged in to Facebook
			User FACEBOOK_USER = new User(CURRENT_FB_USER_ID);
			List<Item> FB_USER_RATINGS = aws.getPlaceRatings(FACEBOOK_USER);
			if (!FB_USER_RATINGS.isEmpty()) {
				String current_user_ratings = aws.getPlaceRatingsForItem(FB_USER_RATINGS.get(0));
				CURRENT_FB_USER_RATINGS = current_user_ratings;
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
			Log.i("OTHER_USER'S_ID_RATINGS", user_id_ratings_list.toString());
			
			
			
			for (int i = 0; i < user_id_ratings_list.size(); i++) {
				
				// Extract user_id
				String user_id = user_id_ratings_list.get(i).split(":")[0];
				// Extract user ratings and compute similarity between user's ratings and Facebook user's ratings (currently logged in)
				double sim_score = computeSimilarity(user_id_ratings_list.get(i).split(":")[1]);

				// Add entry to HashMap of similarity scores
				similarity_scores.put(user_id, String.valueOf(sim_score));
			}
			Log.i("HASHMAP_SIMILARITY_SCORES", similarity_scores.toString());
			
			
			
			// HashMap of user and facebook user ratings respectively
			HashMap<String,String> user_ratings_map = new HashMap<String,String>();
			HashMap<String,String> facebook_user_ratings_map = new HashMap<String,String>();
			
			// Find user ID with maximum similarity score to currently logged in (Facebook user)
			String user_id_max_similarity = findUserIDWithMaxSimilarity(similarity_scores);
			User user_max_similarity = new User(user_id_max_similarity);
			
			// String format of HashMap of ratings
			List<Item> user_ratings_list_items = aws.getPlaceRatings(user_max_similarity);
			String user_ratings = aws.getPlaceRatingsForItem(user_ratings_list_items.get(0));
			
			// Convert String to HashMap
			if (!user_ratings.equals("")) {
				user_ratings_map = User.convertToHashMap(user_ratings);
			}
			if (!CURRENT_FB_USER_RATINGS.equals("")) {
				facebook_user_ratings_map = User.convertToHashMap(CURRENT_FB_USER_RATINGS);
			}
			
			
			// Loop over all place_id from user with highest similarity to Facebook user
			for (String place_id : user_ratings_map.keySet()) {
				
				// User currently logged in to Facebook has not visited the place
				if (!facebook_user_ratings_map.containsKey(place_id)) {
					recommended_place_id_ratings.put(place_id, user_ratings_map.get(place_id));
				}
			}
			
			// Sort ratings in descending order
			recommended_place_id_ratings = sortByRatings(recommended_place_id_ratings);
			
			Log.i("RECOMMENDED_PLACE_ID_RATINGS", recommended_place_id_ratings.toString());
			
			return recommended_place_id_ratings;
		}
		
		// Display list of recommended places in descending order of ratings
		@Override
		protected void onPostExecute(HashMap<String,String> recommended_place_id_ratings) {
			dialog.dismiss();
			
			
			for (String place_id : recommended_place_id_ratings.keySet()) {
				point_of_interest_data = dbHelper.getPOIDataByID(place_id);
								
				// Store in list of points of interest
				point_of_interest_data_list.add(point_of_interest_data);
				
				// Close database
				dbHelper.close();
			}
			
			// Add point of interest data to ListView
			point_of_interest_data_adapter = new ArrayAdapter<PointOfInterest>(Recommendations.this, R.layout.recommendations_list, R.id.recommendations_list_item, point_of_interest_data_list);
			lv_recommendations.setAdapter(point_of_interest_data_adapter);
			
			
			// Clear ListView when user current logged in to Facebook does not have any ratings (recommendations not accurate)
			if (CURRENT_FB_USER_RATINGS.equals("")) {
				lv_recommendations.setAdapter(null);
				Toast.makeText(getApplicationContext(), "Please submit some ratings in order to get recommendations!", Toast.LENGTH_LONG).show();
			}
			
			
			// Handle clicks on specific recommended place (view more information)
			lv_recommendations.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					
					Intent intent = new Intent(Recommendations.this, RecommendedPlaceInfo.class);
					intent.putExtra("KEY_ID", point_of_interest_data_list.get(position).getID());
					intent.putExtra("KEY_NAME", point_of_interest_data_list.get(position).getName());
					intent.putExtra("KEY_SERVICE", point_of_interest_data_list.get(position).getServices());
					intent.putExtra("KEY_LATITUDE", point_of_interest_data_list.get(position).getLatitude());
					intent.putExtra("KEY_LONGITUDE", point_of_interest_data_list.get(position).getLongitude());
					intent.putExtra("KEY_CONTENT_URL", point_of_interest_data_list.get(position).getContentURL());
					intent.putExtra("KEY_RECOMMENDATION_ITEM_POSITION", position);
					intent.putExtra("KEY_TYPE", point_of_interest_data_list.get(position).getServices());
					
					startActivity(intent);
				}
			});
			
		}
			
	}
	
	/**
	 * 
	 * @param another_user_ratings: another user's ratings
	 * @return similarity score with current user logged in to Facebook
	 */
	public double computeSimilarity(String another_user_ratings) {
				
		double partial_euclidean_distance = 0.0;
		double euclidean_distance = 0.0;
		double similarity_score = 0.0;
		
		
		// Temporary HashMaps for FB USER and other user's ratings respectively
		HashMap<String,String> temp_fb_user_ratings = new HashMap<String,String>();
		HashMap<String,String> temp_user_ratings = new HashMap<String,String>();
		
		
		// Convert String format to HashMap
		if (!CURRENT_FB_USER_RATINGS.equals("")) {
			temp_fb_user_ratings = User.convertToHashMap(CURRENT_FB_USER_RATINGS);
		}
		if (!another_user_ratings.equals("")) {
			temp_user_ratings = User.convertToHashMap(another_user_ratings);
		}
		
		
		// Loop over place_id for current user logged in to Facebook
		for (String fb_user_key : temp_fb_user_ratings.keySet()) {
						
			// Loop over place_id for a user
			for (String user_key : temp_user_ratings.keySet()) {
				
				// Check for the same place_id
				if (fb_user_key.equals(user_key)) {
					
					// Get place rating
					double fb_user_rating = Double.valueOf(temp_fb_user_ratings.get(fb_user_key));
					double user_rating = Double.valueOf(temp_user_ratings.get(user_key));
					
					/**
					 * User 1 ratings: r1{1}, r1{2}, r1{3},..., r1{n}
					 * User 2 ratings: r2{1}, r2{2}, r2{3},..., r2{n}
					 * Compute the partial Euclidean Distance:
					 * (r1{1} - r2{1})^2 + (r1{2} - r2{2})^2 + ... + (r1{n} - r2{n})
					 */
					partial_euclidean_distance += Math.pow((fb_user_rating - user_rating),2);
				}
			}
		}
		
		// Euclidean distance computed from square root of partial Euclidean distance
		euclidean_distance = Math.sqrt(partial_euclidean_distance);
		
		// Similarity score compute by: 1/(1 + euclidean_distance)
		similarity_score = 1.0/(1.0 + euclidean_distance);
		
		return similarity_score;
	}
	
	// Finds the User ID with the highest similarity score to the user currently logged in to Facebook
	public String findUserIDWithMaxSimilarity(HashMap<String,String> similarity_scores) {
		
		double max_similarity_score = 0.0;
		String user_id = "";
		
		// Loop over HashMap with key = user_id, value = similarity score
		for (String uid : similarity_scores.keySet()) {
			
			double sim_score = Double.valueOf(similarity_scores.get(uid));
			
			if (sim_score > max_similarity_score) {
				max_similarity_score = sim_score;
				user_id = uid;
			}
		}
		return user_id;
	}
	
	// Sort place_id based on ratings in descending order
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static LinkedHashMap<String,String> sortByRatings(HashMap<String,String> unsortedMap) {
 		
		List<Map.Entry> list = new ArrayList<Map.Entry>(unsortedMap.entrySet());
		LinkedHashMap<String,String> sorted_place_id_ratings = new LinkedHashMap<String,String>();
		 
		// Sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;
				return ((Comparable) e2.getValue()).compareTo(e1.getValue());
			}
		});
 
		for (Map.Entry e : list) {
			sorted_place_id_ratings.put((String) e.getKey(), (String) e.getValue());
		}
		
		return sorted_place_id_ratings;
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
