package com.digitalwall.services;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by ${VIDYA}
 */

public class ApiConfiguration {


    final static String SERVER_NOT_RESPONDING = "We are unable to connect the internet. " + "Please check your connection and try again.";
    public final static String ERROR_RESPONSE_CODE = "We could not process your request at this time. Please try again later.";
    public static final int TIMEOUT = 8000;

    private static final String APP_PREF = "APP_PREF";
    public static final String PREF_KEY_DISPLAY_ID = "PREF_KEY_DISPLAY_ID";
    public static final String AUTHORIZATION = "AUTHORIZATION";


    public static final String PLAT_FORM = "Android";


    private static final String BASE_SERVER_URL = "http://dev.digitalwall.in/dw/";
    public static final String OUTPUT_FILTER = "?outputFIlters={\"campaingID\":1,\"layout\":1}";

    //ADD A DEVICE
    public static final String DEVICES = BASE_SERVER_URL + "user/register";
    public static final int DEVICES_CODE = 1;

    public static final int GET_CAMPAIGN_INFO_CODE = 2;
    public static final String GET_CAMPAIGN_INFO = BASE_SERVER_URL + "campaign/%s";

    //GET THE AUTO  CAMPAIGN INFO
    public static final String GET_AUTO_CAMPAIGN_INFO = BASE_SERVER_URL + "campaign/%s";
    public static final int GET_AUTO_CAMPAIGN_INFO_CODE = 3;


    public static final String GET_SCHEDULE_INFO = BASE_SERVER_URL + "schedule/%s";
    public static final int GET_SCHEDULE_INFO_CODE = 4;


    public static void setAuthToken(Context context, String key, String value) {
        try {
            if (context != null) {
                SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(APP_PREF,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
                appInstallInfoEditor.putString(key, value);
                appInstallInfoEditor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAuthToken(Context context, String key) {
        try {
            SharedPreferences authPreference = context.getSharedPreferences(APP_PREF,
                    Context.MODE_PRIVATE);
            return authPreference.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
