package com.basewarp.basewarp.util;

import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerFragment
extends DialogFragment
implements OnDateSetListener {
	
	TextView callingView;
	
	public void setCallingView(TextView callingView) {
		this.callingView = callingView;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance(Locale.ENGLISH);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		month++;
		String d = (day < 10) ? "0"+day : Integer.toString(day);
		String m = (month < 10) ? "0"+month : Integer.toString(month);
		
		callingView.setText(d + "/" + m + "/" + year);
	}
}