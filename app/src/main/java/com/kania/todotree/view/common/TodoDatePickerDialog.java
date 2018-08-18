package com.kania.todotree.view.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class TodoDatePickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_YEAR = "picker_init_year";
    private static final String ARG_MONTH = "picker_init_month";
    private static final String ARG_DAY_OF_MONTH = "picker_init_day_of_month";
    private OnDateSetListener mDateSetListener;

    public interface OnDateSetListener {
        void onDateSet(Date date);
    }

    public TodoDatePickerDialog() {
    }

    public static TodoDatePickerDialog newInstance(Date initDate, OnDateSetListener callback) {
        TodoDatePickerDialog dialog = new TodoDatePickerDialog();
        Calendar initCalendar = Calendar.getInstance();
        initCalendar.setTime(initDate);
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, initCalendar.get(Calendar.YEAR));
        args.putInt(ARG_MONTH, initCalendar.get(Calendar.MONTH));
        args.putInt(ARG_DAY_OF_MONTH, initCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.setArguments(args);
        dialog.setListener(callback);
        return dialog;
    }

    public void setListener(OnDateSetListener callback) {
        mDateSetListener = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year;
        int monthOfYear;
        int dayOfMonth;
        if (getArguments() != null) {
            Bundle args = getArguments();
            year = args.getInt(ARG_YEAR);
            monthOfYear = args.getInt(ARG_MONTH);
            dayOfMonth = args.getInt(ARG_DAY_OF_MONTH);
            Log.d("todo_tree", "onCreateDialog() get from args, year:" + year + " monthOfYear:" + monthOfYear + " dayOfMonth:" + dayOfMonth);
        } else {
            // Use the current date as the default values for the picker
            Calendar today = Calendar.getInstance();
            year = today.get(Calendar.YEAR);
            monthOfYear = today.get(Calendar.MONTH);
            dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
            Log.d("todo_tree", "onCreateDialog() get from today, year:" + year + " monthOfYear:" + monthOfYear + " dayOfMonth:" + dayOfMonth);
        }
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnDateSetListener) {
//            mDateSetListener = (OnDateSetListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnDateSetListener");
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDateSetListener == null) {
            Log.e("todo_tree", "onResume() mDateSetListener is null, dismiss");
            dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDateSetListener = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.d("todo_tree", "OnDateSetListener() called, year:" + year + " monthOfYear:" + monthOfYear + " dayOfMonth:" + dayOfMonth);
        Calendar setDate = Calendar.getInstance();
        setDate.set(year, monthOfYear, dayOfMonth);
        //mDateSetListener.OnDateSetListener(year, monthOfYear + 1, dayOfMonth);
        mDateSetListener.onDateSet(setDate.getTime());
    }
}
