package com.basewarp.basewarp.bubbledisplay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//import org.ocpsoft.prettytime.PrettyTime;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basewarp.basewarp.bubbledata.BubbleListManager;
import com.basewarp.basewarp.bubbledata.BubbleModel;
import com.basewarp.basewarp.location.GPSListener;
import com.basewarp.basewarp.location.GPSManager;
//import com.basewarp.basewarp.login.LoginManager;
import com.basewarp.basewarp.services.AddUserRating;



import android.support.v4.app.FragmentActivity;


//import com.basewarp.basewarp.ui.MainActivity;
//import com.basewarp.basewarp.ui.MapFragment;
//import com.basewarp.basewarp.ui.WebViewActivity;
//import com.basewarp.basewarp.ui.MainActivity.FragmentTags;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.DownloadBubbleImageTask;
import com.basewarp.basewarp.util.DownloadProfileImageTask;
import com.basewarp.basewarp.util.Util;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
import com.google.android.gms.maps.SupportMapFragment;
//import com.basewarp.basewarp.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * BubbleListAdapter
 * 
 * @author Samuel
 * 
 *         Used by the parent view to get a view for each element in the list of
 *         bubbles This is where the bubble templates (.xml layouts for each
 *         bubble type) get filled with the bubble data we hold.
 */
public class BubbleListAdapter extends ArrayAdapter<BubbleModel> {

	private Context mContext;
	private ArrayList<BubbleModel> mData;
	private boolean addLoadMoreBubblesElement;
	private boolean moreBubblesAvailable;
	private BubbleListTypes bubbleListType;

	public BubbleListAdapter(Context context, BubbleListTypes type, ArrayList<BubbleModel> objects, boolean addLoadMoreBubblesElement) {
		super(context, android.R.layout.expandable_list_content, objects);
		this.mContext = context;
		this.bubbleListType = type;
		this.mData = objects;
		this.addLoadMoreBubblesElement = addLoadMoreBubblesElement;
	}

	public void remove(int position) {
		mData.remove(position);
	}

	/**
	 * Returns the BubbleModel when the bubble has been clicked in the list of bubbles
	 * @param position : the position in the list view
	 * @return a bubble model if the correct item in the list has been clicked or null if that item is not a bubble
	 */
	public BubbleModel getItem(int position) {
		if(position == 0 || (addLoadMoreBubblesElement && position >= getCount()-1)) {
			// Clicked on the map view or the load more bubbles button. Map view or button should override this the touch focus, so this shouldn't actually happen
			System.err.println("Item in BubbleListAdapter clicked which is not a bubble");
			return null;
		}

		// The offset is needed because the first element in the list is the map-view, instead of the first bubble
		return mData.get(position-1);
	}

	public void setMoreBubblesAvailable (boolean moreBubbles) {
		this.moreBubblesAvailable = moreBubbles;
	}

	public void clearAndAdd(ArrayList<BubbleModel> bubbles) {
		mData.clear();
		if(bubbles != null) {
			for(BubbleModel bubble : bubbles) {
				mData.add(bubble);
			}
		}
		notifyDataSetChanged();
	}

	public void insertItem(int position, BubbleModel bubble) {
		mData.add(position, bubble);
	}

	@Override
	public int getCount() {
		if(mData == null || mData.size() == 0) return 1;
		else if(addLoadMoreBubblesElement) return mData.size() +2;
		else return mData.size() +1;
	}

	//@Override
	//public View getView(int position, View convertView, ViewGroup parent) {

//		View row = convertView;
	//	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		/* If the location is warped and this is the first element of the list,
		 * display the google maps element with the current location */
	/*	if(position == 0) {
			//row = inflater.inflate(R.layout.template_list_map, parent, false);
			//TextView locationDescription = (TextView)row.findViewById(R.id.map_text);
			//TextView hereButtonText = (TextView)row.findViewById(R.id.here_button_label);
			//ImageView hereButtonImage = (ImageView)row.findViewById(R.id.here_button);

			String markerText = "Warped location";
			SupportMapFragment newFragment = new SupportMapFragment();
			//MapFragment newFragment = new MapFragment();
			if(GPSManager.isLocationWarped()) {
				//newFragment.animateOnStartup(new LatLng(GPSManager.getLocation().getLatitude(),GPSManager.getLocation().getLongitude()), 0, markerText, false, true);
				//locationDescription.setText("Warp location");
			}
			else {
				//newFragment.showOnlyOwnLocation();
				//locationDescription.setText("Current location");
				//hereButtonText.setText("Refresh");
				//hereButtonImage.setImageResource(R.drawable.navigation_refresh);
			}*/

