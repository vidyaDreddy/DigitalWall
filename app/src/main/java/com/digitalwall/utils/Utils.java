package com.digitalwall.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.digitalwall.R;
import com.digitalwall.activities.BaseActivity;
import com.digitalwall.activities.DashboardActivity;
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

    /**
     * ARIAL REGULAR
     **/
    public static Typeface setRobotoTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular_0.ttf");
    }

    @SuppressLint("SetTextI18n")
    public static void showRegisterPlayerNoteView(DashboardActivity parent) {

        parent.ll_display_key.setVisibility(View.VISIBLE);
        parent.rl_main.setVisibility(View.GONE);
        parent.tv_reg_note.setText(Utils.getStrings(parent, R.string.txt_reg_note));
        parent.tv_display_key.setText(Utils.getStrings(parent, R.string.txt_license_key) + parent.display_key);
    }

    public static void showPlayerSyncView(DashboardActivity parent) {
        Utils.showProgressDialog(parent);
        parent.ll_display_key.setVisibility(View.VISIBLE);
        parent.rl_main.setVisibility(View.GONE);
        parent.tv_reg_note.setText(Utils.getStrings(parent, R.string.txt_auto_campaign_sync_note));
        parent.tv_display_key.setText(Utils.getStrings(parent, R.string.txt_player_reg_successfully));
    }

    public static void showPlayerSyncFailedView(DashboardActivity parent) {
        Utils.hideProgressBar(parent);
        parent.ll_display_key.setVisibility(View.VISIBLE);
        parent.rl_main.setVisibility(View.GONE);
        parent.tv_reg_note.setText(Utils.getStrings(parent, R.string.txt_auto_campaign_sync_failed));
        parent.tv_display_key.setText(Utils.getStrings(parent, R.string.txt_player_reg_successfully));
    }


    public static void hideRegisterPlayerView(DashboardActivity parent) {
        Utils.hideProgressBar(parent);
        parent.ll_display_key.setVisibility(View.GONE);
        parent.rl_main.setVisibility(View.VISIBLE);
    }

    private static Dialog showProgressDialog(BaseActivity baseActivity) {

        Dialog mDialog = new Dialog(baseActivity,R.style.NewDialog);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater mInflater = LayoutInflater.from(baseActivity);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View layout = mInflater.inflate(R.layout.progressbar_custom, null);
        mDialog.setContentView(layout);

        if (baseActivity.progressDialog != null) {
            baseActivity.progressDialog.dismiss();
            baseActivity.progressDialog = null;
        }

        baseActivity.progressDialog = mDialog;

        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        return mDialog;
    }

    public static void hideProgressBar(BaseActivity parent) {
        if (parent.progressDialog != null)
            parent.progressDialog.dismiss();
    }


}
