package com.digitalwall.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalwall.R;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.scheduler.Job;
import com.digitalwall.scheduler.SmartScheduler;
import com.digitalwall.services.ApiConfiguration;
import com.digitalwall.services.JSONResult;
import com.digitalwall.services.JSONTask;
import com.digitalwall.utils.DownloadFileFromURL;
import com.digitalwall.utils.PlayerUtils;
import com.digitalwall.utils.Preferences;
import com.digitalwall.utils.Utils;
import com.mixpanel.android.java_websocket.client.WebSocketClient;
import com.mixpanel.android.java_websocket.handshake.ServerHandshake;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;


@SuppressLint("SetTextI18n")
public class PlayerActivity extends BaseActivity implements JSONResult, SmartScheduler.JobScheduledCallback {

    private JSONTask getChannelListInfo;

    public WebSocketClient webSocketClient;

    private TextView tv_display_key;
    private RelativeLayout rl_main;
    public String display_key;


    SmartScheduler jobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        jobScheduler = SmartScheduler.getInstance(this);


        display_key = ApiConfiguration.getAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID);

        /*MAIN LAYOUT*/
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_main.setVisibility(View.GONE);

        /*DISPLAY KEY TEXT_VIEW*/
        tv_display_key = (TextView) findViewById(R.id.tv_display_key);
        tv_display_key.setVisibility(View.GONE);

        /*CHECK FOR THE PLAYER INFO*/
        String campaignId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_CAMPAIGN_ID);
        if (!Utils.isValueNullOrEmpty(campaignId)) {
            getAutoCampaignChannelInfo(campaignId);
        } else {
            /*DISPLAY ID*/
            tv_display_key.setVisibility(View.VISIBLE);
            tv_display_key.setText("Display ID: " + display_key);
        }

        /*CONNECT WITH WEB SOCKET*/
        connectWithWebSocket(display_key);

        /*SCHEDULER TASK*/
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 7);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 29);
        calendar.set(Calendar.SECOND, 0);
        setSchedulerPlayer(100, calendar.getTimeInMillis());
    }

    private void connectWithWebSocket(String display_key) {

        String serverUrl = "ws://dev.digitalwall.in/socket/websocket?room=" + display_key;

        try {
            webSocketClient = new WebSocketClient(new URI(serverUrl)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.v("WEB SOCKET ----", "ON OPEN");
                }

                @Override
                public void onMessage(String message) {
                    Log.v("WEB SOCKET ----", "ON MESSAGE" + message);
                    try {
                        JSONObject jObject = new JSONObject(message);

                        /*CHECK FOR THE PAYLOAD TYPE*/
                        String type = jObject.optString("type");
                        if (type.equalsIgnoreCase("PLAYERCREATED")) {
                            configureAPlayer(jObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public void setDateToWebSocket(String message) {

        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }


    private void configureAPlayer(JSONObject jObject) throws JSONException {

        /*CAMPAIGN ID*/
        String campaignId = jObject.getString("campID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_CAMPAIGN_ID, campaignId);

        /*CLIENT ID*/
        String clientID = jObject.getString("clientID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_CLIENT_ID, clientID);

        /*VOLUME*/
        int deviceVolume = jObject.getInt("volume");
        Preferences.setIntSharedPref(this, Preferences.PREF_KEY_VOLUME, deviceVolume);

        /*CLIENT ID*/
        String deviceOrientation = jObject.getString("orientation");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_ORIENTATION, deviceOrientation);


        /*GET CHANNEL INFO FOR AUTO CAMPAIGN*/
        getAutoCampaignChannelInfo(campaignId);
    }

    private void getAutoCampaignChannelInfo(String campaignId) {

        if (getChannelListInfo != null)
            getChannelListInfo.cancel(true);

        getChannelListInfo = new JSONTask(this);
        getChannelListInfo.setMethod(JSONTask.METHOD.GET);
        getChannelListInfo.setCode(ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE);
        getChannelListInfo.setHeader("clientID", "DEFAULT");

        String url = String.format(ApiConfiguration.GET_CAMPAIGN_INFO, campaignId) +
                ApiConfiguration.OUTPUT_FILTER;
        getChannelListInfo.setServerUrl(url);

        getChannelListInfo.setErrorMessage(ApiConfiguration.ERROR_RESPONSE_CODE);
        getChannelListInfo.setConnectTimeout(ApiConfiguration.TIMEOUT);
        getChannelListInfo.execute();

    }


    private void getChannelInfo(String clientId, String campaignId) {

        if (getChannelListInfo != null)
            getChannelListInfo.cancel(true);

        getChannelListInfo = new JSONTask(this);
        getChannelListInfo.setMethod(JSONTask.METHOD.GET);
        getChannelListInfo.setCode(ApiConfiguration.GET_CAMPAIGN_INFO_CODE);
        getChannelListInfo.setHeader("clientID", clientId);

        String url = String.format(ApiConfiguration.GET_CAMPAIGN_INFO, campaignId) +
                ApiConfiguration.OUTPUT_FILTER;
        getChannelListInfo.setServerUrl(url);

        getChannelListInfo.setErrorMessage(ApiConfiguration.ERROR_RESPONSE_CODE);
        getChannelListInfo.setConnectTimeout(ApiConfiguration.TIMEOUT);
        getChannelListInfo.execute();

    }

    @Override
    public void successJsonResult(int code, Object result) {

        /*GET THE  AUTO CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE) {
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    tv_display_key.setVisibility(View.GONE);
                    rl_main.setVisibility(View.VISIBLE);
                    CampaignModel model = new CampaignModel(jObject);
                    PlayerUtils.setData(PlayerActivity.this, rl_main, model);

                    //saveTheData(model);
                } else {
                    tv_display_key.setVisibility(View.VISIBLE);
                    rl_main.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


         /*GET THE  CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {

        }


    }


    private void saveTheData(CampaignModel model) {

        if (model.getChannelList().size() > 0) {

            for (int i = 0; i < model.getChannelList().size(); i++) {
                ChannelModel model1 = model.getChannelList().get(i);

                if (model1.getAssetsList().size() > 0) {
                    for (int j = 0; j < model1.getAssetsList().size(); j++) {
                        AssetsModel asset = model1.getAssetsList().get(j);
                        new DownloadFileFromURL(this, asset).execute();
                    }

                }

            }

        }


    }


    @Override
    public void failedJsonResult(int code) {

        /*GET THE AUTO CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE) {
            Log.v("AUTO CAMPAIGN:", "FAILED TO GET THE DETAILS");
        }

        /*GET THE CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {
            Log.v("CAMPAIGN:", "FAILED TO GET THE DETAILS");
        }
    }


    private void setSchedulerPlayer(int JOB_ID, long time) {

        // Check if any periodic job is currently scheduled
        if (jobScheduler.contains(JOB_ID)) {
            jobScheduler.removeJob(JOB_ID);
            return;
        }

        /*JOB CREATED*/
        Job.Builder builder = new Job.Builder(JOB_ID, this, Job.Type.JOB_TYPE_ALARM, "TAG")
                .setIntervalMillis(time);

        Job job = builder.build();
        jobScheduler.addJob(job);
    }


    @Override
    /*DISABLED THE BACK PRESS */
    public void onBackPressed() {

    }

    @Override
    public void onJobScheduled(Context context, Job job) {
        if (job != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });

        }
    }
}
