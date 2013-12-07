package com.basewarp.basewarp.services;

import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;

public class UserBubbles extends BubbleListService {
	
	int uid = 0;
	
	public UserBubbles(int bubbleNum, int UID) {
		super(bubbleNum);
		uid = UID;
		type = BubbleListTypes.USERBUBBLES;
		url = Constants.servicesBaseURL + "myBubbles.php";
	}
	
}