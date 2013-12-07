package com.basewarp.basewarp.bubbledisplay;


import java.util.ArrayList;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import android.support.v7.app.ActionBarActivity;

//import com.actionbarsherlock.app.SherlockFragment;
//import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.basewarp.basewarp.bubbledata.BubbleListManager;
import com.basewarp.basewarp.bubbledata.BubbleModel;
import com.basewarp.basewarp.location.GPSManager;
//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.services.AddUserRating;
import com.basewarp.basewarp.services.ServiceListener;
//import com.basewarp.basewarp.ui.BubbleDetailActivity;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
//import com.basewarp.basewarp.R;

public class BubbleListFragment extends ActionBarActivity implements ServiceListener {

	BubbleListAdapter mBubbleListAdapter;
	BubbleListTypes type;
	//SherlockFragmentActivity mContext;
	ActionBarActivity mContext;
	
	int offset = 0;
	boolean moreBubblesAvailable = true;

	static BubbleListFragment newInstance(int num) {
		return new BubbleListFragment();
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*type = BubbleListTypes.values()[getArguments().getInt("listtype")];
		if(getArguments().containsKey("clearBubbles") && getArguments().getBoolean("clearBubbles")) {
			BubbleListManager.clearBubbleList(type);
		}*/
		mBubbleListAdapter = new BubbleListAdapter(this.getApplicationContext(), type, new ArrayList<BubbleModel>(), true);
		mContext = (ActionBarActivity) this.mContext;
	}


	//@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContext.setSupportProgressBarIndeterminateVisibility(false);

	/*	if(!LoginManager.isLoggedIn() && (type == BubbleListTypes.STARRED || type == BubbleListTypes.MYBUBBLES)) {
			View v = new View(this.getActivity());
			v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			Toast.makeText(this.getActivity(), "Please log in to view these bubbles", Toast.LENGTH_SHORT).show();
			return v;
		}*/

		ArrayList<BubbleModel> bubbles = BubbleListManager.getCurrentBubbles(type,this.mContext);
		if (bubbles != null && bubbles.size() > 0) {
			mBubbleListAdapter.clearAndAdd(bubbles);
			if(bubbles.size() % 20 == 0) { // number of bubbles multiple of 20, so we know there are more available
				mBubbleListAdapter.setMoreBubblesAvailable(true);
			}
			mBubbleListAdapter.notifyDataSetChanged();
		} else {
			offset = 0;
			mBubbleListAdapter.clear();
			mBubbleListAdapter.notifyDataSetChanged();
			mContext.setSupportProgressBarIndeterminateVisibility(true);
			BubbleListManager.updateBubbles(type, this, offset);
		}

		ListView lv = new ListView(this.getApplication());	

		lv.setAdapter(mBubbleListAdapter);
		lv.setDividerHeight(0);
		//lv.setBackgroundColor(getActivity().getResources().getColor(com.basewarp.basewarp.R.color.BUBBLE_LIST_BG));
		//lv.setCacheColorHint(getActivity().getResources().getColor(com.basewarp.basewarp.R.color.BUBBLE_LIST_BG));


		lv.setOnItemClickListener(new OnItemClickListener() {
			//@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				BubbleModel bm = mBubbleListAdapter.getItem(position);
				if(bm != null) {
				//	BubbleDetailActivity.setBubbleModel(bm);
				//	startActivity(new Intent(getActivity(),BubbleDetailActivity.class));
				}
			}
		});

		return lv;
	}

/*	private void submitHideBubble(BubbleModel bubble) {
		AddUserRating addUserRating = new AddUserRating();
		addUserRating.setBubbleId(Integer.toString(bubble.getBubbleId()));
		addUserRating.setUserId(Integer.toString(LoginManager.getUID()));
		addUserRating.setRatingTypeId(Integer.toString(1));
		addUserRating.setListener(this);
		addUserRating.execute();
	}*/

