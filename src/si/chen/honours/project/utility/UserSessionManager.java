package si.chen.honours.project.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

// Manages user interaction with the application (including SharedPreferences)
public class UserSessionManager {
	
	SharedPreferences mPref;
	Editor mEditor;
	Context mContext;
	int PRIVATE_MODE = 0;
	
	private static final String PREF_NAME = "UserPref";
	
	
	// Constructor
	public UserSessionManager(Context context) {
		
		this.mContext = context;
		mPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		mEditor = mPref.edit();
	}

}
