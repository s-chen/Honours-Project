package si.chen.honours.project.login;

import si.chen.honours.project.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Set up authenticated UI fragment for Facebook to show user when authenticated
public class LoggedInFragment extends Fragment {

	private static final String TAG = "LoggedInFragment";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.activity_logged_in_fragment, container, false);
	    
	    Log.d(TAG, "Showing authenticated fragment..");
	    return view;
	    
	}

}
