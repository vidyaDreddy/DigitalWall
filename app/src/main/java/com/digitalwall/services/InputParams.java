package com.digitalwall.services;

import android.os.Build;

import com.digitalwall.activities.BaseActivity;
import com.digitalwall.utils.DeviceInfo;
import com.digitalwall.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by vidhayadhars
 */

public class InputParams {


    /*GET THE ONLY UN_SIGNED ACCESS TOKEN*/
    public static JSONObject registerDeviceParams(BaseActivity parent) {
        JSONObject object = new JSONObject();
        try {
            object.put("deviceType", "Android");
            object.put("deviceID", DeviceInfo.getDeviceID(parent));
            object.put("deviceOsVersion", DeviceInfo.getDeviceOSVersion(parent));
            object.put("deviceResolution", DeviceInfo.getDeviceResolution(parent));
            object.put("deviceResolution", DeviceInfo.getDeviceResolution(parent));
            object.put("deviceBrand", DeviceInfo.getDeviceBrandName(parent));
            object.put("deviceModel", DeviceInfo.getDeviceModelName(parent));
            object.put("batteryPercentage", DeviceInfo.getBatteryPercentage(parent));
            object.put("deviceVolume", DeviceInfo.getDeviceVolume(parent));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
