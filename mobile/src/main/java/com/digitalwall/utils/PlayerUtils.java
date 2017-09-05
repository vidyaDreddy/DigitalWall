package com.digitalwall.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalwall.database.AssetsSource;
import com.digitalwall.slidertypes.BaseSliderView;
import com.digitalwall.R;
import com.digitalwall.Tricks.ViewPagerEx;
import com.digitalwall.activities.PlayerActivity;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.slidertypes.SlideView;
import com.digitalwall.views.SliderLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 15/08/17.
 */

public class PlayerUtils {


    public static void setLiveData(PlayerActivity parent, RelativeLayout rl_main, CampaignModel model) {

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

    public static void setAutoCampaignPlayerData(PlayerActivity parent, RelativeLayout rl_main,
                                                 CampaignModel campaignModel) {
        rl_main.removeAllViews();

        ArrayList<ChannelModel> channelList = campaignModel.getChannelList();
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
    private static void initilizeViewPagerWithAssets(final PlayerActivity parent,
                                                     final SliderLayout sl_layout,
                                                     ChannelModel channel) {

        /*BOTTOM TEXT LABEL*/
        final TextView tv_label = ChannelUtils.addChannelBottomLabel(parent, sl_layout);
        tv_label.setText("CAMPAIGN RUNNING");
        tv_label.setBackgroundColor(Utils.getColor(parent, R.color.black_over_lay));
        tv_label.setVisibility(View.GONE);

        /*ASSIGNING THE ASSETS TO THE CHANNEL*/
        final ArrayList<AssetsModel> mList = channel.getAssetsList();
        if (mList != null && mList.size() > 0) {

            for (int i = 0; i < mList.size(); i++) {
                AssetsModel model = mList.get(i);
                SlideView slide = createSlideView(parent, model);
                sl_layout.addSlider(slide);
            }

            //SET SLIDE DURATION AND ANIMATION TO THE  ONLY 1ST ASSET/*
            sl_layout.setDuration(mList.get(0).getAssetDuration());
            sl_layout.setPresetTransformer(getAnimation(mList.get(0).getAsset_animation()));

            sl_layout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }


                @Override
                public void onPageSelected(int position) {

                    AssetsModel asset = mList.get(position);
                    sl_layout.setDuration(asset.getAssetDuration());
                    sl_layout.setPresetTransformer(getAnimation(asset.getAsset_animation()));

               /* JSONObject object = new JSONObject();
                try {
                    object.put("registrationID", parent.display_key);
                    object.put("Message", "Hi");
                    //parent.setDateToWebSocket(object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                    //parent.setDateToWebSocket("");
                    //sl_layout.setPresetTransformer(mList.get(position).getAssetAnimation());


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


    private static SlideView createSlideView(PlayerActivity parent, AssetsModel model) {

        SlideView slide = new SlideView(parent, model);
        slide.setSlideType(model.getAssetType());
        slide.setPicasso(Picasso.with(parent));
        slide.image(model.getAssetUrl());

        if (!Utils.isValueNullOrEmpty(model.getAsset_local_url()))
            slide.imageFile(new File(model.getAsset_local_url()));

        return slide;

    }


    private static SliderLayout.Transformer getAnimation(String anim) {
        SliderLayout.Transformer mTransformer;

        switch (anim) {
            case "Accordion":
                mTransformer = SliderLayout.Transformer.Accordion;
                break;
            case "Background2Foreground":
                mTransformer = SliderLayout.Transformer.Background2Foreground;
                break;
            case "CubeIn":
                mTransformer = SliderLayout.Transformer.CubeIn;
                break;
            case "DepthPage":
                mTransformer = SliderLayout.Transformer.DepthPage;
                break;
            case "Fade":
                mTransformer = SliderLayout.Transformer.Fade;
                break;
            case "FlipHorizontal":
                mTransformer = SliderLayout.Transformer.FlipHorizontal;
                break;
            case "FlipPage":
                mTransformer = SliderLayout.Transformer.FlipPage;
                break;
            case "Foreground2Background":
                mTransformer = SliderLayout.Transformer.Foreground2Background;
                break;
            case "RotateDown":
                mTransformer = SliderLayout.Transformer.RotateDown;
                break;
            case "RotateUp":
                mTransformer = SliderLayout.Transformer.RotateUp;
                break;
            case "Stack":
                mTransformer = SliderLayout.Transformer.Stack;
                break;
            case "Tablet":
                mTransformer = SliderLayout.Transformer.Tablet;
                break;
            case "ZoomIn":
                mTransformer = SliderLayout.Transformer.ZoomIn;
                break;
            case "ZoomOutSlide":
                mTransformer = SliderLayout.Transformer.ZoomOutSlide;
                break;
            case "ZoomOut":
                mTransformer = SliderLayout.Transformer.ZoomOut;
                break;
            default:
                mTransformer = SliderLayout.Transformer.Default;
                break;
        }

        return mTransformer;
    }
}
