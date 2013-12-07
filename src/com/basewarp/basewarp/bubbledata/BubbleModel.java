package com.basewarp.basewarp.bubbledata;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//import org.ocpsoft.prettytime.PrettyTime;

import android.location.Address;
import android.util.Log;

import com.google.gson.annotations.SerializedName;


public class BubbleModel implements Serializable{
	private static final long serialVersionUID = 8073701625701332707L;
	@SerializedName("id") private int bubbleId;
	@SerializedName("uuid") private String bubbleUUID;
	@SerializedName("type_id") private int typeId;
	@SerializedName("user_id") private int userId;
	@SerializedName("location_id") private int locationId;
	@SerializedName("description") private String description;
	@SerializedName("content_url") private String contentUrl; // Used to store the final url of the bubble's content, parsed from web service json (not set in app)
	private String imageContentPath; // Used to store the path of the bubble's image content on the device 
	private String webContentPath; // Used to store the url for web content
	@SerializedName("latitude")	private double latitude;
	@SerializedName("longitude")	private double longitude;
	@SerializedName("radius") private int radius;
	@SerializedName("start_datetime") private	 Date startDatetime;
	@SerializedName("end_datetime") private Date endDatetime;
	@SerializedName("added_datetime") private Date addedDatetime; // Used to store when the bubble was added to the server, parsed from json (not set in app)
	@SerializedName("num_opens") private int numOpens;
	@SerializedName("distance") private Double distance;
	@SerializedName("forename") private String forename;
	@SerializedName("is_sponsored") private int sponsored;
	@SerializedName("num_ratings") private int numRatings;
	@SerializedName("has_profile_image") private int hasProfileImage;
	@SerializedName("cumulative_score") private int cumulativeScore;
	@SerializedName("street_address") private String streetAddress;
	@SerializedName("city") private String city;
	@SerializedName("country") private String country;
	@SerializedName("user_rating") private int userRating;
	@SerializedName("sort") private String sort;
	@SerializedName("time_diff_seconds") private int timeDiffSeconds;
	@SerializedName("diff_to_utc") private int diffToUTC;
	@SerializedName("day_light_saving") private int dayLightSaving;
	@SerializedName("is_pinned") private int pinned;
	@SerializedName("is_warpable") private int isWarpable;
	
	public int getIsWarpable() {
		return isWarpable;
	}

	public void setIsWarpable(int warpable) {
		this.isWarpable = warpable;
	}

	public int getDiffToUTC() {
		return diffToUTC;
	}

	public void setDiffToUTC(int diffToUTC) {
		this.diffToUTC = diffToUTC;
	}

	public int getDayLightSaving() {
		return dayLightSaving;
	}

	public void setDayLightSaving(int dayLightSaving) {
		this.dayLightSaving = dayLightSaving;
	}
	
	public String getBubbleUUID() {
		return bubbleUUID;
	}

	public void setBubbleUUID(String bubbleUUID) {
		this.bubbleUUID = bubbleUUID;
	}

	
	public BubbleModel(){}
	
