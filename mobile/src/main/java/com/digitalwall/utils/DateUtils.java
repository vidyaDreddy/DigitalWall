package com.digitalwall.utils;

import java.util.Calendar;

/**
 * Created by vidhayadhar
 * on 29/08/17.
 */

public class DateUtils {


    public static Calendar getCalendarDate(String date, String time) {

        Calendar cal = Calendar.getInstance();

        String[] DateStr = date.split("-");
        int year = Integer.parseInt(DateStr[0]);
        int month = Integer.parseInt(DateStr[1]);
        int day = Integer.parseInt(DateStr[2]);

        String[] TimeStr = time.split(":");
        int hour = Integer.parseInt(TimeStr[0]);
        int minute = Integer.parseInt(TimeStr[1]);
        int sec = Integer.parseInt(TimeStr[2]);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;

    }

    public static long convertTimeToSeconds(String h) {
        String[] h1 = h.split(":");
        int hour = Integer.parseInt(h1[0]);
        int minute = Integer.parseInt(h1[1]);
        int second = Integer.parseInt(h1[2]);
        long temp;
        temp = second + (60 * minute) + (3600 * hour);
        return temp;
    }


    private static int getHours(int seconds) {
        return seconds / 3600;
    }

    private static int getMinute(int seconds) {
        return (seconds % 3600) / 60;
    }

    private static int getSec(int seconds) {
        seconds = seconds % 60;
        return seconds;
    }


}
