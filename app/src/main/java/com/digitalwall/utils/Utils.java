package com.digitalwall.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.digitalwall.activities.BaseActivity;
import com.digitalwall.model.TileModel;
import com.digitalwall.model.LayoutModel;
import com.digitalwall.views.SliderLayout;
import com.github.pwittchen.networkevents.library.BusWrapper;
import com.squareup.otto.Bus;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 */

public class Utils {

    public static void hideStatusBar(BaseActivity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    public static final int REFERENCE_DEVICE_HEIGHT = 426;
    public static final int REFERENCE_DEVICE_WIDTH = 1280;


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

    public static boolean isMarshmallowOS() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static boolean isNougatOS() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
    }

    /**
     * a
     * ASSIGN THE COLOR
     **/
    @SuppressWarnings("deprecation")
    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23)
            return ContextCompat.getColor(context, id);
        else
            return context.getResources().getColor(id);
    }

    /**
     * ASSIGN THE DRAWABLE
     **/
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    /**
     * ASSIGN THE DIMENS
     **/
    public static int getDimen(Context context, int id) {
        return (int) context.getResources().getDimension(id);
    }

    /**
     * ASSIGN THE STRINGS
     **/
    public static String getStrings(Context context, int id) {
        String value = null;
        if (context != null && id != -1) {
            value = context.getResources().getString(id);
        }
        return value;
    }


    public static boolean isValueNullOrEmpty(String value) {
        boolean isValue = false;
        if (value == null || value.equals("") || value.equals("null")
                || value.trim().length() == 0) {
            isValue = true;
        }
        return isValue;
    }


    public static BusWrapper getOttoBusWrapper(final Bus bus) {
        return new BusWrapper() {
            @Override
            public void register(Object object) {
                bus.register(object);
            }

            @Override
            public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override
            public void post(Object event) {
                bus.post(event);
            }
        };
    }

    public static String getFileDownloadPath(String url) {

        Uri uri = Uri.parse(url);
        String extension = url.substring(url.lastIndexOf("."));
        String fileName = uri.getLastPathSegment() + extension;
        String dir = getSaveDir();
        return (dir + "/Asset/" + System.nanoTime() + "_" + fileName);
    }

    public static String getSaveDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/DigitalWall";
    }

}
