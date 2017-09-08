package com.digitalwall.model;

import org.json.JSONObject;

/**
 * Created by vidhayadhar
 */

public class DeviceModel {

    private String displayKey;
    private String deviceId;
    private String id;

    public DeviceModel(JSONObject dObject) {

        if(dObject.has("registrationID"))
            setDisplayKey(dObject.optString("registrationID"));

        if(dObject.has("_id"))
            setId(dObject.optString("_id"));


    }

    public String getDisplayKey() {
        return displayKey;
    }

    public void setDisplayKey(String displayKey) {
        this.displayKey = displayKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
