package kr.latera.mopler.dialog;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import kr.latera.mopler.R;

/**
 * Created by Jinwoo Shin on 2017-04-25.
 */

public class DatePickerDialogFactory extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener
{
	private DatePickerDialog.OnDateSetListener mListener;

	public void setListener(DatePickerDialog.OnDateSetListener onDateSetListener)
	{
		this.mListener = onDateSetListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialogFactory and return it
		return new DatePickerDialog(getActivity(), R.style.DialogTheme, this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		// Do something with the date chosen by the user
		if(mListener != null)
		{
			mListener.onDateSet(view, year, month, day);
		}
	}
}
