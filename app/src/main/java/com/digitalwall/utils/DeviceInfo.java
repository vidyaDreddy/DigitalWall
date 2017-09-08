package com.digitalwall.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.digitalwall.activities.BaseActivity;

import java.util.Random;

/**
 * Created by vidhayadhar on 30/07/17.
 */

public class DeviceInfo {


    /*RETURNS THE DEVICE CURRENT BATTERY PERCENTAGE*/
    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }


    /*RETURN THE DEVICE CURRENT VOLUME*/
    public static int getDeviceVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /*RETURN THE DEVICE OS VERSION*/
    public static String getDeviceOSVersion(Context context) {
        return Build.VERSION.RELEASE;
    }

    /*RETURN THE DEVICE BRAND NAME*/
    public static String getDeviceBrandName(Context context) {
        return Build.BRAND;
    }

    /*RETURN THE DEVICE HARDWARE NAME*/
    public static String getDeviceHardwareName(Context context) {
        return Build.HARDWARE;
    }

    /*RETURN THE DEVICE MANUFACTURE NAME*/
    public static String getDeviceManufacture(Context context) {
        return Build.MANUFACTURER;
    }

    /*RETURN THE DEVICE MODEL NAME*/
    public static String getDeviceModelName(Context context) {
        return Build.MODEL;
    }

    /*RETURN THE DEVICE ID*/
    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    /*RETURN THE DEVICE RESOLUTION*/
    public static String getDeviceResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return "{" + width + "," + height + "}";
    }

    public static void setDeviceOrientation(Activity parent, String orientation) {

        if (!Utils.isValueNullOrEmpty(orientation)
                && orientation.equalsIgnoreCase("landscape"))
            parent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            parent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    /**
     * GET THE DEVICE WIDTH
     **/
    public static int getDeviceWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * GET THE DEVICE HEIGHT
     **/
    public static int getDeviceHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    public static int getReferenceWidth(String orientation) {
        if (!Utils.isValueNullOrEmpty(orientation)
                && orientation.equalsIgnoreCase("landscape"))
            return 1920;
        else
            return 1080;
    }

    public static int getReferenceHeight(String orientation) {
        if (!Utils.isValueNullOrEmpty(orientation)
                && orientation.equalsIgnoreCase("landscape"))
            return 1080;
        else
            return 1920;
    }

    public static int randomJobId() {
        Random rand = new Random();
        return rand.nextInt(100000000);

    }
}
