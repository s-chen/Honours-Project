package com.basewarp.basewarp.services;

import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
import com.basewarp.basewarp.util.Constants.Radius;


public class Closest extends BubbleListService {
	
	public Closest(int bubbleNum) {
		super(bubbleNum);
		radius = Radius.CLOSEST.getRadiusInMeters();
		type = BubbleListTypes.CLOSEST;
		updateLocation();
		url = Constants.servicesBaseURL + "closest.php";
	}
	
}