package si.chen.honours.project.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

/** Helper Class which uses Google Places to perform search on nearby points of interest surrounding user **/
public class NearbyPlaces {

	// Google API Key
	private static final String API_KEY = "AIzaSyCItFhQxomjvp4Ns-p81E2Xx7uIMGr2ULw";
	// Google Nearby Places search URL
	private String NEARBY_PLACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
	
	private double mLatitude;
	private double mLongitude;
	private double mRadius;
	private String mTypes;
	JSONParser jParser = new JSONParser();
	

	// Constructor for Nearby Place Search
	public NearbyPlaces(double latitude, double longitude, double radius, String types) {
		
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mTypes = types;
	}
	
	// Returns a JSON object (JSON response from Google Places URL)
	public JSONObject getNearbyPlaceResponse() {
			
		String user_lat = String.valueOf(mLatitude);
		String user_lng = String.valueOf(mLongitude);
		String radius = String.valueOf(mRadius);
	
		try {
			 NEARBY_PLACE_URL = NEARBY_PLACE_URL 
					+ URLEncoder.encode(user_lat, "UTF-8")
					+ ","
					+ URLEncoder.encode(user_lng, "UTF-8")
					+ "&radius="
					+ URLEncoder.encode(radius, "UTF-8")
					+ "&sensor="
					+ URLEncoder.encode("false", "UTF-8")
					+ "&types="
					+ URLEncoder.encode(mTypes, "UTF-8")
					+ "&key="
					+ URLEncoder.encode(API_KEY, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
				
			//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=56.4644033,-2.9925105&radius=1000.0&types=airport|amusement_park|aquarium|art_gallery|atm|bakery|bar|cafe|clothing_store|convenience_store|establishment|food|grocery_or_supermarket|movie_theater|museum|night_club|park|restaurant|shopping_mall|zoo&sensor=false&key=AIzaSyCItFhQxomjvp4Ns-p81E2Xx7uIMGr2ULw
		
		
		return jParser.getJSONFromURL(NEARBY_PLACE_URL);
	} 
}


