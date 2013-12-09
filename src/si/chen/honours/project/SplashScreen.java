package si.chen.honours.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreen extends Activity {

	// Display welcome screen for 0.1 seconds
	private final int SPLASH_DURATION = 100; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       
    	new Handler().postDelayed(new Runnable() {
    	
    		// This method is executed once timer expires
    		public void run() {
    			// Start Main Menu activity and finish current activity
    			Intent intent = new Intent(SplashScreen.this, MainMenu.class);
    			startActivity(intent);
    			finish();
    		}
    	}, SPLASH_DURATION);
    }

         
}
