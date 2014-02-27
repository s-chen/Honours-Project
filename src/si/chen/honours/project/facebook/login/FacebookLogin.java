package si.chen.honours.project.facebook.login;

import si.chen.honours.project.R;
import si.chen.honours.project.ui.MainMenu;
import si.chen.honours.project.utility.UserSessionManager;
import si.chen.honours.project.utility.aws.AWSHelper;
import si.chen.honours.project.utility.aws.User;
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;


// Login Activity, allow users to Log In to Facebook (using non-authenticated and authenticated fragments)
public class FacebookLogin extends FragmentActivity {

	
	private static final int LOGIN_SPLASH_FRAGMENT = 0;
	private static final int LOGGED_IN_FRAGMENT = 1;
	private static final int LOGOUT_FRAGMENT = 2;
	private static final int FRAGMENT_COUNT = LOGOUT_FRAGMENT + 1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.activity_facebook_login);
	    
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Log In");
		actionBar.setDisplayHomeAsUpEnabled(true);
	
		
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);

	    
	    // Define non-authenticated and authenticated UI fragments
	    FragmentManager fm = getSupportFragmentManager();
	    fragments[LOGIN_SPLASH_FRAGMENT] = fm.findFragmentById(R.id.loginSplashFragment);
	    fragments[LOGGED_IN_FRAGMENT] = fm.findFragmentById(R.id.loggedInFragment);
	    // Logout fragment
	    fragments[LOGOUT_FRAGMENT] = fm.findFragmentById(R.id.userSettingsFragment);

	    // Initially hide fragments
	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	}

	// Show given fragment and hide other fragments
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		
	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	            // If the session state is open:
	            // Show the authenticated fragment
	            showFragment(LOGGED_IN_FRAGMENT, false);
	            Log.i("LOGGED_IN_FRAGMENT", "Showing authenticated fragment");
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(LOGIN_SPLASH_FRAGMENT, false);
	            Log.i("LOGIN_SPLASH_FRAGMENT", "Showing non-authenticated fragment");
	        }
	    }
	}
	
	// Check session status
	private Session.StatusCallback callback = new Session.StatusCallback() {
		public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
		}
	};
	

	@Override
	protected void onResumeFragments() {
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the authenticated fragment
	        showFragment(LOGGED_IN_FRAGMENT, false);
	        Log.i("LOGGED_IN_FRAGMENT", "Showing authenticated fragment");
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(LOGIN_SPLASH_FRAGMENT, false);
	        Log.i("LOGIN_SPLASH_FRAGMENT", "Showing non-authenticated fragment");
	    }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	    
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		// Handle presses on the action bar items
		switch(item.getItemId()) {
		case android.R.id.home:
			 // Go to previous screen when app icon in action bar is clicked
            Intent intent = new Intent(this, MainMenu.class);
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
            return true;
        case R.id.action_logout:
        	// Log out from Facebook
        	showFragment(LOGOUT_FRAGMENT, true);
        	Log.i("LOGOUT_FRAGMENT", "Showing logout fragment");
        	return true;
		default:
		      return super.onOptionsItemSelected(item);
		}
	}
	
    @Override
    public void onBackPressed() {
    	
    	Intent intent = new Intent(this, MainMenu.class);
    	startActivity(intent);
    	finish();
    }
}

