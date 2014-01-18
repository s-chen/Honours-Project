package si.chen.honours.project.ui;

import java.util.Collections;
import java.util.List;

import si.chen.honours.project.R;
import si.chen.honours.project.utility.DatabaseHelper;
import si.chen.honours.project.utility.PointOfInterest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

// Display list of shops
public class Shops extends Activity {

	private List<PointOfInterest> shops_list;
	private int[] shops_id;
	private String[] shops_name;
	private String[] services;
	private double[] shops_latitude;
	private double[] shops_longitude;
	private String[] shops_url;
	private ArrayAdapter<PointOfInterest> shops_adapter;
	private ListView lv_shops;
	private PointOfInterest shops_data[];
	
	private EditText search_shops;
	private boolean is_filtered = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shops);
		
		// Set up action bar
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Shops");
		actionBar.setDisplayHomeAsUpEnabled(true);

		
		lv_shops = (ListView) findViewById(R.id.listView_shops);
		search_shops = (EditText) findViewById(R.id.editText_shops_search);
		
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		
		shops_list = dbHelper.getShopsData();
		
		// Uses Comparator from PointOfInterest class to sort shops
		Collections.sort(shops_list);

		
		// Initialise arrays for storing shops data
		shops_id = new int[shops_list.size()];
		shops_name = new String[shops_list.size()];
		services = new String[shops_list.size()];
		shops_latitude = new double[shops_list.size()];
		shops_longitude = new double[shops_list.size()];
		shops_url = new String[shops_list.size()];
		shops_data = new PointOfInterest[shops_list.size()];
		
		
		// Store shops data in arrays
		for (int i = 0; i < shops_list.size(); i++) {
			shops_id[i] = shops_list.get(i).getID();
			shops_name[i] = shops_list.get(i).getName();
			services[i] = shops_list.get(i).getServices();
			shops_latitude[i] = shops_list.get(i).getLatitude();
			shops_longitude[i] = shops_list.get(i).getLongitude();
			shops_url[i] = shops_list.get(i).getContentURL();
		}
				
		// Store custom PointOfInterest object (name, services)
		for (int i = 0; i < shops_list.size(); i++) {
			shops_data[i] = new PointOfInterest(i+1, shops_name[i], services[i]);
		}
			

		// Display shops name, service type in ListView
		Log.i("ShopsListView", "Adding shops data to list view.");
		shops_adapter = new ArrayAdapter<PointOfInterest>(this, R.layout.point_of_interest_list, R.id.list_item, shops_data);
		lv_shops.setAdapter(shops_adapter);
		

		// Detect text entered when user performs search
		search_shops.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				Shops.this.shops_adapter.getFilter().filter(cs);
				is_filtered = true;
			}
			
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub	
			}
			
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub							
			}
			
		});

		
		// Display more specific information about a particular shop
		lv_shops.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// Check whether ListView is filtered (user performs search)
				if (is_filtered) {
					
					Log.i("LIST_FILTERED", "ListView is filtered");
					
					// Get current List Item position
					String currentItem = lv_shops.getItemAtPosition(position).toString();
				
					// Update List Item position when ListView is filtered
					for (int updatedIndex = 0; updatedIndex < shops_data.length; updatedIndex++) {
						if (currentItem.equals(shops_data[updatedIndex].toString())) {
							position = updatedIndex;
							break;
						}
					}
				}
								
				Intent shopsIntent = new Intent(getApplicationContext(), ShopInfo.class);
				shopsIntent.putExtra("KEY_NAME", shops_name[position]);
				shopsIntent.putExtra("KEY_SERVICES", services[position]);
				shopsIntent.putExtra("KEY_LATITUDE", shops_latitude[position]);
				shopsIntent.putExtra("KEY_LONGITUDE", shops_longitude[position]);
				shopsIntent.putExtra("KEY_CONTENT_URL", shops_url[position]);
				shopsIntent.putExtra("KEY_SHOP_ITEM_POSITION", position);
				shopsIntent.putExtra("KEY_TYPE", "Shop");
						
				startActivity(shopsIntent);
				finish();
			}
					
		});
			
		// Close database
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shops, menu);
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
