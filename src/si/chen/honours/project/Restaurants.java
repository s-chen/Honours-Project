package si.chen.honours.project;

import java.io.IOException;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/** Called when 'Restaurant' button pressed **/
public class Restaurants extends ActionBarActivity {
	
	private ListView lv_restaurants;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurants);
		
		setTitle("Restaurants and Takeaways");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		try {
			dbHelper.openDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = dbHelper.getRestaurantData();
		
		// Columns to be bound
		String columns[] = new String[] {"name", "services"};
		// XML values which data will bound to
		int to[] = new int[] {R.id.textView_name, R.id.textView_services};
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_restaurants, cursor,
				columns, to);
		
		lv_restaurants = (ListView) findViewById(R.id.listView_restaurants);
		lv_restaurants.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.restaurants, menu);
		return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch(item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
		}
		return true;
	}
	
    @Override
    public void onBackPressed() {
    	
    	Intent intent = new Intent(this, MainMenu.class);
    	startActivity(intent);
    	finish();
    }

}
