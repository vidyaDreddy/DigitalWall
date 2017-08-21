package com.digitalwall.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalwall.slidertypes.BaseSliderView;
import com.digitalwall.slidertypes.DefaultSliderView;
import com.digitalwall.Indicators.PagerIndicator;
import com.digitalwall.R;
import com.digitalwall.Tricks.ViewPagerEx;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.services.ApiConfiguration;
import com.digitalwall.services.JSONResult;
import com.digitalwall.services.JSONTask;
import com.digitalwall.utils.ChannelUtils;
import com.digitalwall.utils.DeviceInfo;
import com.digitalwall.utils.Preferences;
import com.digitalwall.utils.Utils;
import com.digitalwall.views.SliderLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends BaseActivity implements JSONResult {

    private JSONTask getChannelListInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        getChannelInfo();
    }

    private void getChannelInfo() {

        if (getChannelListInfo != null)
            getChannelListInfo.cancel(true);

        getChannelListInfo = new JSONTask(this);
        getChannelListInfo.setMethod(JSONTask.METHOD.GET);
        getChannelListInfo.setCode(ApiConfiguration.GET_CAMPAIGN_INFO_CODE);
        getChannelListInfo.setHeader("clientID", "ne3pCn3MM");

        String url = String.format(ApiConfiguration.GET_CAMPAIGN_INFO, "cWfOGEFMM") + ApiConfiguration.OUTPUT_FILTER;
        getChannelListInfo.setServerUrl(url);

        getChannelListInfo.setErrorMessage(ApiConfiguration.ERROR_RESPONSE_CODE);
        getChannelListInfo.setConnectTimeout(ApiConfiguration.TIMEOUT);
        getChannelListInfo.execute();

    }

    @Override
    public void successJsonResult(int code, Object result) {

        if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {

            try {
                JSONObject jObject = (JSONObject) result;
                CampaignModel model = new CampaignModel(jObject);
                setData(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setData(CampaignModel model) {

        /*SAVE THE LAYOUT ORIENTATION */
        String orientation = model.getLayoutOrientation();
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_ORIENTATION, orientation);
        DeviceInfo.setDeviceOrientation(this, orientation);

        ArrayList<ChannelModel> channelList = model.getChannelList();
        if (channelList.size() > 0) {
            RelativeLayout rl_main = (RelativeLayout) findViewById(R.id.rl_main);
            rl_main.removeAllViews();

            for (int i = 0; i < channelList.size(); i++) {
                final ChannelModel channel = channelList.get(i);
                /*CREATE THE CHANNEL*/
                SliderLayout sl_layout = ChannelUtils.createChannel(this, i, channel);
                rl_main.addView(sl_layout);

                TextView tv_label = ChannelUtils.addChannelBottomLabel(this, sl_layout);
                initilizeViewPagerWithAssets(sl_layout, channel, tv_label);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initilizeViewPagerWithAssets(final SliderLayout sl_layout, ChannelModel channel,
                                              final TextView tv_label) {

        sl_layout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        tv_label.setText("CAMPAIGN RUNNING");
        tv_label.setBackgroundColor(Utils.getColor(this, R.color.black_over_lay));

        final ArrayList<AssetsModel> mList = channel.getAssetsList();

        for (int i = 0; i < mList.size(); i++) {
            AssetsModel model = mList.get(i);

            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView.setPicasso(Picasso.with(this));
            textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);

            String mediaType = model.getAssetType();
            if (!Utils.isValueNullOrEmpty(mediaType) && mediaType.equalsIgnoreCase("video")) {
                textSliderView.image(R.drawable.icon_no_video);
            } else {
                textSliderView.image(model.getAssetUrl());
            }
            sl_layout.addSlider(textSliderView);
        }

        if (mList.size() > 0) {
            sl_layout.setDuration(mList.get(0).getAssetDuration());
            //sl_layout.setPresetTransformer(mList.get(0).getAssetAnimation());
        }
        sl_layout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }


            @Override
            public void onPageSelected(int position) {
                if (mList.size() > 0) {
                    sl_layout.setDuration(mList.get(position).getAssetDuration());
                    //sl_layout.setPresetTransformer(mList.get(position).getAssetAnimation());
                }

                if (position == 0) {
                    tv_label.setText("CAMPAIGN FINISHED");
                    sl_layout.stopAutoCycle();
                    sl_layout.moveNextPosition();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void failedJsonResult(int code) {
        if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {
            Log.v("GET CAMPAIGN DETAILS:", "FAILED TO GET THE DETAILS");
        }
    }


    /*private void createLayout(ArrayList<LayoutModel> mLayoutList) {

        RelativeLayout rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_main.removeAllViews();

        for (int i = 0; i < mLayoutList.size(); i++) {
            final LayoutModel layout = mLayoutList.get(i);

            *//*CREATE THE TILE*//*
            SliderLayout sl_layout = createTile(layout, i);
            rl_main.addView(sl_layout);
            initilizeViewPager(sl_layout, layout);
        }
    }


    *//*
   * CRATING THE TILE WITH THE GIVEN DATA AND DYNAMIC DEVICE BASE BASE CALCULATION \
   *
   * METHODS RERUNS THE CREATED THE TILE(RELATIVE LAYOUT
   * *//*
    private SliderLayout createTile(LayoutModel mTile, int position) {

       *//*TILE HEIGHT AND WIDTHS*//*
        double tileHeight = mTile.getLayoutHeight();
        double tileWidth = mTile.getLayoutWidth();

        *//*TILE LEFT  AND TOP MARGINS*//*
        double tileLeftMar = mTile.getLayoutXSize();
        double tileTopMar = mTile.getLayoutYSize();


        *//* TILE HEIGHT AND WIDTH  CALCULATION*//*
        int reqWidth = (int) (deviceWidth * tileWidth) / Utils.REFERENCE_DEVICE_WIDTH;
        int reqHeight = (int) (deviceWidth * tileHeight) / Utils.REFERENCE_DEVICE_WIDTH;

        *//* TILE LEFT  AND TOP MARGINS  CALCULATION*//*
        int reqLeftMar = (int) (deviceWidth * tileLeftMar) / Utils.REFERENCE_DEVICE_WIDTH;
        int reqTopMargin = (int) (deviceWidth * tileTopMar) / Utils.REFERENCE_DEVICE_WIDTH;

        RelativeLayout.LayoutParams paramsTile = new RelativeLayout.LayoutParams(reqWidth, reqHeight);
        paramsTile.setMargins(reqLeftMar, reqTopMargin, 0, 0);


        *//*CREATING THE TILE WITH CALCULATED PARAMS *//*
        SliderLayout rl_tile = new SliderLayout(this);
        rl_tile.setId((position));
        rl_tile.setLayoutParams(paramsTile);

        int color = Color.parseColor(mTile.getLayoutColor());
        rl_tile.setBackgroundColor(color);

        return rl_tile;
    }





    private void initilizeViewPager(final SliderLayout sl_layout, LayoutModel layout) {

        sl_layout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        final ArrayList<TileModel> mList = layout.getaList();


        long total = 0;
        for (int i = 0; i < mList.size(); i++) {
            TileModel model = mList.get(i);
            total += model.getAssetDuration();
            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView.setPicasso(Picasso.with(this));
            textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
            textSliderView.image(model.getAssetUrl());
            sl_layout.addSlider(textSliderView);
        }

        System.out.print(total);

        if (mList.size() > 0) {
            sl_layout.setDuration(mList.get(0).getAssetDuration());
            sl_layout.setPresetTransformer(mList.get(0).getAssetAnimation());
        }
        sl_layout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mList.size() > 0) {
                    sl_layout.setDuration(mList.get(position).getAssetDuration());
                    sl_layout.setPresetTransformer(mList.get(position).getAssetAnimation());
                }

                if (position == 0) {
                    sl_layout.stopAutoCycle();
                    Toast.makeText(DashboardActivity.this, "CAMPAIGN FINISHED", Toast.LENGTH_SHORT).show();
                    sl_layout.moveNextPosition();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }*/


    /*public WebSocketClient webSocketClient;

    private void connectSever(String display_key) {

        String serverUrl = "ws://dev.digitalwall.in/socket/websocket?room=" + display_key;
        try {
            webSocketClient = new WebSocketClient(new URI(serverUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.v("WEB SOCKET ----", "ON OPEN");
                    webSocketClient.send("HI");
                }

                @Override
                public void onMessage(String message) {
                    Log.v("WEB SOCKET ----", "ON MESSAGE" + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.v("WEB SOCKET ----", "ON CLOSE" + reason);
                }

                @Override
                public void onError(Exception ex) {
                    Log.v("WEB SOCKET ----", "ON ERROR" + ex.toString());
                }
            };
            webSocketClient.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
*/

}
