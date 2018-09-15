package com.kania.todotree.view.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class TodoDateUtil {

    public static final String DATEFORMAT_DATE = "yyyyMMdd";
    public static final String DATEFORMAT_TIME = "hhmm";

    public static int campareDate(Calendar target, Calendar today) {
        int diffDays;
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        diffDays = (int) ((target.getTimeInMillis() - today.getTimeInMillis())
                / (1000 * 60 * 60 * 24));

        return diffDays;
    }

    public static Date getDateFromTodoDate(long todoDate) {
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTimeInMillis(todoDate);
        return dueDate.getTime();
    }

    /*
    public static Date getDateFromTodoDate(String todoDate) {
        Calendar dueDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT_DATE);

        try {
            dueDate.setTime(sdf.parse(todoDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDate.getTime();
    }
    */

    public static long getCurrent() {
        Calendar now =  Calendar.getInstance();
        return now.getTimeInMillis();
    }

    public static Date getCurrentDate() {
        Calendar now =  Calendar.getInstance();
        return now.getTime();
    }

    public static String getFormatedDateString(Context context, long todoTimeInMillis) {
        if (todoTimeInMillis < 0)
            return "";
        else
            return DateUtils.formatDateTime(context, todoTimeInMillis,
                    DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY);
    }

    public static String getFormatedDateString(Context context, Date date) {
        return getFormatedDateString(context, date.getTime());
    }

    public static String getFormatedDateAndTimeString(Context context, long todoTimeInMillis) {
        if (todoTimeInMillis < 0)
            return "";
        else
            return DateUtils.formatDateTime(context, todoTimeInMillis,
                    DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);
    }

    public static String getFormatedDateAndTimeString(Context context, Date date) {
        return getFormatedDateAndTimeString(context, date.getTime());
    }

    public static Date removeTimeFromDate(Date dateWithTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(dateWithTime);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    public static long getTimeInMillis(Date target) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(target);
        return calendar.getTimeInMillis();

    }

    public static boolean isToday(long dueDate) {
        long todayWithoutTime = removeTimeFromDate(getCurrentDate()).getTime();
        //TODO do need to remove time from dueDate?
        return (todayWithoutTime == dueDate);
    }

    /*
    public static String getFormatedTime(Context context, long todoStartTime, long todoEndTime) {
        String ret = "";

        if (todoStartTime > TodoData.TIME_NOT_EXIST) {
            if (todoEndTime > TodoData.TIME_NOT_EXIST) {
                ret = DateUtils.formatDateRange(context, todoStartTime, todoEndTime,
                        DateUtils.FORMAT_SHOW_TIME);
            } else {
                ret = DateUtils.formatDateRange(context, todoStartTime, todoStartTime,
                        DateUtils.FORMAT_SHOW_TIME);
            }
        } else {
            ret = "";
        }

        return ret;
    }
    */
}
