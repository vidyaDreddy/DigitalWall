package com.digitalwall.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by vidhayadhar
 * on 22/08/17.
 */

public class ScheduleModel {

    private String status;
    private String scheduleId;
    private String scheduleCampaignId;
    private String scheduleCampaignName;
    private String scheduleClientId;
    private ArrayList<ScheduleDateModel> mScheduleList;

    public ScheduleModel(JSONObject object) throws JSONException {

        if (object.has("status"))
            setStatus(object.getString("status"));

        if (object.has("_id"))
            setScheduleId(object.getString("_id"));

        if (object.has("campaignId"))
            setScheduleCampaignId(object.getString("campaignId"));

        if (object.has("campaignName"))
            setScheduleCampaignName(object.getString("campaignName"));

        if (object.has("clientID"))
            setScheduleClientId(object.getString("clientID"));

        if (object.has("schedules")) {

            JSONArray array = object.getJSONArray("schedules");

            ArrayList<ScheduleDateModel> mList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                ScheduleDateModel model = new ScheduleDateModel(jsonObject);
                mList.add(model);
            }
            setmScheduleList(mList);
        }

    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleCampaignId() {
        return scheduleCampaignId;
    }

    public void setScheduleCampaignId(String scheduleCampaignId) {
        this.scheduleCampaignId = scheduleCampaignId;
    }

    public String getScheduleCampaignName() {
        return scheduleCampaignName;
    }

    public void setScheduleCampaignName(String scheduleCampaignName) {
        this.scheduleCampaignName = scheduleCampaignName;
    }

    public String getScheduleClientId() {
        return scheduleClientId;
    }

    public void setScheduleClientId(String scheduleClientId) {
        this.scheduleClientId = scheduleClientId;
    }

    public ArrayList<ScheduleDateModel> getmScheduleList() {
        return mScheduleList;
    }

    public void setmScheduleList(ArrayList<ScheduleDateModel> mScheduleList) {
        this.mScheduleList = mScheduleList;
    }
}
