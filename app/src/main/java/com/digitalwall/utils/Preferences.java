package com.digitalwall.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vidhayadhar
 * on 01/08/17.
 */

public class Preferences {

    public static final String CAMPAIGN_SCHEDULE = "CAMPAIGN_SCHEDULE";
    public static final String CAMPAIGN_AUTO = "CAMPAIGN_AUTO";

    private static final String APP_PREF = "APP_PREF";
    public static final String PREF_KEY_CLIENT_ID = "PREF_KEY_CLIENT_ID";
    public static final String PREF_KEY_ORIENTATION = "PREF_KEY_ORIENTATION";
    public static final String PREF_KEY_AUTO_CAMPAIGN_ID = "PREF_KEY_AUTO_CAMPAIGN_ID";
    public static final String PREF_KEY_VOLUME = "PREF_KEY_VOLUME";


    public static void setStringSharedPref(Context context, String key, String value) {
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

    public static String getStringSharedPref(Context context, String key) {
        try {
            SharedPreferences authPreference = context.getSharedPreferences(APP_PREF,
                    Context.MODE_PRIVATE);
            return authPreference.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void setIntSharedPref(Context context, String key, int value) {
        try {
            if (context != null) {
                SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(APP_PREF,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
                appInstallInfoEditor.putInt(key, value);
                appInstallInfoEditor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int getIntSharedPref(Context context, String key) {
        try {
            SharedPreferences authPreference = context.getSharedPreferences(APP_PREF,
                    Context.MODE_PRIVATE);
            return authPreference.getInt(key, 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 60;
    }
}