	public BubbleModel(HashMap<String,Object> data) {
		setBubbleId((String)data.get("id"));
		setBubbleUUID((String)data.get("uuid") == null ? "" : (String)data.get("uuid"));
		setTypeId((String)data.get("type_id"));
		setUserId((String)data.get("user_id"));
		setLocationId((String)data.get("location_id"));
		setDescription((String)data.get("description"));
		setContentUrl((String)data.get("content_url"));
		setLatitude((String)data.get("latitude"));
		setLongitude((String)data.get("longitude"));
		setRadius((String)data.get("radius"));
		setStartDatetime((String)data.get("start_datetime"));
		setEndDatetime((String)data.get("end_datetime"));
		setAddedDatetime((String)data.get("added_datetime"));
		setNumOpens((String)data.get("num_opens"));
		setDistance((String)data.get("distance"));
		setForename((String)data.get("forename"));
		setSponsored(Integer.parseInt((String)data.get("is_sponsored") == null ? "0" : (String)data.get("is_sponsored")));
		setPinned(Integer.parseInt((String)data.get("is_pinned") == null ? "0" : (String)data.get("is_pinned")));
		setNumRatings(Integer.parseInt((String)data.get("num_ratings") == null ? "0" : (String)data.get("num_ratings")));
		setHasProfileImage(Integer.parseInt((String)data.get("has_profile_image") == null ? "0" : (String)data.get("has_profile_image")));
		setCumulativeScore(Integer.parseInt((String)data.get("cumulative_score") == null ? "0" : (String)data.get("cumulative_score")));
		setStreetAddress((String)data.get("street_address"));
		setCity("");
		setCountry("");
		setUserRating(Integer.parseInt((String)data.get("user_rating") == null ? "0" : (String)data.get("user_rating")));
		setSort((String)data.get("sort"));
		setTimeDiffSeconds((String)data.get("time_diff_seconds"));
		setDayLightSaving((Integer)data.get("day_light_saving") == null ? 0 : (Integer)data.get("day_light_saving"));
		setDiffToUTC((Integer)data.get("diff_to_utc") == null ? 0 : (Integer)data.get("diff_to_utc"));
		setIsWarpable(data.get("is_warpable") == null ? 0 : Integer.parseInt((String)data.get("is_warpable")));
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	public void setAddress(Address address) {
		if (address != null) {
			if (address.getMaxAddressLineIndex() > 0) {
				streetAddress = address.getAddressLine(0);
				city = address.getAddressLine(1);
				country = address.getAddressLine(2);
			} else {
				streetAddress = address.getFeatureName();
				city = address.getLocality();
				country = address.getCountryName();
			}
		}
	}
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public int getUserRating() {
		return userRating;
	}

	public void setUserRating(int userRating) {
		this.userRating = userRating;
	}

	
	public boolean hasProfileImage() {
		return hasProfileImage == 1;
	}

	public void setHasProfileImage(int hasProfileImage) {
		this.hasProfileImage = hasProfileImage;
	}
	
	public boolean isSponsored() {
		if(sponsored == 1) return true;
		else return false;
	}
	
	public void setSponsored(int sponsored) {
		this.sponsored = sponsored;
	}

	public boolean isPinned() {
		if(pinned == 1) return true;
		else return false;
	}

	public void setPinned(int pinned) {
		this.pinned = pinned;
	}

	
	public int getNumRatings() {
		return numRatings;
	}

	public void setNumRatings(int numRatings) {
		this.numRatings = numRatings;
	}

	public int getCumulativeScore() {
		return cumulativeScore;
	}

	public void setCumulativeScore(int cumulativeScore) {
		this.cumulativeScore = cumulativeScore;
	}
	
	public String getUserForename() {
		return forename;
	}

	public void setUserForename(String forename) {
		this.forename = forename;
	}
	
	public void setBubbleId(int id) {
		this.bubbleId = id;
	}

	public void setBubbleId(String id) {
		this.bubbleId = Integer.parseInt(id);
	}

	public int getBubbleId() {
		return this.bubbleId;
	}

	public void setTypeId(String type_id) {
		this.typeId = Integer.parseInt(type_id);
	}
	
	public void setTypeId(int type_id) {
		this.typeId = type_id;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setLocationId(String location_id) {
		this.locationId = Integer.parseInt(location_id);
	}

	public int getLocationId() {
		return this.locationId;
	}

	public void setUserId(String user_id) {
		this.userId = Integer.parseInt(user_id);
	}
	
	public void setUserId(int user_id) {
		this.userId = user_id;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setContentUrl(String content_url) {
		this.contentUrl = content_url;
	}

	public String getContentUrl() {
		return this.contentUrl;
	}

	public void setImageContentPath(String imageContentPath) {
		this.imageContentPath = imageContentPath;
	}

	public String getImageContentPath() {
		return this.imageContentPath;
	}
	
	public void setWebContentPath(String webContentPath) {
		this.webContentPath = webContentPath;
	}

	public String getWebContentPath() {
		return this.webContentPath;
	}

	public void setLatitude (String latitude) {
		this.latitude = Double.parseDouble(latitude);
	}
	
	public void setLatitude (Double latitude) {
		this.latitude = latitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLongitude (String longitude) {
		this.longitude = Double.parseDouble(longitude);
	}
	
	public void setLongitude (Double longitude) {
		this.longitude = longitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setRadius (String radius) {
		this.radius = Integer.parseInt(radius);
	}

	public int getRadius() {
		return this.radius;
	}

	public void setNumOpens (String num_opens) {
		this.numOpens = Integer.parseInt(num_opens);
	}

	public int getNumOpens() {
		return this.numOpens;
	}

	public void setStartDatetime (String start_datetime) {
		try {
			this.startDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(start_datetime);
		} catch (ParseException e) {
			Log.w("BubbleModel", "Failed to parse start date");
			e.printStackTrace();
		}
	}
	
	public void setStartDatetime(Date startDate) {
		this.startDatetime = startDate;
	}

	public Date getStartDatetime() {
		return this.startDatetime;
	}

	public void setEndDatetime (String end_datetime) {
		try {
			this.endDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(end_datetime);
		} catch (ParseException e) {
			Log.w("BubbleModel", "Failed to parse end date");
			e.printStackTrace();
		}
	}
	
	public void setEndDatetime(Date endDate) {
		this.endDatetime = endDate;
	}

	public Date getEndDatetime() {
		return this.endDatetime;
	}

	public void setAddedDatetime (String added_datetime) {
		try {
			this.addedDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(added_datetime);
		} catch (ParseException e) {
			Log.w("BubbleModel", "Failed to parse added date");
			e.printStackTrace();
		}
	}

	public Date getAddedDatetime() {
		return this.addedDatetime;
	}
	
	public void setDistance (String distance) {
		if(distance == null) this.distance = null;
		else this.distance = Double.parseDouble(distance);
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public Double getDistanceObject() {
		return this.distance;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

/*	public String getTimeDiff() {
		if (timeDiffSeconds<0) return "Just now";

		PrettyTime p = new PrettyTime();
		Calendar calendar = Calendar.getInstance(Locale.ENGLISH); // gets a calendar using the default time zone and locale.
		calendar.add(Calendar.SECOND, -1 * timeDiffSeconds);
		Date addedTime = calendar.getTime();

		return p.format(addedTime);
	}*/

	public void setTimeDiffSeconds(String timeDiffSeconds) {
		this.timeDiffSeconds = Integer.valueOf(timeDiffSeconds);
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}