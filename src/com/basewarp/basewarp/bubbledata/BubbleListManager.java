package com.basewarp.basewarp.bubbledata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.basewarp.basewarp.bubbledisplay.BubbleListFragment;
import com.basewarp.basewarp.location.GPSListener;
import com.basewarp.basewarp.location.GPSManager;
import com.basewarp.basewarp.services.BubbleListService;
import com.basewarp.basewarp.services.Closest;
import com.basewarp.basewarp.services.Hot;
import com.basewarp.basewarp.services.MyBubbles;
import com.basewarp.basewarp.services.Newest;
import com.basewarp.basewarp.services.ServiceListener;
import com.basewarp.basewarp.services.Starred;
import com.basewarp.basewarp.services.UserBubbles;
import com.basewarp.basewarp.util.Constants;
import com.basewarp.basewarp.util.Constants.BubbleListTypes;
import com.basewarp.basewarp.util.SerialBitmap;

/**
 * This class holds a static reference to certain orderings of bubble lists & associated data (such as images to be displayed).
 * This way a fragment to display some list can see if it has been created already
 * and fetch it from here (and otherwise create a new one).
 * @author Samuel
 *
 */
public class BubbleListManager implements ServiceListener, GPSListener {


	private static BubbleListManager instance = new BubbleListManager();
	private static HashMap<String,Bitmap> imageCache;
	private static HashMap<String,Boolean> imagesDownloading;
	private static BubbleListTypes attemptedSelection;
	private static BubbleListFragment attemptedNotifyMe;
	private static int attemptedBubbleNum;
	private static boolean[] refreshBubblesTags;
	private static ArrayList<ArrayList<BubbleModel>> bubbleLists = new ArrayList<ArrayList<BubbleModel>>(BubbleListTypes.values().length);
	private static BubbleListFragment[] listAdaptersToNotify = new BubbleListFragment[BubbleListTypes.values().length];
	private static int userBubblesUID; // This is used for the userbubbles service
	private static int lastUserBubblesUID;
	private boolean removeAfterUpdate = false;


	public static boolean isImageDownloading(String url) {
		if(imagesDownloading.containsKey(url))
			return imagesDownloading.get(url);
		else return false;
	}

	public static void setImageDownloadState(String url, boolean downloading) {
		imagesDownloading.put(url, Boolean.valueOf(downloading));
	}

	public static void init(Activity context) {	
		
		Log.w("BUBBLELISTMANAGER","INITCALLED");

		// Try to fetch cached data from files
		readBubbleListFromFile(context);

		if(bubbleLists == null) bubbleLists = new ArrayList<ArrayList<BubbleModel>>(BubbleListTypes.values().length);
		if(bubbleLists.size() == 0) {
			for(int i = 0; i < BubbleListTypes.values().length; i++) {
				bubbleLists.add(new ArrayList<BubbleModel>());
			}
		}
		if(imageCache == null) {
			imageCache = new HashMap<String, Bitmap>();
		}
		imagesDownloading = new HashMap<String, Boolean>();
		refreshBubblesTags = new boolean[BubbleListTypes.values().length];
	}
	
	public static boolean hasBeenInitialized() {
		return bubbleLists != null && bubbleLists.size() > 0;
	}

