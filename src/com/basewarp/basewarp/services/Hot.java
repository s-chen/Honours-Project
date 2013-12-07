package com.basewarp.basewarp.services;

import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
import com.basewarp.basewarp.util.Constants.Radius;


public class Hot extends BubbleListService {
	
	public Hot(int bubbleNum) {
		super(bubbleNum);
		radius = Radius.CLOSEST.getRadiusInMeters();
		type = BubbleListTypes.HOT;
		url = Constants.servicesBaseURL + "hot.php";
	}
	
}