			//((MainActivity)mContext).getSupportFragmentManager().beginTransaction()
			//.replace(row.findViewById(R.id.map_content_frame).getId(), newFragment, "notag")
			//.commit();
			//return row;
		//}

		// If this is the last element of the list, create the "load more bubbles" button:
		//if(position == mData.size()+1 && addLoadMoreBubblesElement)  {
			//row = inflater.inflate(R.layout.template_loading_bubbles, parent, false);
			//FrameLayout fl = ((FrameLayout)row.findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_frame));
			//fl.setFocusable(true);
			//fl.setFocusableInTouchMode(true);
			//fl.setClickable(true);
			//fl.setOnClickListener(new OnClickListener() {
				//@Override
				//public void onClick(View v) {}
			//});

			//if(!moreBubblesAvailable) {
				//Button b = ((Button)row.findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_button));
				//LinearLayout ll = ((LinearLayout)row.findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_loading_bar));
				//TextView tv = ((TextView)row.findViewById(R.id.com_basewarp_righthere_bubble_list_load_bubbles_nomore));

			//	b.setVisibility(View.GONE);
			//	ll.setVisibility(View.GONE);
			//	tv.setVisibility(View.VISIBLE);
			//}

			//return row;
	//	}

		// Otherwise continue on and create the layout for a bubble:

		// Get the bubble corresponding to this row
	//	final BubbleModel bubble = mData.get(position-1);

		// Check if the bubble is sponsored
		// touring id = 69
		// eif id = 68
//		if(bubble.isSponsored() && bubble.getUserId() == 68) {
//			// If so, inflate the EIF template
//			row = inflater.inflate(R.layout.template_bubble_eif,parent, false);
//		} else if(bubble.getUserId() == 69) {
//			// If so, inflate the Turing template
//			row = inflater.inflate(R.layout.template_bubble_turing,parent, false);
//		} else {
//			// Else use the standard bubble template
			//row = inflater.inflate(R.layout.template_bubble,parent, false);
//		}
			
		// Create the view objects
	/*	final ImageView bubbleIcon = (ImageView) row.findViewById(R.id.profilePicButton);
		final TextView profileName = (TextView) row.findViewById(R.id.userName_field);
		final TextView contentDescription = (TextView) row.findViewById(R.id.content_description);
		final ImageView contentPicture = (ImageView) row.findViewById(R.id.content_image);
		final LinearLayout urlFrame = (LinearLayout) row.findViewById(R.id.url_frame);
		final TextView url = (TextView) row.findViewById(R.id.url_field);
		final ProgressBar loadingAnim = (ProgressBar) row.findViewById(R.id.loading_anim);
		final ImageView hideButton = (ImageView) row.findViewById(R.id.hide_button);
		final ImageView starredButton = (ImageView) row.findViewById(R.id.star_button);
		final TextView hideButtonLabel = (TextView) row.findViewById(R.id.hide_button_label);
		final TextView starredButtonLabel = (TextView) row.findViewById(R.id.star_button_label);
		final RelativeLayout starButtonFrame = (RelativeLayout) row.findViewById(R.id.star_button_frame);
		final RelativeLayout hideButtonFrame = (RelativeLayout) row.findViewById(R.id.hide_button_frame);
		final RelativeLayout shareButtonFrame = (RelativeLayout) row.findViewById(R.id.share_button_frame);
		final TextView distanceEstimate = (TextView) row.findViewById(R.id.distance_field);*/
		
	/*	if(bubble.isSponsored() && bubble.getUserId() == 68) {
			// If so, inflate the EIF template
			bubbleIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.eif_bubble_header));
		} else if(bubble.getUserId() == 69) {
			// If so, inflate the Turing template
			bubbleIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.turing_logo_2013_small));
		}*/


