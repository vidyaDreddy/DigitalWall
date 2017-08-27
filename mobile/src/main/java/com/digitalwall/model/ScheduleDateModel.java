package com.digitalwall.model;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by vidhayadhar
 * on 22/08/17.
 */

public class ScheduleDateModel {

    private Calendar sDate;
    private Calendar eDate;
    private String dType;

    public ScheduleDateModel(JSONObject jsonObject) {

        /*END DATE*/
        String eDate = jsonObject.optString("endDate");
        String eTime = jsonObject.optString("endTime");
        Calendar eCal = getCalendarDate(eDate, eTime);
        seteDate(eCal);

        /*START DATE*/
        String sDate = jsonObject.optString("endDate");
        String sTime = jsonObject.optString("startTime");
        Calendar sCal = getCalendarDate(sDate, sTime);
        setsDate(sCal);

    }


    private Calendar getCalendarDate(String date, String time) {

        Calendar cal = Calendar.getInstance();

        String[] DateStr = date.split("-");
        int year = Integer.parseInt(DateStr[0]);
        int month = Integer.parseInt(DateStr[1]);
        int day = Integer.parseInt(DateStr[2]);


        String[] TimeStr = time.split(":");
        int hour = Integer.parseInt(TimeStr[0]);
        int minute = Integer.parseInt(TimeStr[1]);
        int sec = 0;//Integer.parseInt(TimeStr[2]);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;

    }


    public Calendar getsDate() {
        return sDate;
    }

    public void setsDate(Calendar sDate) {
        this.sDate = sDate;
    }

    public Calendar geteDate() {
        return eDate;
    }

    public void seteDate(Calendar eDate) {
        this.eDate = eDate;
    }

    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }
}
