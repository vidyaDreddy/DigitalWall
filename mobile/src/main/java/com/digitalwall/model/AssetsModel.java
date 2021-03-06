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
    private String channel_id;
    private String asset_local_url;


    public AssetsModel(JSONObject object) throws JSONException {

        if (object.has("_id"))
            setAssetId(object.getString("_id"));

        if (object.has("duration") && !object.isNull("duration"))
            setAssetDuration((long) object.getInt("duration"));

        if (object.has("type"))
            setAssetType(object.getString("type"));

        if (object.has("url"))
            setAssetUrl(object.getString("url"));

        if (object.has("local_url"))
            setAsset_local_url(object.getString("local_url"));

        if (object.has("channel_id"))
            setChannel_id(object.getString("channel_id"));

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

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getAsset_local_url() {
        return asset_local_url;
    }

    public void setAsset_local_url(String asset_local_url) {
        this.asset_local_url = asset_local_url;
    }
}
