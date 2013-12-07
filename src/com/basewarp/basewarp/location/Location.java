package com.basewarp.basewarp.location;

import java.io.IOException;
import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Serializable {
	
	private static final long serialVersionUID = -6515349293592700475L;
	private String name;
	private double lat;
	private double lon;
	
	public Location(String name, double lat, double lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	public String getName() {
		return name;
	}

	public double[] getLatLong() {
		return new double[] {lat,lon};
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lon;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public boolean equals(Object other) {
		if(other.getClass() != Location.class) return false;
		Location otherLoc = (Location) other;
		return	this.name.equals(otherLoc.getName()) &&
				this.lat == otherLoc.getLatitude() &&
				this.lon == otherLoc.getLongitude();
	}
	
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	out.writeInt(name.length());
    	out.writeChars(name);
    	out.writeDouble(lat);
    	out.writeDouble(lon);
    }
    
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	int nameLength = in.readInt();
    	char[] name = new char[nameLength];
    	for(int i = 0; i < nameLength; i++) {
    		name[i] = in.readChar();
    	}
    	this.name = new String(name);
    	this.lat = in.readDouble();
    	this.lon = in.readDouble();
    }
}
