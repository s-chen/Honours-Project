package si.chen.honours.project.facebook.login;

import si.chen.honours.project.R;
import si.chen.honours.project.facebook.places.FacebookPlacePicker;
import si.chen.honours.project.ui.Recommendations;
import si.chen.honours.project.utility.aws.AWSHelper;
import si.chen.honours.project.utility.aws.User;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

/** 
 * - Set up authenticated UI fragment for Facebook to show user when authenticated
 * - Makes API call to retrieve user data from Facebook
 * - Connect to AWS to add user to SimpleDB if user details not already in DB 
 */
public class LoggedInFragment extends Fragment implements OnClickListener{

	private UiLifecycleHelper uiHelper;
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private Button myRecommendations;
	private Button fbNearbyPlaces;
	
	public static String USER_ID;
	private String USER_PROFILE_NAME;
	private String USER_EMAIL;
	
	// For making permissions request
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.activity_logged_in_fragment, container, false);

	    // Find the user's profile picture custom view
	    profilePictureView = (ProfilePictureView) view.findViewById(R.id.profile_picture);
	    profilePictureView.setCropped(true);
	    
	    // Find the user's name view
	    userNameView = (TextView) view.findViewById(R.id.profile_name);
	    
	    // Find user recommendations button view
	    myRecommendations = (Button) view.findViewById(R.id.my_recommendations);
	    myRecommendations.setOnClickListener(this);
	    
	    // Initially disable button press until AsyncTask finishes
	    myRecommendations.setEnabled(false);
	    
	    
	    // Display places nearby using Facebook Graph
	    fbNearbyPlaces = (Button) view.findViewById(R.id.facebook_whats_near_here);
	    fbNearbyPlaces.setOnClickListener(this);
	    
	    // Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	    	Log.i("FB_LOGIN", "User logged in");
	        // Get the user's data
	        makeMeRequest(session);
	    } else {
	    	Log.i("FB_LOGIN", "User not logged in..");
	    }
	    
	    return view;
	    
	}
	
	// Called when buttons are clicked
	public void onClick(View view) {
		switch (view.getId()) {
		// "My Recommendations" button
		case R.id.my_recommendations:
			Intent intent = new Intent(getActivity(), Recommendations.class);
			startActivity(intent);
		case R.id.facebook_whats_near_here:
			Intent fbNearbyPlaceIntent = new Intent(getActivity(), FacebookPlacePicker.class);
			startActivity(fbNearbyPlaceIntent);
		}
	}
	
	// Requests user data from Facebook
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                	
                    	// User Facebook ID
                    	USER_ID = user.getId();	                    	
                    	// User Facebook Profile name
                        USER_PROFILE_NAME = user.getName();
                        // User email
                        USER_EMAIL = user.asMap().get("email").toString();

                        // Set user profile picture using ID
	                    profilePictureView.setProfileId(USER_ID);
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText("Welcome, " + USER_PROFILE_NAME);
	                    
	                    
            		    Log.i("FB_USER_ID", USER_ID);
            		    Log.i("FB_USER_PROFILE_NAME", USER_PROFILE_NAME);
            		    Log.i("FB_USER_EMAIL", USER_EMAIL);
            		    
                        // Start thread to connect to Amazon SimpleDB
                        new ConnectAWS().execute();
	                }
	            } 
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	
	public class ConnectAWS extends AsyncTask<Void, Void, Boolean> {
		
		/** Check whether Facebook user info are in Amazon SimpleDB **/
		@Override
		protected Boolean doInBackground(Void... params) {
			
			AWSHelper aws = new AWSHelper();
			User user = new User(USER_ID, USER_PROFILE_NAME, USER_EMAIL);
			
			
			// User info NOT in SimpleDB, store user info
			if (aws.getUserInfo(user).isEmpty()) {
				aws.storeUserInfo(user);
				return false;
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean flag) {
			if (flag) {
				// User details already in SimpleDB
				Log.i("SIMPLE_DB", "User details already in SimpleDB");
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "User information already in SimpleDB", Toast.LENGTH_SHORT).show();
				}
			} else {
				// User details not in SimpleDB
				Log.i("SIMPLE_DB", "Storing user details in SimpleDB");
				if (getActivity() != null) {
					Toast.makeText(getActivity(), "Storing user information in SimpleDB", Toast.LENGTH_SHORT).show();
				}
			}
			
			// Enable button press
			myRecommendations.setEnabled(true);
		}
	}
	
	// Check session status
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		// Start request if user has logged in
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	

	private Session.StatusCallback callback = new Session.StatusCallback() {
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

}
