package com.digitalwall.utils;

import android.graphics.Color;
import android.net.ParseException;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalwall.Indicators.PagerIndicator;
import com.digitalwall.R;
import com.digitalwall.activities.BaseActivity;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.model.ScheduleModel;
import com.digitalwall.views.SliderLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 01/08/17.
 */

public class ChannelUtils {

    public static ArrayList<ScheduleModel> getScheduleCampaignModelList
            (JSONObject jsonObject) throws JSONException {

        ArrayList<ScheduleModel> scheduleModels = new ArrayList<>();
        JSONArray mScheduleJsonArray = jsonObject.optJSONArray("schedules");

        for (int i = 0; i < mScheduleJsonArray.length(); i++) {
            JSONObject mScheduleCampaignModelJson = mScheduleJsonArray.optJSONObject(i);

            mScheduleCampaignModelJson.put("_id", jsonObject.optString("_id"));
            mScheduleCampaignModelJson.put("campaignId", jsonObject.optString("campaignId"));
            mScheduleCampaignModelJson.put("startDate", mScheduleCampaignModelJson.optString("startDate"));
            mScheduleCampaignModelJson.put("endDate", mScheduleCampaignModelJson.optString("endDate"));
            mScheduleCampaignModelJson.put("sTime", mScheduleCampaignModelJson.optString("startTime"));
            mScheduleCampaignModelJson.put("eTime", mScheduleCampaignModelJson.optString("endTime"));
            mScheduleCampaignModelJson.put("jobId", DateUtils.getCalendarDate(mScheduleCampaignModelJson.optString("startDate"),
                    mScheduleCampaignModelJson.optString("startTime")).getTimeInMillis());
            mScheduleCampaignModelJson.put("startTime", DateUtils.
                    convertTimeToSeconds(mScheduleCampaignModelJson.optString("startTime")));
            mScheduleCampaignModelJson.put("endTime", DateUtils.
                    convertTimeToSeconds(mScheduleCampaignModelJson.optString("endTime")));

            ScheduleModel scheduleModel = new ScheduleModel(mScheduleCampaignModelJson);

            scheduleModels.add(scheduleModel);

        }
        return scheduleModels;
    }


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
        //rl_tile.setId((Integer.parseInt(mTile.getChannelId())));
        rl_tile.setId((position));
        rl_tile.setLayoutParams(paramsTile);

        try {
            String color = mTile.getChannelColor();
            rl_tile.setBackgroundColor(Color.parseColor(color));
        } catch (ParseException e) {
            e.printStackTrace();
            rl_tile.setBackgroundColor(Utils.getColor(parent, R.color.colorPrimaryDark));
        }
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
