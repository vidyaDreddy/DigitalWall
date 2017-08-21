package com.digitalwall.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 01/08/17.
 */

public class ChannelModel {


    private String channelId;
    private int channelHeight;
    private int channelWidth;
    private int channelLeftMargin;
    private int channelTopMargin;
    private String channelColor;
    private String volume;
    private ArrayList<AssetsModel> assetsList;


    public ChannelModel(JSONObject object) throws JSONException {

        if (object.has("id"))
            setChannelId(object.getString("id"));

        if (object.has("height"))
            setChannelHeight(object.getInt("height"));

        if (object.has("width"))
            setChannelWidth(object.getInt("width"));

        if (object.has("left"))
            setChannelLeftMargin(object.getInt("left"));

        if (object.has("top"))
            setChannelTopMargin(object.getInt("top"));

        if (object.has("fill"))
            setChannelColor(object.getString("fill"));

        if (object.has("volume"))
            setVolume(object.getString("volume"));

        ArrayList<AssetsModel> mList = new ArrayList<>();
        if (object.has("assets")) {
            JSONArray sArray = object.getJSONArray("assets");
            for (int i = 0; i < sArray.length(); i++) {
                JSONObject aObject = sArray.getJSONObject(i);
                AssetsModel model = new AssetsModel(aObject);
                if (i == 1) {
                    model.setAssetType("video");
                    model.setAssetDuration(30000);
                }
                mList.add(model);
            }
        }
        setAssetsList(mList);

    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getChannelHeight() {
        return channelHeight;
    }

    public void setChannelHeight(int channelHeight) {
        this.channelHeight = channelHeight;
    }

    public int getChannelWidth() {
        return channelWidth;
    }

    public void setChannelWidth(int channelWidth) {
        this.channelWidth = channelWidth;
    }

    public int getChannelLeftMargin() {
        return channelLeftMargin;
    }

    public void setChannelLeftMargin(int channelLeftMargin) {
        this.channelLeftMargin = channelLeftMargin;
    }

    public int getChannelTopMargin() {
        return channelTopMargin;
    }

    public void setChannelTopMargin(int channelTopMargin) {
        this.channelTopMargin = channelTopMargin;
    }

    public ArrayList<AssetsModel> getAssetsList() {
        return assetsList;
    }

    public void setAssetsList(ArrayList<AssetsModel> assetsList) {
        this.assetsList = assetsList;
    }

    public String getChannelColor() {
        return channelColor;
    }

    public void setChannelColor(String channelColor) {
        this.channelColor = channelColor;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
