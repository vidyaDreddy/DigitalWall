package com.digitalwall.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bizooku.SliderTypes.BaseSliderView;
import com.bizooku.SliderTypes.DefaultSliderView;
import com.digitalwall.Indicators.PagerIndicator;
import com.digitalwall.R;
import com.digitalwall.Tricks.ViewPagerEx;
import com.digitalwall.activities.PlayerActivity;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.views.SliderLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 15/08/17.
 */

public class PlayerUtils {


    public static void setData(PlayerActivity parent, RelativeLayout rl_main, CampaignModel model) {

        rl_main.removeAllViews();

        /*SAVE THE LAYOUT ORIENTATION */
        String orientation = model.getLayoutOrientation();
        Preferences.setStringSharedPref(parent, Preferences.PREF_KEY_ORIENTATION, orientation);
        DeviceInfo.setDeviceOrientation(parent, orientation);

        ArrayList<ChannelModel> channelList = model.getChannelList();
        if (channelList.size() > 0) {
            for (int i = 0; i < channelList.size(); i++) {
                /*CREATE THE CHANNEL*/
                ChannelModel channel = channelList.get(i);
                SliderLayout sl_layout = ChannelUtils.createChannel(parent, i, channel);
                initilizeViewPagerWithAssets(parent, sl_layout, channel);
                rl_main.addView(sl_layout);

            }
        }

    }


    @SuppressLint("SetTextI18n")
    private static void initilizeViewPagerWithAssets(PlayerActivity parent, final SliderLayout sl_layout,
                                                     ChannelModel channel) {

        /*BOTTOM TEXT LABEL*/
        final TextView tv_label = ChannelUtils.addChannelBottomLabel(parent, sl_layout);
        tv_label.setText("CAMPAIGN RUNNING");
        tv_label.setBackgroundColor(Utils.getColor(parent, R.color.black_over_lay));


        /*ASSIGNING THE ASSETS TO THE CHANNEL*/
        final ArrayList<AssetsModel> mList = channel.getAssetsList();
        for (int i = 0; i < mList.size(); i++) {
            AssetsModel model = mList.get(i);

            DefaultSliderView textSliderView = new DefaultSliderView(parent);
            textSliderView.setPicasso(Picasso.with(parent));
            textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);

            String mediaType = model.getAssetType();
            if (!Utils.isValueNullOrEmpty(mediaType) && mediaType.equalsIgnoreCase("video")) {
                textSliderView.image(R.drawable.icon_no_video);
            } else {
                textSliderView.image(model.getAssetUrl());
            }
            sl_layout.addSlider(textSliderView);


            /*SET SLIDE DURATION AND ANIMATION TO THE  ONLY 1ST ASSET*/
            if (mList.size() > 0) {
                sl_layout.setDuration(mList.get(0).getAssetDuration());

                JSONObject object = new JSONObject();
                try {
                    object.put("registrationID", parent.display_key);
                    object.put("Message", "Hi");
                    //parent.setDateToWebSocket(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //sl_layout.setPresetTransformer(mList.get(0).getAssetAnimation());
            }
        }


        sl_layout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(int position) {
                if (mList.size() > 0) {
                    sl_layout.setDuration(mList.get(position).getAssetDuration());
                    //parent.setDateToWebSocket("");
                    //sl_layout.setPresetTransformer(mList.get(position).getAssetAnimation());
                }

               /* if (position == 0) {
                    tv_label.setText("CAMPAIGN FINISHED");
                    sl_layout.stopAutoCycle();
                    sl_layout.moveNextPosition();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
