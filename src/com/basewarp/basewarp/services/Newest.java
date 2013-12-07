package com.basewarp.basewarp.services;

import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
import com.basewarp.basewarp.util.Constants.Radius;

public class Newest extends BubbleListService {
	
	public Newest(int bubbleNum) {
		super(bubbleNum);
		radius = Radius.NEARBY.getRadiusInMeters();
		type = BubbleListTypes.NEWEST;
		url = Constants.servicesBaseURL + "newest.php";
	}
	
}