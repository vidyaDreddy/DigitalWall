package com.digitalwall.model;

import android.content.pm.ActivityInfo;

import com.digitalwall.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 01/08/17.
 */

public class CampaignModel {


    private String campaignId;
    private String campaignName;
    private String clientId;
    private String status;
    private String campaignType;
    private String layoutOrientation;
    private ArrayList<ChannelModel> channelList;


    public CampaignModel(JSONObject object) throws JSONException {


        if (object.has("status"))
            setStatus(object.getString("status"));

        if (object.has("_id"))
            setCampaignId(object.getString("_id"));

        if (object.has("campaignName"))
            setCampaignName(object.getString("campaignName"));

        if (object.has("clientID"))
            setClientId(object.getString("clientID"));

        if (object.has("type"))
            setCampaignType(object.getString("type"));

        if (object.has("layout")) {
            JSONObject lObject = object.getJSONObject("layout");
            setLayoutOrientation(lObject.optString("orientation"));

            /*CHANNEL LIST ARRAY PARSING*/
            ArrayList<ChannelModel> mList = new ArrayList<>();
            if (lObject.has("channels")) {
                JSONArray cArray = lObject.getJSONArray("channels");
                for (int i = 0; i < cArray.length(); i++) {
                    JSONObject cObject = cArray.getJSONObject(i);
                    ChannelModel model = new ChannelModel(cObject);
                    model.setChannelId(getCampaignId() + model.getChannelId());
                    mList.add(model);
                }
            }
            setChannelList(mList);
        }

        /*VALIDATE THE ORIENTATION*/
        String orientation = getLayoutOrientation();
        if (!Utils.isValueNullOrEmpty(orientation) && orientation.equalsIgnoreCase("landscape"))
            setLayoutOrientation(orientation);
        else
            setLayoutOrientation("portrait");


    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLayoutOrientation() {
        return layoutOrientation;
    }

    public void setLayoutOrientation(String layoutOrientation) {
        this.layoutOrientation = layoutOrientation;
    }

    public ArrayList<ChannelModel> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<ChannelModel> channelList) {
        this.channelList = channelList;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }
}
