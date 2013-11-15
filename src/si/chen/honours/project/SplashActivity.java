package si.chen.honours.project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity {

	// Display welcome screen for 1000ms
	private final int SPLASH_DISPLAY_TIME = 1000;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	// Retrieve shared preferences, default to false if not available
    	boolean splashScreenShown = prefs.getBoolean("splashScreenShown", false);
    	
    	if (!splashScreenShown) {
    		
    		// Show splash screen for 1 second
    		new Handler().postDelayed(new Runnable() {
    			
    			public void run() {
    				
    				// Create intent to start Main Menu activity
    				Intent intent = new Intent(SplashActivity.this, MainMenu.class);
    				startActivity(intent);
    				finish();
    			}
    		}, SPLASH_DISPLAY_TIME);
    		
    	} else {
    		// Splash screen shown, immediately start Main Menu activity
    		Intent intent = new Intent(SplashActivity.this, MainMenu.class);
    		startActivity(intent);
    		finish();
    	}
    }
      
}