	@SuppressWarnings("unchecked")
	private static void readImageCacheFromFile(Activity context) {
		File cacheDir = context.getCacheDir();
		if(cacheDir.exists()) {
			File ic = context.getFileStreamPath(Constants.imageCacheFileName); // ImageCache cache file
			FileInputStream fis;
			ObjectInputStream ois;
			if(ic.exists()) {
				try {
					fis = context.openFileInput(Constants.imageCacheFileName);
					ois = new ObjectInputStream(fis);

					// Fetch the hashmap with the serialized bitmaps from the file, then put its bitmaps into this class' hashmap
					HashMap<String,SerialBitmap> temp  = (HashMap<String, SerialBitmap>) ois.readObject();
					imageCache = new HashMap<String, Bitmap>();
					for(String key : temp.keySet()) {
						imageCache.put(key, temp.get(key).getBitmap());
					}
					ois.close();
					temp = null;
				} catch (FileNotFoundException e) {
					Log.w("BubbleListManager", "Could not find " + Constants.imageCacheFileName);
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "Corrupted input stream for " + Constants.imageCacheFileName);
				} catch (IOException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "IOException for " + Constants.imageCacheFileName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "Cast to object failed for " + Constants.imageCacheFileName);
				}
				ic.delete(); // Remove cache file so we don't accidentally load it with stale data later (or if anything was corrupted and hence didn't load)
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void readBubbleListFromFile(Activity context) {
		File cacheDir = context.getCacheDir();
		if(cacheDir.exists()) {
			File bl = context.getFileStreamPath(Constants.bubbleDataFileName); // BubbleList cache file
			FileInputStream fis;
			ObjectInputStream ois;
			if(bl.exists()) {
				try {
					fis = context.openFileInput(Constants.bubbleDataFileName);
					ois = new ObjectInputStream(fis);
					bubbleLists = (ArrayList<ArrayList<BubbleModel>>) ois.readObject();
					ois.close();
				} catch (FileNotFoundException e) {
					Log.w("BubbleListManager", "Could not find " + Constants.bubbleDataFileName);
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "Corrupted input stream for " + Constants.bubbleDataFileName);
				} catch (IOException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "IOException for " + Constants.bubbleDataFileName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "Cast to object failed for " + Constants.bubbleDataFileName);
				}
				bl.delete(); // Remove cache file so we don't accidentally load it with stale data later (or if anything was corrupted and hence didn't load)
			}

		}
	}

	public static void removeCacheFiles(Activity context) {
		File cacheDir = context.getCacheDir();
		if(cacheDir.exists()) {
			File bl = context.getFileStreamPath(Constants.bubbleDataFileName); // BubbleList cache file
			File ic = context.getFileStreamPath(Constants.imageCacheFileName); // ImageCache cache file
			if(bl.exists()) bl.delete();
			if(ic.exists()) ic.delete();
		}
	}

	public static void saveState(Activity context) {
		saveBubbleLists(context);
//		saveImageCache(context); // Takes too long atm! (use threading and do it asynchronously?)
	}
	
	private static void saveBubbleLists(Activity context) {
		File cacheDir = context.getCacheDir();
		if(!cacheDir.exists()) cacheDir.mkdirs();
		if(bubbleLists != null && bubbleLists.size() > 0) {
			try {
				FileOutputStream fos = context.openFileOutput(Constants.bubbleDataFileName, Activity.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(bubbleLists);
				oos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Log.w("BubbleListManager","Storing bubbles to file failed!");
			} catch (IOException e) {
				e.printStackTrace();
				Log.w("BubbleListManager","Storing bubbles to file failed!");
			}
		}
	}

	private static void saveImageCache(Activity context) {
		File cacheDir = context.getCacheDir();
		if(!cacheDir.exists()) cacheDir.mkdirs();
		if(imageCache != null && !imageCache.isEmpty()) {
			try {
				FileOutputStream fos = context.openFileOutput(Constants.imageCacheFileName, Activity.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				// Need to temporarily create a new hashmap which uses a helper class to make the bitmaps serializable
				HashMap<String,SerialBitmap> temp = new HashMap<String, SerialBitmap>(imageCache.size());
				for(String key : imageCache.keySet()) {
					temp.put(key, new SerialBitmap((Bitmap)imageCache.get(key)));
				}
				oos.writeObject(temp);
				oos.close();
				temp = null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Log.w("BubbleListManager","Storing imagecache to file failed!");
			} catch (IOException e) {
				e.printStackTrace();
				Log.w("BubbleListManager","Storing imagecache to file failed!");
			}
		}
	}

	public static void clearBubbles() {
		for(int i = 0; i < BubbleListTypes.values().length; i++) {
			bubbleLists.get(i).clear();
		}
	}

	public static void addImage(String url, Bitmap image) {
		if(!imageCache.containsKey(url)) imageCache.put(url, image);
	}

	public static boolean containsImage(String url) {
		if(imageCache.containsKey(url)) return true;
		else return false;
	}

	public static Bitmap getImage(String url) {
		return imageCache.get(url);
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<BubbleModel> getCurrentBubbles(BubbleListTypes selection, Activity context) {
		if(bubbleLists.size() == 0) init(context);
		if(selection == BubbleListTypes.USERBUBBLES && lastUserBubblesUID != userBubblesUID) {
			// We need to clear the userbubbles if we are requesting them for a new user
			bubbleLists.get(selection.ordinal()).clear(); 
		}
		return (ArrayList<BubbleModel>)bubbleLists.get(selection.ordinal()).clone();
	}

	/**
	 * Starts a service (async task) to fetch new bubbles from the server.
	 * @param selection: Which type of bubbles to fetch
	 * @param notifyMe: The listener to notify on completion/failure
	 * @param firstBubbleNum: The starting index for the list. (e.g.: already have 20 bubbles; want the next X bubbles (assuming there are more) -> set index to 20).
	 */
	public static void updateBubbles(BubbleListTypes selection, BubbleListFragment notifyMe, int firstBubbleNum) {		
		BubbleListService service = null;

		Location loc = GPSManager.getLocation();
		if (loc == null) {
			BubbleListManager.attemptedSelection = selection;
			BubbleListManager.attemptedNotifyMe = notifyMe;
			BubbleListManager.attemptedBubbleNum = firstBubbleNum;
			if(!GPSManager.isLocationWarped()) {
				instance.setRemoveAfterUpdate(true);
				GPSManager.setLocationChangeListener(instance);
			}
			return;
		}

		switch(selection) {
		case CLOSEST:
			service = new Closest(firstBubbleNum);
			break;
		case NEWEST:
			service = new Newest(firstBubbleNum);
			break;
		case HOT:
			service = new Hot(firstBubbleNum);
			break;
		case STARRED:
			service = new Starred(firstBubbleNum);
			break;
		case MYBUBBLES:
			service = new MyBubbles(firstBubbleNum);
			break;
		case USERBUBBLES:
			service = new UserBubbles(firstBubbleNum,userBubblesUID);
			break;
		default:
			break;
		}

		if(service != null) {
			listAdaptersToNotify[selection.ordinal()] = notifyMe;
			service.setListener(instance);
			service.execute();
		}
	}


	//@Override
	public void beforeStart() {

	}

	//@Override
	public void onComplete(Object data, StatusCode rc) {
		switch(rc) {
		case CONNECTION_TIMEOUT:
			handleTimeout();
			break;
		case CONNECTION_FAILED:
			handleTimeout();
			break;
		default:
			unknownError();
			break;
		}
	}

	public static void clearBubbleList(BubbleListTypes type) {
		bubbleLists.get(type.ordinal()).clear();
		Log.w("BUBBLELISTMANAGER", "BUBBLES CLEARED FOR TYPE: " + type.toString());
	}

	public static void refreshBubbles(BubbleListTypes type, BubbleListFragment notifyMe) {
		refreshBubblesTags[type.ordinal()] = true;
		updateBubbles(type, notifyMe, 0);
	}


	public void unknownError() {
		for (int i = 0; i < listAdaptersToNotify.length; i++) {
			if (listAdaptersToNotify[i] != null) {
				listAdaptersToNotify[i].unknownError();
			}
		}
	}

	public void handleTimeout() {
		for (int i = 0; i < listAdaptersToNotify.length; i++) {
			if (listAdaptersToNotify[i] != null) {
				listAdaptersToNotify[i].handleTimeout();
			}
		}
	}

	public void handleNoBubbles() {
		for (int i = 0; i < listAdaptersToNotify.length; i++) {
			if (listAdaptersToNotify[i] != null) {
				listAdaptersToNotify[i].handleNoBubbles();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void onComplete(Object data, StatusCode rc, BubbleListTypes type) {

		switch(rc) {
		case CONNECTION_TIMEOUT:
			handleTimeout();
			break;
		case BUBBLE_DATA_OK:
			int lastElem = bubbleLists.get(type.ordinal()).size();

			// Append returned bubbles to the list of current bubbles
			ArrayList<BubbleModel> instance = bubbleLists.get(type.ordinal());
			instance.addAll((ArrayList<BubbleModel>)data);

			// Call for new bubbles was from refresh button, so clear the old bubbles from the list
			if(refreshBubblesTags[type.ordinal()]) {
				for(int i = 0; i < lastElem; i++) {
					instance.remove(0);
				}
				refreshBubblesTags[type.ordinal()] = false;
			}
			break;
		case BUBBLES_EMPTY:
			clearBubbleList(type);
			handleNoBubbles();
			return;
		default:
			handleTimeout();
			break;
		}

		if(listAdaptersToNotify[type.ordinal()] != null) {
			listAdaptersToNotify[type.ordinal()].notifyDataSetChanged((ArrayList<BubbleModel>)(bubbleLists.get(type.ordinal())).clone());
			// Set the reference to null to prevent multiple requests being handled
			listAdaptersToNotify[type.ordinal()] = null;
		}
	}

	public void onLocationFound(Location location) {
		updateBubbles(attemptedSelection, attemptedNotifyMe,attemptedBubbleNum);
		instance.setRemoveAfterUpdate(false);
	}

	public static void setUserBubblesUID(int userBubblesUID) {
		BubbleListManager.lastUserBubblesUID = BubbleListManager.userBubblesUID;
		BubbleListManager.userBubblesUID = userBubblesUID;
	}

	//@Override
	public void setRemoveAfterUpdate(boolean value) {
		this.removeAfterUpdate = value;
	}

	//@Override
	public boolean removeAfterUpdate() {
		return removeAfterUpdate;
	}
}
