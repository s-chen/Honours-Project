package si.chen.honours.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainMenu extends ActionBarActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint("Search Edinburgh");       
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_maps:
            	Toast.makeText(getApplicationContext(), "Opening maps...", Toast.LENGTH_SHORT).show();
            	Intent intentMap = new Intent(this, MapActivity.class);
            	startActivity(intentMap);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// Get status code
    	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
    	
    	/* Checks whether Google Play Services APK is up-to-date and direct user to Google Play store
    	if Google Play services is out of date or missing or to system settings if service is disabled */
    	if(resultCode != ConnectionResult.SUCCESS) {
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
    		dialog.show();
    	} 
    	Log.d("GooglePlayVersionCheck", "resultCode: " + resultCode);
    }
}
