package com.basewarp.basewarp.services;

import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;

public class MyBubbles extends BubbleListService {
	
	public MyBubbles(int bubbleNum) {
		super(bubbleNum);
		type = BubbleListTypes.MYBUBBLES;
		url = Constants.servicesBaseURL + "myBubbles.php";
	}
	
}