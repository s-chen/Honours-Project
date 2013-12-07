package com.basewarp.basewarp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.text.GetChars;
import android.util.Log;

import com.basewarp.basewarp.bubbledata.BubbleModel;
//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.util.Constants.Radius;

public class Algorithm {
	
	private final static long _72HRS = 259200000;
	private final static long _24HRS = 86400000;
	
	public static long getImportance(BubbleModel bubble) {
		
		long rating = 0;
		
		long numOpens = bubble.getNumOpens();
		long numRatings = bubble.getNumRatings();
		long cumulativeScore = bubble.getCumulativeScore();
		long timeScale = 10;
		long author = 1;
		long sponsored = 1;
		long proximity = 1;
		long relationship = 1; // default value; TODO: implement notion of a "friend"
		long target = 1; // default value; TODO: implement
		long numShares = 0; // dafault value; TODO: implement
		
		// Check timescale of bubble
		long timeDiff = bubble.getEndDatetime().getTime() - bubble.getStartDatetime().getTime();
		if(timeDiff > _72HRS) {
			timeScale = 1;
		} else if (timeDiff > _24HRS) {
			timeScale = 5;
		} // else timeScale is < 24HRS  =  10;
		
		// Check whether it's my bubble
		/*if(bubble.getUserId() == LoginManager.getUID()) {
			relationship = 10;
			author = 10;
		}*/
		
		// Check which radius the bubble has
		if(bubble.getRadius() == Radius.RIGHTHERE.getRadiusInMeters()) {
			proximity = 10;
		} else if(bubble.getRadius() == Radius.CLOSE.getRadiusInMeters()) {
			proximity = 5;
		} // else proximity = 1;
		
		// Check whether the bubble has been sponsored
		if(bubble.isSponsored()) sponsored = 10;
		
		//   i = ((Cs x [Rf + Tf + Pf]) + ((B*1000) + U))/10000
		
		long boost = timeScale + author + sponsored;
		long usage = numOpens + numRatings + numShares;
		
		rating = (cumulativeScore * (relationship + target + proximity))*10000 + (boost*1000) + usage;
		return rating;
	}
	
	public static void sortBubbles (ArrayList<BubbleModel> data) {
		
//		for(BubbleModel bm : data) {
//			bm.setImportance(getImportance(bm));
//		}
		
		Collections.sort(data, new Comparator<BubbleModel>() {
		//	@Override
			public int compare(BubbleModel a, BubbleModel b) {
				return Long.valueOf(b.getSort()).compareTo(Long.valueOf(a.getSort()));
			}
			
		});
	}
	
	public static void sortBubblesByDistance(ArrayList<BubbleModel> data) {
		Collections.sort(data, new Comparator<BubbleModel>() {
			//@Override
			public int compare(BubbleModel a, BubbleModel b) {
				Long distA = new Long((long)a.getDistance());
				Long distB = new Long((long)b.getDistance());
				return distB.compareTo(distA);
			}
			
		});
	}

}