	/*	// Disable the like/favourite buttons for static bubbles
		if (bubble.getBubbleId() != -1) {

			final View cellViewRef = row;

			hideButtonFrame.setClickable(true);
			hideButtonFrame.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Check if user is logged in
					if(!LoginManager.isLoggedIn()) {
						Toast.makeText(v.getContext(), "Please log in to hide bubbles", Toast.LENGTH_SHORT).show();
						return;
					}

					AddUserRating addUserRating = new AddUserRating();

					// If the user had already hidden this bubble, unhide
					if (Integer.valueOf(bubble.getUserRating()) == 1) {
						// "Unrate" the bubble
						bubble.setUserRating(0);
						// Unselect it
						hideButton.setColorFilter(null);
						hideButtonLabel.setTextColor(getContext().getResources().getColor(R.color.RATING_HINT));
						addUserRating.setRatingTypeId(Integer.toString(0));
					} else {
						// Otherwise, hide and give it a blue filter
						bubble.setUserRating(1);
						hideButton.setColorFilter(getContext().getResources().getColor(R.color.basewarp_blue));
						hideButtonLabel.setTextColor(getContext().getResources().getColor(R.color.basewarp_blue));
						addUserRating.setRatingTypeId(Integer.toString(1));

						// Animate the removal from the list view
						final Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out);
						animation.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationEnd(Animation animation) {
								remove(bubble);
								notifyDataSetChanged();
							}

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}
						});

						cellViewRef.startAnimation(animation);
					}
					// Either way, the star button should now be deselected
					starredButton.setColorFilter(null);

					// Submit the new rating of the bubble
					addUserRating.setBubbleId(Integer.toString(bubble.getBubbleId()));
					addUserRating.setUserId(Integer.toString(LoginManager.getUID()));
					addUserRating.setListener(null);
					addUserRating.execute();
				}
			});

			shareButtonFrame.setClickable(true);
			shareButtonFrame.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Util.shareBubble(bubble, v.getContext());
				}
			});

			starButtonFrame.setClickable(true);
			starButtonFrame.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// Check if user is logged in
					if(!LoginManager.isLoggedIn()) {
						Toast.makeText(v.getContext(), "Please log in to star bubbles", Toast.LENGTH_SHORT).show();
						return;
					}

					// If the user had already starred this bubble
					if (Integer.valueOf(bubble.getUserRating()) == 3) {
						// "Unrate" the bubble
						bubble.setUserRating(0);
						// Unselect it
						starredButton.setColorFilter(null);
						starredButtonLabel.setTextColor(getContext().getResources().getColor(R.color.RATING_HINT));
					} else {
						// Otherwise, star it and give it a blue filter
						bubble.setUserRating(3);
						starredButton.setColorFilter(getContext().getResources().getColor(R.color.basewarp_blue));
						starredButtonLabel.setTextColor(getContext().getResources().getColor(R.color.basewarp_blue));
					}
					// Either way, the star button should now be deselected
					hideButton.setColorFilter(null);

					AddUserRating addUserRating = new AddUserRating();
					addUserRating.setBubbleId(Integer.toString(bubble.getBubbleId()));
					addUserRating.setUserId(Integer.toString(LoginManager.getUID()));
					addUserRating.setRatingTypeId(Integer.toString(3));
					addUserRating.setListener(null);
					addUserRating.execute();

				}
			});

		}*/

