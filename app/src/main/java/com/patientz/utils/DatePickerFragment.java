package com.patientz.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {
    static String formattedDate;
    TextView editdate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT,this, year, month, day);

        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        // Create a new instance of DatePickerDialog and return it
        return dpd;
    }

    public DatePickerFragment(TextView editdate) {
        super();
        this.editdate = editdate;
    }


    @SuppressLint("SimpleDateFormat")
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy", Locale.ENGLISH);
        formattedDate = sdf.format(c.getTime());
        editdate.setText(formattedDate);
        return;
    }
}