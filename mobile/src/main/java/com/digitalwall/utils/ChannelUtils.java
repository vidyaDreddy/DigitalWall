package com.digitalwall.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bizooku.SliderTypes.BaseSliderView;
import com.bizooku.SliderTypes.DefaultSliderView;
import com.digitalwall.Indicators.PagerIndicator;
import com.digitalwall.R;
import com.digitalwall.Tricks.ViewPagerEx;
import com.digitalwall.activities.BaseActivity;
import com.digitalwall.activities.PlayerActivity;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.views.SliderLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 01/08/17.
 */

public class ChannelUtils {





    /*
     * CRATING THE TILE WITH THE GIVEN DATA AND DYNAMIC DEVICE BASE BASE CALCULATION \
     *
     * METHODS RERUNS THE CREATED THE TILE(RELATIVE LAYOUT
     * */
    public static SliderLayout createChannel(BaseActivity parent, int position, ChannelModel mTile) {

        int deviceHeight = Utils.getDeviceHeight(parent);
        int deviceWidth = Utils.getDeviceWidth(parent);

        String orientation = Preferences.getStringSharedPref(parent, Preferences.PREF_KEY_ORIENTATION);
        int refHeight = DeviceInfo.getReferenceHeight(orientation);
        int refWidth = DeviceInfo.getReferenceWidth(orientation);

       /*TILE HEIGHT AND WIDTHS*/
        double tileHeight = mTile.getChannelHeight();
        double tileWidth = mTile.getChannelWidth();

        /*TILE LEFT  AND TOP MARGINS*/
        double tileLeftMar = mTile.getChannelLeftMargin();
        double tileTopMar = mTile.getChannelTopMargin();

        /* TILE HEIGHT AND WIDTH  CALCULATION*/
        int reqWidth = (int) (deviceWidth * tileWidth) / refWidth;
        int reqHeight = (int) (deviceHeight * tileHeight) / refHeight;

        /* TILE LEFT  AND TOP MARGINS  CALCULATION*/
        int reqLeftMar = (int) (deviceWidth * tileLeftMar) / refWidth;
        int reqTopMargin = (int) (deviceHeight * tileTopMar) / refHeight;

        RelativeLayout.LayoutParams paramsTile = new RelativeLayout.LayoutParams(reqWidth, reqHeight);
        paramsTile.setMargins(reqLeftMar, reqTopMargin, 0, 0);

        /*CREATING THE TILE WITH CALCULATED PARAMS */
        SliderLayout rl_tile = new SliderLayout(parent);
        rl_tile.setId((position));
        rl_tile.setLayoutParams(paramsTile);

        rl_tile.setBackgroundColor(Utils.getColor(parent, R.color.colorAccent));
        rl_tile.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        return rl_tile;
    }


    public static TextView addChannelBottomLabel(BaseActivity parent, SliderLayout layout) {

        TextView textView = new TextView(parent);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(10);
        textView.setPadding(10, 10, 10, 10);

        SliderLayout.LayoutParams params = new SliderLayout.LayoutParams(SliderLayout.LayoutParams.WRAP_CONTENT,
                SliderLayout.LayoutParams.WRAP_CONTENT);

        // Align bottom-right, and add bottom-margin
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        // params.bottomMargin = 10;

        textView.setLayoutParams(params);

        // Add the custom layout to your parent layout
        layout.addView(textView);

        return textView;
    }

}
