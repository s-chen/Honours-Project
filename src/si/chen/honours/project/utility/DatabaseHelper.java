package si.chen.honours.project.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** SQLite Database Helper class to integrate
 * Edinburgh Point of Interest database with Android  **/
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	// Android System Path for database
	private static String DATABASE_PATH = "";
	private static final String DATABASE_NAME = "edinburghDatabase";
	public static final String TABLE_EDINBURGH = "edinburgh";
	private SQLiteDatabase mDB;
	private final Context mContext;
	
		
	// Constructor
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		/** Checks and set appropriate paths for the Android system database
		 depending on Android version **/
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
		} else {
			DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		} 
		
		this.mContext = context;
	}
	
	
	/** Create empty database on Android system and copy database from Assets folder **/
	public void createDatabase() throws IOException {
		
		boolean dbExist = checkDatabaseExist();
		
		if (dbExist) {
			// Do nothing
			Log.i("DB_EXISTS", "Database already exists");
		} else {
			
			this.getReadableDatabase();
			this.close();

			try {
				copyDatabase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}	
	}
	
	/** Check whether database already exists to prevent re-copying database file
	  each time the app is opened **/
	private boolean checkDatabaseExist() {
		
		File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
		return dbFile.exists();
	}
	
	
	private void copyDatabase() throws IOException {
		
		// Open local database as input stream
		InputStream inStream = mContext.getAssets().open(DATABASE_NAME);
		
		// Path to newly created empty database
		String path = DATABASE_PATH + DATABASE_NAME;
		
		// Open empty database as output stream
		OutputStream outStream = new FileOutputStream(path);
		
		// Transfer data from input to output
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inStream.read(buffer)) > 0) {
			outStream.write(buffer, 0, length);
		}
		
		// Close input/output streams
		outStream.flush();
		outStream.close();
		inStream.close();
		
		Log.i("DB_COPIED", "Copied database file to Android system");
	}
	
	// Open database for querying
	public boolean openDatabase() throws IOException {
		
		String path = DATABASE_PATH + DATABASE_NAME;
		mDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		
		return mDB != null;
	}
	
	@Override
	public synchronized void close() {
		
		if (mDB != null) {
			mDB.close();
		}
		super.close();
	}
		
	// Get a List of restaurant data from the result of a SQL query
	public List<PointOfInterest> getRestaurantData() {
		
		List<PointOfInterest> restaurantList = new ArrayList<PointOfInterest>(); 
		
		// Retrieve all restaurant or fast food type services
		String sqlQuery = "SELECT * FROM edinburgh "
				+ "WHERE services = 'restaurant' OR services = 'fast_food'";
		
		// Open database for querying
		try {
			openDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = mDB.rawQuery(sqlQuery, null);
		
		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
				do {
					PointOfInterest poi = new PointOfInterest();
					poi.setID(Integer.parseInt(cursor.getString(0)));
					poi.setName(cursor.getString(1));
					poi.setServices(cursor.getString(2));
					poi.setLatitude(cursor.getFloat(3));
					poi.setLongitude(cursor.getFloat(4));
					poi.setContentURL(cursor.getString(5));
				
					// Add restaurant data to list
					restaurantList.add(poi);
					
				} while (cursor.moveToNext());
		}
		
		return restaurantList;	
	}
	
	
	// Get a List of drinks data from the result of a SQL query
	public List<PointOfInterest> getDrinksData() {
		
		List<PointOfInterest> drinksList = new ArrayList<PointOfInterest>(); 
		
		// Retrieve all bar/cafe/pub/nightclub type services
		String sqlQuery = "SELECT * FROM edinburgh "
				+ "WHERE services = 'bar' OR services = 'cafe' OR services = 'pub' OR services = 'nightclub'";
		
		// Open database for querying
		try {
			openDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = mDB.rawQuery(sqlQuery, null);
		
		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
				do {
					PointOfInterest poi = new PointOfInterest();
					poi.setID(Integer.parseInt(cursor.getString(0)));
					poi.setName(cursor.getString(1));
					poi.setServices(cursor.getString(2));
					poi.setLatitude(cursor.getFloat(3));
					poi.setLongitude(cursor.getFloat(4));
					poi.setContentURL(cursor.getString(5));
				
					// Add restaurant data to list
					drinksList.add(poi);
					
				} while (cursor.moveToNext());
		}
		
		return drinksList;	
	}
	
	
	// Get a List of attractions data from the result of a SQL query
	public List<PointOfInterest> getAttractionsData() {
		
		List<PointOfInterest> attractionsList = new ArrayList<PointOfInterest>(); 
		
		// Retrieve all tourist attraction types
		String sqlQuery = "SELECT * FROM edinburgh "
				+ "WHERE services = 'archaeological_site' OR services = 'artwork' OR services = 'attraction' "
				+ "OR services = 'battlefield' OR services = 'camp_site' OR services = 'cannon' OR services = 'caravan_site' "
				+ "OR services = 'castle' OR services = 'gallery' OR services = 'garden' OR services = 'golf_course' "
				+ "OR services = 'hill' OR services = 'information' OR services = 'memorial' OR services = 'monument' "
				+ "OR services = 'museum' OR services = 'nature_reserve' OR services = 'park' OR services = 'ruins' "
				+ "OR services = 'theatre' OR services = 'viewpoint' OR services = 'zoo'";
		
		// Open database for querying
		try {
			openDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = mDB.rawQuery(sqlQuery, null);
		
		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
				do {
					PointOfInterest poi = new PointOfInterest();
					poi.setID(Integer.parseInt(cursor.getString(0)));
					poi.setName(cursor.getString(1));
					poi.setServices(cursor.getString(2));
					poi.setLatitude(cursor.getFloat(3));
					poi.setLongitude(cursor.getFloat(4));
					poi.setContentURL(cursor.getString(5));
				
					// Add attractions data to list
					attractionsList.add(poi);
					
				} while (cursor.moveToNext());
		}
		
		return attractionsList;	
	}
	
	
	// Get a List of accommmodation data from the result of a SQL query
	public List<PointOfInterest> getAccommodationData() {
			
		List<PointOfInterest> accommodationList = new ArrayList<PointOfInterest>(); 
			
		// Retrieve all accommodation types
		String sqlQuery = "SELECT * FROM edinburgh "
				+ "WHERE services = 'guest_house' OR services = 'hostel' OR services = 'hotel'";
			
		// Open database for querying
		try {
			openDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		Cursor cursor = mDB.rawQuery(sqlQuery, null);
			
		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
				do {
					PointOfInterest poi = new PointOfInterest();
					poi.setID(Integer.parseInt(cursor.getString(0)));
					poi.setName(cursor.getString(1));
					poi.setServices(cursor.getString(2));
					poi.setLatitude(cursor.getFloat(3));
					poi.setLongitude(cursor.getFloat(4));
					poi.setContentURL(cursor.getString(5));
					
					// Add attractions data to list
					accommodationList.add(poi);
						
				} while (cursor.moveToNext());
		}
			
		return accommodationList;	
	}
	
	// Get a List of shops data from the result of a SQL query
	public List<PointOfInterest> getShopsData() {
			
		List<PointOfInterest> shopsList = new ArrayList<PointOfInterest>(); 
			
		// Retrieve all accommodation types
		String sqlQuery = "SELECT * FROM edinburgh "
				+ "WHERE services = 'alcohol' OR services = 'beverages' OR services = 'deli' "
				+ "OR services = 'farm' OR services = 'department_store' OR services = 'general' "
				+ "OR services = 'mall' OR services = 'supermarket' OR services = 'clothes' "
				+ "OR services = 'jewelry' OR services = 'beauty' OR services = 'electronics' "
				+ "OR services = 'outdoor' OR services = 'sports' OR services = 'art' OR services = 'music' "
				+ "OR services = 'gift' OR services = 'ticket'";
			
		// Open database for querying
		try {
			openDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		Cursor cursor = mDB.rawQuery(sqlQuery, null);
			
		// Loop through all rows and add to list
		if (cursor.moveToFirst()) {
				do {
					PointOfInterest poi = new PointOfInterest();
					poi.setID(Integer.parseInt(cursor.getString(0)));
					poi.setName(cursor.getString(1));
					poi.setServices(cursor.getString(2));
					poi.setLatitude(cursor.getFloat(3));
					poi.setLongitude(cursor.getFloat(4));
					poi.setContentURL(cursor.getString(5));
					
					// Add shops data to list
					shopsList.add(poi);
						
				} while (cursor.moveToNext());
		}
			
		return shopsList;	
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	 
	}
}
