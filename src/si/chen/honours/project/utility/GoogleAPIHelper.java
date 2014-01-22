package si.chen.honours.project.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/** Helper Class which uses JSONParser class to get JSON response from: 
 * Google Places API URL
 * Google Directions API URL
 *  **/
public class GoogleAPIHelper {

	// Google API Key
	private static final String API_KEY = "AIzaSyB_Lb3cBA2ex0x0BxYpc0YfSwXLXNZConI";
	
	// Google Nearby Places search URL
	private String NEARBY_PLACE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
	// Nearby Place details search using reference id
	private String NEARBY_PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?reference=";
	
	// Google Directions URL
	private String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=";
	
	
	private double mLatitude;
	private double mLongitude;
	private double mRadius;
	private String mTypes;
	private String mReference;
	
	private double originLatitude;
	private double originLongitude;
	private double destinationLatitude;
	private double destinationLongitude;
	private String transportMode;
	
	JSONParser jParser = new JSONParser();
	
	
	// Constructor for Nearby Places Search
	public GoogleAPIHelper(double latitude, double longitude, double radius, String types) {
		
		this.mLatitude = latitude;
		this.mLongitude = longitude;
		this.mRadius = radius;
		this.mTypes = types;
	}
	
	// Constructor for requesting additional information of a nearby place using reference id
	public GoogleAPIHelper(String reference) {
		
		this.mReference = reference;
	}
	
	// Constructor for Google Directions API
	public GoogleAPIHelper(double origin_latitude, double origin_longitude, double destination_latitude, double destination_longitude, String mode) {
		
		this.originLatitude = origin_latitude;
		this.originLongitude = origin_longitude;
		this.destinationLatitude = destination_latitude;
		this.destinationLongitude = destination_longitude;
		this.transportMode = mode;
		
	}
	
	
	// Returns a JSON object (JSON response from Google Places URL)
	public JSONObject getNearbyPlacesResponse() {
			
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
					+ "&types="
					+ URLEncoder.encode(mTypes, "UTF-8")
					+ "&sensor="
					+ URLEncoder.encode("false", "UTF-8")
					+ "&key="
					+ URLEncoder.encode(API_KEY, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=56.4644033,-2.9925105&radius=1000.0&types=airport|amusement_park|aquarium|art_gallery|atm|bakery|bar|cafe|clothing_store|convenience_store|establishment|food|grocery_or_supermarket|movie_theater|museum|night_club|park|restaurant|shopping_mall|zoo&sensor=false&key=AIzaSyB_Lb3cBA2ex0x0BxYpc0YfSwXLXNZConI
		Log.i("NEARBY_PLACE_URL", NEARBY_PLACE_URL);
		return jParser.getJSONFromURL(NEARBY_PLACE_URL);
	} 

	// Returns a JSON object (JSON response from Google Place Details URL)
	public JSONObject getNearbyPlaceDetailsResponse() {
		
		NEARBY_PLACE_DETAILS_URL = NEARBY_PLACE_DETAILS_URL
				+ mReference
				+ "&sensor=false"
				+ "&key="
				+ API_KEY;
		
		
		//https://maps.googleapis.com/maps/api/place/details/json?reference=&sensor=false&key=AIzaSyB_Lb3cBA2ex0x0BxYpc0YfSwXLXNZConI
		Log.i("NEARBY_PLACE_DETAILS_URL", NEARBY_PLACE_DETAILS_URL);
		return jParser.getJSONFromURL(NEARBY_PLACE_DETAILS_URL);
	}
	
	// Returns a JSON object (JSON response from Google Directions URL)
	public JSONObject getDirectionsResponse() {
		
		String origin_lat = String.valueOf(originLatitude);
		String origin_lng = String.valueOf(originLongitude);
		String dest_lat = String.valueOf(destinationLatitude);
		String dest_lng = String.valueOf(destinationLongitude);
		
		try {
			DIRECTIONS_URL = DIRECTIONS_URL 
					+ URLEncoder.encode(origin_lat, "UTF-8")
					+ ","
					+ URLEncoder.encode(origin_lng, "UTF-8")
					+ "&destination="
					+ URLEncoder.encode(dest_lat, "UTF-8")
					+ ","
					+ URLEncoder.encode(dest_lng, "UTF-8")
					+ "&sensor="
					+ URLEncoder.encode("false", "UTF-8")
					+ "&mode="
					+ URLEncoder.encode(transportMode, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("DIRECTIONS_URL", DIRECTIONS_URL);
		return jParser.getJSONFromURL(DIRECTIONS_URL);
		
	}
	
	// Decode Polyline points obtained from Google Directions API
    public List<LatLng> decodePolyline(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}