/*	public void onLoadMoreBubblesClicked (View view) {
		if(moreBubblesAvailable) {
			((Button)getActivity().findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_button)).setVisibility(View.GONE);
			((LinearLayout)getActivity().findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_loading_bar)).setVisibility(View.VISIBLE);
			BubbleListManager.updateBubbles(type, this, offset);
		} else {
			((Button)getActivity().findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_button)).setVisibility(View.GONE);
			((TextView)getActivity().findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_nomore)).setVisibility(View.VISIBLE);
		}
	}*/

	public void notifyDataSetChanged(ArrayList<BubbleModel> newBubbles) {
		mContext.setSupportProgressBarIndeterminateVisibility(false);
		
		int numBubbles = newBubbles != null ? newBubbles.size() : 0;
		
		offset = numBubbles;

		if(numBubbles % 20 > 0) moreBubblesAvailable = false;
		if(getApplication()!=null) { // In case we moved out of focus of the activity!
			Button b = ((Button)this.findViewById(R.id.button1));//R.id.com_basewarp_righthere_bubble_list_load_bubbles_button));
			LinearLayout ll = ((LinearLayout)this.findViewById(R.id.list));//R.id.com_basewarp_righthere_bubble_list_load_bubbles_loading_bar));
			TextView tv = ((TextView)this.findViewById(R.id.text1));//R.id.com_basewarp_righthere_bubble_list_load_bubbles_nomore));

			if(moreBubblesAvailable) {
				mBubbleListAdapter.setMoreBubblesAvailable(true);
				if(b != null) b.setVisibility(View.VISIBLE);
				if(ll != null) ll.setVisibility(View.GONE);
				if(tv != null) tv.setVisibility(View.GONE);
			} else {
				mBubbleListAdapter.setMoreBubblesAvailable(false);
				if(b != null) b.setVisibility(View.GONE);
				if(ll != null) ll.setVisibility(View.GONE);
				if(tv != null) tv.setVisibility(View.VISIBLE);
			}
		}
		if(mBubbleListAdapter != null) {
			mBubbleListAdapter.clearAndAdd(newBubbles);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle out) {
		// Workaround for bug on api < 11 devices
		out.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(out);
	}

	public void refresh() {
		mContext.setSupportProgressBarIndeterminateVisibility(true);
		offset = 0;
		moreBubblesAvailable = true;
		BubbleListManager.refreshBubbles(type, this);
	}

	public void handleTimeout() {
		Log.w("timeout","timeout");
		mContext.setSupportProgressBarIndeterminateVisibility(false);
		Toast.makeText(this.mContext, "The connection has timed out. Please check your internet connection, then tap the refresh button to retry.", Toast.LENGTH_LONG).show();
	}

	public void unknownError() {
		mContext.setSupportProgressBarIndeterminateVisibility(false);
		Toast.makeText(this.mContext, "Error", Toast.LENGTH_LONG).show();
	}

	public void handleNoBubbles() {
		mContext.setSupportProgressBarIndeterminateVisibility(false);
		moreBubblesAvailable = false;
		mBubbleListAdapter.setMoreBubblesAvailable(false);
		mBubbleListAdapter.notifyDataSetChanged();
		Button b = ((Button)this.findViewById(R.id.button1));//R.id.com_basewarp_righthere_bubble_list_load_bubbles_button));
		LinearLayout ll = ((LinearLayout)this.findViewById(R.id.list));//R.id.com_basewarp_righthere_bubble_list_load_bubbles_loading_bar));
		TextView tv = ((TextView)this.findViewById(R.id.text1));//.com_basewarp_righthere_bubble_list_load_bubbles_nomore));
		if(b != null) b.setVisibility(View.GONE);
		if(ll != null) ll.setVisibility(View.GONE);
		if(tv != null) tv.setVisibility(View.VISIBLE);
		String toastText = "";
		if(type == BubbleListTypes.STARRED) {
			toastText = "Nothing here. You have not starred any bubbles.";
		} else if(type == BubbleListTypes.MYBUBBLES) {
			toastText = "Nothing here. You have not created any bubbles.";
		} else {
			toastText = "No bubbles found at your current location.";
			if(GPSManager.isLocationWarped()) toastText += " Warp to a different location or tap the refresh button to retry.";
			else toastText +=  " Tap the refresh button to retry.";
		}
		Toast.makeText(this.mContext, toastText, Toast.LENGTH_LONG).show();

	}

//	@Override
	public void beforeStart() {
		// TODO Auto-generated method stub

	}

//	@Override
	public void onComplete(Object data, StatusCode rc) {
		switch(rc) {
		case CONNECTION_TIMEOUT:
			handleTimeout();
			break;
		default:
			break;
		}
	}

}