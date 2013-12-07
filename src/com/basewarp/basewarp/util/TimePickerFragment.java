package com.basewarp.basewarp.util;

import java.util.Calendar;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
	
	TextView callingView;
	
	public void setCallingView(TextView callingView) {
		this.callingView = callingView;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		// Create a new instance of DatePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, true);
	}

	//@Override
	public void onTimeSet(TimePicker view, int hour, int minute) {
		String h = (hour < 10) ? "0"+hour : Integer.toString(hour);
		String m = (minute < 10) ? "0"+minute : Integer.toString(minute);
		
		callingView.setText(h + ":" + m);		
	}
}