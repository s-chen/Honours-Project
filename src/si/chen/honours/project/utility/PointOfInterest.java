package si.chen.honours.project.utility;

/** Model Object provides getter/setter methods for storing and retrieving Point of Interest data **/
public class PointOfInterest implements Comparable<PointOfInterest> {

	private String id;
	private String name;
	private String services;
	private float latitude;
	private float longitude;
	private String content_url;
	
	// Empty constructor
	public PointOfInterest() {
		
	}
	
	// Constructor with all data
	public PointOfInterest(String id, String name, String services, float latitude, float longitude, String content_url) {
		
		this.id = id;
		this.name = name;
		this.services = services;
		this.latitude = latitude;
		this.longitude = longitude;
		this.content_url = content_url;
	}
	
	// Constructor with name and services
	public PointOfInterest(String name, String services) {
		
		this.name = name;
		this.services = services;
	}
	
	// Constructor includes PointOfInterest id to identify POI data
	public PointOfInterest(String id, String name, String services) {
		this.id = id;
		this.name = name;
		this.services = services;
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getServices() {
		return this.services;
	}
	
	public void setServices(String services) {
		this.services = services;
	}
	
	public float getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	public float getLongitude() {
		return this.longitude;
	}
	
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public String getContentURL() {
		return this.content_url;
	}
	
	public void setContentURL(String content_url) {
		this.content_url = content_url;
	}
	
	// Formats the look when displayed in ListView
	public String toString() {
		return this.id + ".  " + this.name + " - " + "(" + this.services + ")";
	}
	
	// Comparator to compare and sort in alphabetical order
	public int compareTo(PointOfInterest poi) {
		
		int result = this.name.compareToIgnoreCase(poi.name);
		
		if (result != 0) {
			return result;
		} else {
			return new String(this.name).compareTo(new String(poi.name));
		}
	}
}

