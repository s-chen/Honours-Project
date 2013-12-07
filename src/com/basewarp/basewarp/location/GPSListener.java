package com.basewarp.basewarp.location;

import android.location.Location;

public interface GPSListener {
	void onLocationFound(Location location);
	public void setRemoveAfterUpdate(boolean value);
	public boolean removeAfterUpdate();
}
