package com.digitalwall.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vidhayadhar
 * on 01/08/17.
 */

public class AssetsModel {

    private long assetDuration;
    private String assetId;
    private String assetType;
    private String assetUrl;


    public AssetsModel(JSONObject object) throws JSONException {

        if (object.has("_id"))
            setAssetId(object.getString("_id"));

        if (object.has("duration") && !object.isNull("duration"))
            setAssetDuration((long) object.getInt("duration"));

        if (object.has("type"))
            setAssetType(object.getString("type"));

        if (object.has("url"))
            setAssetUrl(object.getString("url"));

    }


    public long getAssetDuration() {
        return assetDuration;
    }

    public void setAssetDuration(long assetDuration) {
        this.assetDuration = assetDuration;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetUrl() {
        return assetUrl;
    }

    public void setAssetUrl(String assetUrl) {
        this.assetUrl = assetUrl;
    }
}
