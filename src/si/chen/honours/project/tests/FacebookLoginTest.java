package si.chen.honours.project.tests;

import si.chen.honours.project.R;
import si.chen.honours.project.facebook.login.FacebookLogin;
import si.chen.honours.project.facebook.login.LoggedInFragment;
import si.chen.honours.project.facebook.login.LoginSplashFragment;
import android.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.robotium.solo.Solo;

/**
 * JUnit test case using Robotium framework - FacebookLoginTest
 * 
 ***Start running test cases when user is not logged in to Facebook***
 ***Single Sign On must be enabled***
 * 
 * Ensures Facebook Login UI lifecycle is correctly managed
 * (LoginSplashFragment shown when user not authenticated or when user logs out)
 * (LoggedInFragment shown when user is authenticated)
 * 
 */
public class FacebookLoginTest extends ActivityInstrumentationTestCase2<FacebookLogin> {
	
	private Solo solo;

	
	public FacebookLoginTest() {
		super(FacebookLogin.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		//setUp() is run before a test case is started. 
		//This is where the solo object is created.
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	public void tearDown() throws Exception {
		//tearDown() is run after a test case has finished. 
		//finishOpenedActivities() will finish all the activities that have been opened during the test execution.
		solo.finishOpenedActivities();
	}
	
	public void testFacebookLoginUILifeCycle() {
		
		// Initially check for non-authenticated UI fragment
		assertTrue(solo.waitForFragmentById(R.id.loginSplashFragment));
		
		// User clicks 'Log In' button (with Single-Sign-On enabled)
		solo.clickOnMenuItem("Log in with Facebook");
		
		// Wait for authentication
		solo.waitForDialogToClose();
		
		// Ensure the authenticated UI fragment is shown 
		assertTrue(solo.waitForFragmentById(R.id.loggedInFragment));
		
		// Click 'LOGOUT' button on the ActionBar
		solo.clickOnActionBarItem(R.id.action_logout);
		
		// Click 'Log out' (at index 1 - User Settings Fragment)
		solo.clickOnButton(1);
		
		// Confirm Log out
		solo.clickOnMenuItem("Log out");
		
		// Back to non-authenticated UI fragment
		assertTrue(solo.waitForFragmentById(R.id.loginSplashFragment));
		
		solo.goBack();
	}
	

}
