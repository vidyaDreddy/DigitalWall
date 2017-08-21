package com.digitalwall.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.digitalwall.model.TileModel;
import com.digitalwall.model.LayoutModel;
import com.digitalwall.views.SliderLayout;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 */

public class Utils {


    /** Returns the consumer friendly device name */
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


    /**
     * HIDE THE KEYBOARD
     **/
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static boolean isValueNullOrEmpty(String value) {
        boolean isValue = false;
        if (value == null || value.equals("") || value.equals("null")
                || value.trim().length() == 0) {
            isValue = true;
        }
        return isValue;
    }


    public  static ArrayList<LayoutModel> getLayoutList(){

        ArrayList<LayoutModel> list=new ArrayList<>();

        LayoutModel m1= new LayoutModel(12,"VIDYA",800,800,0,0, "#3F51B5",Utils.getAssetList());
        LayoutModel m2= new LayoutModel(14,"VIDYA",400,400,800,0,"#FF4081",Utils.getAssetList1());
        list.add(m1);
        list.add(m2);
        return list;

    }




    public  static ArrayList<TileModel> getAssetList(){

        ArrayList<TileModel> list=new ArrayList<>();

        TileModel m1=new TileModel("Image","http://www.hdwallpapers.in/thumbs/2017/misako_be_kind_the_lego_ninjago_movie_2017-t2.jpg","1",6000,"Default");
        TileModel m2=new TileModel("Image","http://tvfiles.alphacoders.com/100/hdclearart-10.png","1",4000,"Accordion");
        TileModel m3=new TileModel("Image","https://wallpaperbrowse.com/media/" +
                "images/Dubai-Photos-Images-Oicture-Dubai-Landmarks-800x600.jpg",
                "1",2134,"Background2Foreground");
        TileModel m4=new TileModel("Image","http://www.hdwallpapers.in/thumbs" +
                "/2017/metal_vases_hd-t2.jpg","1",7000,"CubeIn");
        TileModel m5=new TileModel("Image","http://www.hdwallpapers.in/thumbs" +
                "/2009/real_artists_dont_make_wallpapers-t2.jpg","1",1000,"DepthPage");
        TileModel m6=new TileModel("Image","http://www.hdwallpapers.in/" +
                "thumbs/2015/android_marshmallow_6-t2.jpg","1",2134,"Fade");
        TileModel m7=new TileModel("Image","http://www.hdwallpapers.in" +
                "/thumbs/2009/green_abstract-t2.jpg","1",100,"FlipHorizontal");
        TileModel m8=new TileModel("Image","http://www.hdwallpapers.in/" +
                "thumbs/2016/paintwave_pink-t2.jpg","1",10000,"FlipPage");

        list.add(m1);
        list.add(m2);
        list.add(m3);
        list.add(m4);
        list.add(m5);
        list.add(m6);
        list.add(m7);
        list.add(m8);


        return list;

    }


    public  static ArrayList<TileModel> getAssetList1(){

        ArrayList<TileModel> list=new ArrayList<>();


        TileModel m5=new TileModel("Image","http://www.hdwallpapers.in/thumbs" +
                "/2009/real_artists_dont_make_wallpapers-t2.jpg","1",1000,"DepthPage");
        TileModel m6=new TileModel("Image","http://www.hdwallpapers.in/" +
                "thumbs/2015/android_marshmallow_6-t2.jpg","1",2134,"Fade");
        TileModel m7=new TileModel("Image","http://www.hdwallpapers.in" +
                "/thumbs/2009/green_abstract-t2.jpg","1",100,"FlipHorizontal");
        TileModel m8=new TileModel("Image","http://www.hdwallpapers.in/" +
                "thumbs/2016/paintwave_pink-t2.jpg","1",10000,"FlipPage");


        list.add(m5);
        list.add(m6);
        list.add(m7);
        list.add(m8);


        return list;

    }


    public  static SliderLayout.Transformer getAnimation(String anim){
        SliderLayout.Transformer mTransformer;


        switch (anim){
            case "Accordion":
                mTransformer=SliderLayout.Transformer.Accordion;
                break;
            case "Background2Foreground":
                mTransformer=SliderLayout.Transformer.Background2Foreground;
                break;
            case "CubeIn":
                mTransformer=SliderLayout.Transformer.CubeIn;
                break;
            case "DepthPage":
                mTransformer=SliderLayout.Transformer.DepthPage;
                break;
            case "Fade":
                mTransformer=SliderLayout.Transformer.Fade;
                break;
            case "FlipHorizontal":
                mTransformer=SliderLayout.Transformer.FlipHorizontal;
                break;
            case "FlipPage":
                mTransformer=SliderLayout.Transformer.FlipPage;
                break;
            case "Foreground2Background":
                mTransformer=SliderLayout.Transformer.Foreground2Background;
                break;
            case "RotateDown":
                mTransformer=SliderLayout.Transformer.RotateDown;
                break;
            case "RotateUp":
                mTransformer=SliderLayout.Transformer.RotateUp;
                break;
            case "Stack":
                mTransformer=SliderLayout.Transformer.Stack;
                break;
            case "Tablet":
                mTransformer=SliderLayout.Transformer.Tablet;
                break;
            case "ZoomIn":
                mTransformer=SliderLayout.Transformer.ZoomIn;
                break;
            case "ZoomOutSlide":
                mTransformer=SliderLayout.Transformer.ZoomOutSlide;
                break;
            case "ZoomOut":
                mTransformer=SliderLayout.Transformer.ZoomOut;
                break;
            default:
                mTransformer=SliderLayout.Transformer.Default;
                break;
        }

        return mTransformer;
    }
}
