package com.basewarp.basewarp.services;

import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;

public class Starred extends BubbleListService {
	
	public Starred(int bubbleNum) {
		super(bubbleNum);
		type = BubbleListTypes.STARRED;
		url = Constants.servicesBaseURL + "starred.php";
	}
	
}