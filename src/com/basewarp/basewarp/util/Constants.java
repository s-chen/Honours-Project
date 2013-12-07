package com.basewarp.basewarp.util;

import android.os.Environment;

public class Constants {
	
	public static enum BubbleListTypes {
		CLOSEST,
		NEWEST,
		HOT,
		STARRED,
		MYBUBBLES,
		USERBUBBLES
//		RIGHTHERE,
//		NEARBY,
//		LATEST,
//		BUBBLES,
//		PLACES,
//		USERS,
//		IMPORTANT,
//		RECENT,
//		NOLOGIN,
	}
	
	public static enum Radius {
		RIGHTHERE(40),
		CLOSE(200),
		NEARBY(500),
		CLOSEST(1000);
		
		private int radiusInMeters;

		 private Radius(int r) {
			 radiusInMeters = r;
		 }

		 public int getRadiusInMeters() {
		   return radiusInMeters;
		 }
	}
		
	public static final int RESULT_LOAD_IMAGE = 1;
	public static final int RESULT_LOAD_WEBSITE = 2;
	public static final int RESULT_LOAD_PHOTO = 3;
	public static final int RESULT_GET_MAPS_LOCATION = 4;

	public static final String host = "http://dev.basewarp.com/";
	public static final String bubbleShareURL = "http://www.basewarp.com/services/bubble.php";
	public static final String servicesBaseURL = host + "services/";
	public static final String imagesBaseURL = host; 
	public static final String imageThumbnailServiceURL = servicesBaseURL + "thumb/thumb.php?src=";

	public static final int profilePicWidth = 150;
	public static final int profilePicHeight = 150;
	public static final int profilePicThumbnailWidth = 50;
	public static final int profilePicThumbnailHeight = 50;

	public static final int uploadImageMaxWidth = 640;
	public static final int uploadImageMaxHeight = 640;
	
	public static final String photoFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "basewarp/";
	public static final String photoFileName = "temp_photo.jpg";
	public static final String bubbleDataFileName = "bubble.data";
	public static final String imageCacheFileName = "imagecache.data";
	public static final String locationsFileName = "locations.data";
	public static final String webViewSnapshotFileName = "wvsnapshot.data";
	
}