		// Need to remove the buttons' focusability or else each listview item will not be clickable
		/*bubbleIcon.setFocusable(false);
		hideButton.setFocusable(false);
		starredButton.setFocusable(false);

		// Set common data
		if (profileName != null){
			if(bubble.getUserForename() != null && !bubble.getUserForename().equals("")) {
				profileName.setText(bubble.getUserForename());
			} else {
				profileName.setText(String.valueOf(bubble.getSort()));
			}
		}

		contentDescription.setText(Util.capitalizeDescription(bubble.getDescription()));

		if (bubbleListType == BubbleListTypes.NEWEST) {
			distanceEstimate.setText("(" + bubble.getTimeDiff() + ")");
		} else if(bubbleListType == BubbleListTypes.HOT) {
			Time now = new Time(Locale.getDefault().toString());
			now.setToNow();
			Date dateNow = new Date(now.toMillis(true));
			Date startDate = bubble.getStartDatetime();
			long diffInMs = dateNow.getTime() - startDate.getTime();
			int diffInSec = (int) TimeUnit.MILLISECONDS.toSeconds(diffInMs);
			PrettyTime p = new PrettyTime();
			Calendar calendar = Calendar.getInstance(Locale.getDefault()); // gets a calendar using the default time zone and locale.
			calendar.add(Calendar.SECOND, -1* diffInSec);
			Date addedTime = calendar.getTime();

			String timeDiff = p.format(addedTime);
			distanceEstimate.setText("(" + timeDiff +" - " + Util.getDistanceEstimate(bubble.getDistance()) + ")");
		} else {
			distanceEstimate.setText("(" + Util.getDistanceEstimate(bubble.getDistance()) + ")");
		}

		if (bubble.isSponsored() || bubble.getUserId() == 69) { // don't load the profile image for eif & turing bubbles

		} else {

			if(bubble.hasProfileImage()) {
				String key = Util.getBubbleListManagerKeyForProfilePic(bubble.getUserId(), Constants.profilePicThumbnailWidth, Constants.profilePicThumbnailHeight);
				if(BubbleListManager.containsImage(key)) {
					bubbleIcon.setImageBitmap(BubbleListManager.getImage(key));
				} else {
					new DownloadProfileImageTask()
					.downloadProfileImage(String.valueOf(bubble.getUserId()), bubbleIcon, Constants.profilePicThumbnailWidth, Constants.profilePicThumbnailHeight)
					.execute();
				}
			}
		}

		// Set rest of bubble depending on its type
		int bubbleType = bubble.getTypeId();

		switch(bubbleType) {
		case 1: // Text bubble?
			contentPicture.setVisibility(View.GONE);
			loadingAnim.setVisibility(View.GONE);
			break;
		case 2: // Image Bubble?

			contentPicture.setBackgroundColor(mContext.getResources().getColor(R.color.BUBBLE_BG));
			loadingAnim.setBackgroundColor(mContext.getResources().getColor(R.color.BUBBLE_BG));

			DisplayMetrics displaymetrics = new DisplayMetrics();
			((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int width =(int)( displaymetrics.widthPixels / 3.0);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentPicture.getLayoutParams();
			params.width = width;
			contentPicture.setLayoutParams(params);


			float dropShadowBorderInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, row.getResources().getDisplayMetrics());
			RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) contentDescription.getLayoutParams();
			width = (int) ((displaymetrics.widthPixels * 2.0f/3.0f) - (2*dropShadowBorderInPixels + 3*row.getResources().getDimension(R.dimen.bubble_inside_margin)));
			params2.width = width; // Make text only go to image + some margin
			contentDescription.setLayoutParams(params2);

			if(BubbleListManager.isImageDownloading(bubble.getContentUrl())) {
				contentPicture.setVisibility(View.GONE);
				loadingAnim.setVisibility(View.VISIBLE);
				urlFrame.setVisibility(View.GONE);
			} else if(!BubbleListManager.containsImage(bubble.getContentUrl())) {
				Log.w("imagedownload",bubble.getContentUrl());
				contentPicture.setVisibility(View.GONE);
				loadingAnim.setVisibility(View.VISIBLE);
				contentPicture.setVisibility(View.GONE);
				urlFrame.setVisibility(View.GONE);
				new DownloadBubbleImageTask()
				.downloadThumbnailImage(bubble, contentPicture, loadingAnim)
				.execute(); // In the case of an image bubble this field holds the url of the image on the server
			} else {
				contentPicture.setImageBitmap(BubbleListManager.getImage(bubble.getContentUrl()));
				contentPicture.setVisibility(View.VISIBLE);
				loadingAnim.setVisibility(View.GONE);
				urlFrame.setVisibility(View.GONE);
			}
			break;
		case 4: // Web Bubble?
			url.setText(bubble.getContentUrl()); // In the case of a web bubble this field holds the url submitted by the user
			urlFrame.setVisibility(View.VISIBLE);
			urlFrame.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(getContext(), WebViewActivity.class);
					i.putExtra("URL", bubble.getContentUrl());
					i.putExtra("bubble_id", String.valueOf(bubble.getBubbleId()));
					getContext().startActivity(i);
				}
			});
			url.setMovementMethod(null);
			url.setFocusable(false);
			url.setFocusableInTouchMode(false);
			url.setClickable(false);
			break;
		default:
			Log.e("BUBBLELISTADAPTER","Default statement entered");
			break;
		}

		// Indicate the user's current rating
		switch (Integer.valueOf(bubble.getUserRating())) {
		case 1: // Hidden
			hideButton.setColorFilter(getContext().getResources().getColor(R.color.basewarp_blue));
			break;
		case 3:
			starredButton.setColorFilter(getContext().getResources().getColor(R.color.basewarp_blue));
			break;
		default:
			break;
		}

		return row;
	}*/


	public OnItemClickListener onItemClickListener = new OnItemClickListener() {

		public void onItemClick(android.widget.AdapterView<?> arg0, View arg1,
				int position, long arg3) {
		};
	};
}
