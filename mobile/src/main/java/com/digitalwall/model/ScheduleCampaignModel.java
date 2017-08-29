package com.digitalwall.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Siva on 8/26/2017.
 */

public class ScheduleCampaignModel {
    private String startDate;
    private String endDate;
    private long startTime;
    private long endTime;
    private int jobid;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }

    private String campaignId;
    private String id;


    public ScheduleCampaignModel(JSONObject object) throws JSONException {

        if (object.has("_id"))
            setId(object.getString("_id"));

        if (object.has("startDate"))
            setStartDate(object.getString("startDate"));

        if (object.has("endDate"))
            setEndDate(object.getString("endDate"));

        if (object.has("startTime"))
            setStartTime(convertTimeToSeconds(object.getString("startTime")));

        if (object.has("endTime"))
            setEndTime(convertTimeToSeconds(object.getString("endTime")));

        if (object.has("campaignId"))
            setCampaignId(object.getString("campaignId"));

        if (object.has("jobId"))
            setJobid(object.getInt("jobId"));
    }

    public long convertTimeToSeconds(String h) {
        String[] h1 = h.split(":");
        int hour = Integer.parseInt(h1[0]);
        int minute = Integer.parseInt(h1[1]);
        int second = Integer.parseInt(h1[2]);
        long temp;
        temp = second + (60 * minute) + (3600 * hour);
        Log.d("long...", "..." + temp);
        return temp;
    }
}
