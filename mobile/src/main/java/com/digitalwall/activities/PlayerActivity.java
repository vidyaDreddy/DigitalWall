package com.digitalwall.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalwall.R;
import com.digitalwall.database.AssetsSource;
import com.digitalwall.database.CampaignSource;
import com.digitalwall.database.ChannelSource;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.model.ScheduleDateModel;
import com.digitalwall.model.ScheduleModel;
import com.digitalwall.scheduler.Job;
import com.digitalwall.scheduler.SmartScheduler;
import com.digitalwall.services.ApiConfiguration;
import com.digitalwall.services.JSONResult;
import com.digitalwall.services.JSONTask;
import com.digitalwall.utils.DeviceInfo;
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
import java.util.concurrent.ExecutionException;


@SuppressWarnings("deprecation")
@SuppressLint("SetTextI18n")
public class PlayerActivity extends BaseActivity implements JSONResult, SmartScheduler.JobScheduledCallback {

    private JSONTask getChannelListInfo;

    public WebSocketClient webSocketClient;

    private TextView tv_display_key;
    private RelativeLayout rl_main;
    public String display_key;

    private SmartScheduler jobScheduler;

    private ProgressDialog progressBar;

    private CampaignSource campaignDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        campaignDB = new CampaignSource(this);

        jobScheduler = SmartScheduler.getInstance(this);

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait player is configuring.");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        display_key = ApiConfiguration.getAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID);

        /*MAIN LAYOUT*/
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_main.setVisibility(View.GONE);

        /*DISPLAY KEY TEXT_VIEW*/
        tv_display_key = (TextView) findViewById(R.id.tv_display_key);
        tv_display_key.setVisibility(View.GONE);

        /*CHECK FOR THE PLAYER INFO*/
        String autoCampaignId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID);
        if (!Utils.isValueNullOrEmpty(autoCampaignId)) {
            if (campaignDB.isCampaignDataAvailable(autoCampaignId)) {
                createCampaignPlayer(autoCampaignId);
            } else {
                getAutoCampaignChannelInfo(autoCampaignId);
            }

            //getScheduleCampaignInfo("dMXzsMOpp", "2jaew-bpM");
        } else {
            /*DISPLAY ID*/
            tv_display_key.setVisibility(View.VISIBLE);
            tv_display_key.setText("Display ID: " + display_key);
        }

        /*CONNECT WITH WEB SOCKET*/
        connectWithWebSocket(display_key);

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
                            configureAutoCampaignPlayer(jObject);
                        }

                        /*SCHEDULE CAMPAIGN*/
                        else if (type.equalsIgnoreCase("SCHEDULECREATED")) {
                            configureScheduleCampaignPlayer(jObject);
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

    private void configureScheduleCampaignPlayer(JSONObject jObject) throws JSONException {

        /*CAMPAIGN ID*/
        String scheduleId = jObject.getString("scheduleID");

        /*CLIENT ID*/
        String clientID = jObject.getString("clientID");

        /*GET CHANNEL INFO FOR SCHEDULE CAMPAIGN*/
        getScheduleCampaignInfo(clientID, scheduleId);
    }


    private void configureAutoCampaignPlayer(JSONObject jObject) throws JSONException {

        /*CAMPAIGN ID*/
        String campaignId = jObject.getString("campID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID, campaignId);

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

        runOnUiThread(new Runnable() {
            public void run() {
                progressBar.show();
            }
        });

        if (getChannelListInfo != null)
            getChannelListInfo.cancel(true);

        getChannelListInfo = new JSONTask(this);
        getChannelListInfo.setMethod(JSONTask.METHOD.GET);
        getChannelListInfo.setCode(ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE);
        getChannelListInfo.setHeader("clientID", "DEFAULT");

        String url = String.format(ApiConfiguration.GET_AUTO_CAMPAIGN_INFO, campaignId) +
                ApiConfiguration.OUTPUT_FILTER;
        getChannelListInfo.setServerUrl(url);

        getChannelListInfo.setErrorMessage(ApiConfiguration.ERROR_RESPONSE_CODE);
        getChannelListInfo.setConnectTimeout(ApiConfiguration.TIMEOUT);
        getChannelListInfo.execute();

    }

    private void getScheduleCampaignInfo(String clientId, String campaignId) {

        if (getChannelListInfo != null)
            getChannelListInfo.cancel(true);

        getChannelListInfo = new JSONTask(this);
        getChannelListInfo.setMethod(JSONTask.METHOD.GET);
        getChannelListInfo.setCode(ApiConfiguration.GET_SCHEDULE_INFO_CODE);
        getChannelListInfo.setHeader("clientID", clientId);

        String url = String.format(ApiConfiguration.GET_SCHEDULE_INFO, campaignId) +
                ApiConfiguration.OUTPUT_FILTER;
        getChannelListInfo.setServerUrl(url);

        getChannelListInfo.setErrorMessage(ApiConfiguration.ERROR_RESPONSE_CODE);
        getChannelListInfo.setConnectTimeout(ApiConfiguration.TIMEOUT);
        getChannelListInfo.execute();

    }


    private void getScheduleChannelInfo(String clientId, String campaignId) {

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

                    saveTheData(model);
                } else {
                    tv_display_key.setVisibility(View.VISIBLE);
                    rl_main.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

         /*GET THE SCHEDULE CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_SCHEDULE_INFO_CODE) {
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    ScheduleModel model = new ScheduleModel(jObject);
                    initilizeScheduleCampaign(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

         /*GET THE  CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    CampaignModel model = new CampaignModel(jObject);
                    PlayerUtils.setLiveData(PlayerActivity.this, rl_main, model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    private void initilizeScheduleCampaign(ScheduleModel model) {

        String campaignId = model.getScheduleCampaignId();
        String clientId = model.getScheduleClientId();

        ArrayList<ScheduleDateModel> mList = model.getmScheduleList();
        for (int i = 0; i < mList.size(); i++) {
            ScheduleDateModel date = mList.get(i);

            /*SCHEDULE THE EVENT WITH START TIME*/
            Calendar sCal = date.getsDate();
            int sJobId = DeviceInfo.randomJobId();
            setSchedulerPlayer(sJobId, campaignId, clientId,
                    Preferences.CAMPAIGN_SCHEDULE, sCal.getTimeInMillis());

             /*SCHEDULE THE EVENT WITH END TIME*/
            Calendar eCal = date.geteDate();
            int eJobId = DeviceInfo.randomJobId();
            setSchedulerPlayer(eJobId, campaignId, clientId,
                    Preferences.CAMPAIGN_AUTO, eCal.getTimeInMillis());
        }

    }


    private void saveTheData(CampaignModel model) {

        campaignDB.insertData(model);

        if (model.getChannelList().size() > 0) {

            for (int i = 0; i < model.getChannelList().size(); i++) {

                ChannelModel channelModel = model.getChannelList().get(i);

                ChannelSource channelDB = new ChannelSource(this);
                channelDB.insertData(channelModel, model.getCampaignId());

                if (channelModel.getAssetsList().size() > 0) {

                    for (int j = 0; j < channelModel.getAssetsList().size(); j++) {

                        AssetsModel asset = channelModel.getAssetsList().get(j);
                        asset.setChannel_id(channelModel.getChannelId());
                        try {
                            DownloadFileFromURL task = new DownloadFileFromURL(this, asset);
                            task.execute();
                            String file_url = task.get();
                            asset.setAsset_local_url(file_url);

                            AssetsSource assetsSource = new AssetsSource(this);
                            assetsSource.insertData(asset);

                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

        if (progressBar != null)
            progressBar.dismiss();


        createCampaignPlayer(model.getCampaignId());
    }


    private void createCampaignPlayer(String campaignId) {

        CampaignModel campaignModel = campaignDB.getCampaignByCampaignId(campaignId);

        /*SET THE CHANNEL LIST INFO*/
        ChannelSource channelDB = new ChannelSource(this);
        ArrayList<ChannelModel> mChannelList = channelDB.selectAllChannelByCampaign(campaignId);
        campaignModel.setChannelList(mChannelList);

        /*SET THE ASSET LIST INFO*/
        AssetsSource assetsDB = new AssetsSource(this);
        for (int i = 0; i < mChannelList.size(); i++) {
            ChannelModel channel = mChannelList.get(i);
            ArrayList<AssetsModel> mAssetsList = assetsDB.getAssetListByChannelId(channel.getChannelId());
            channel.setAssetsList(mAssetsList);
        }

        if (campaignModel.getChannelList().size() > 0) {

            tv_display_key.setVisibility(View.GONE);
            rl_main.setVisibility(View.VISIBLE);
            PlayerUtils.setAutoCampaignPlayerData(this, rl_main, campaignModel);
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


    private void setSchedulerPlayer(int JOB_ID, String campaignId, String clientId, String tag, long time) {

        // Check if any periodic job is currently scheduled
        if (jobScheduler.contains(JOB_ID)) {
            jobScheduler.removeJob(JOB_ID);
            return;
        }

        /*JOB CREATED*/
        Job.Builder builder = new Job.Builder(JOB_ID, campaignId, this, tag, clientId)
                .setIntervalMillis(time);

        Job job = builder.build();
        jobScheduler.addJob(job);
    }

    @Override
    public void onJobScheduled(Context context, Job job) {

        if (job != null) {

            String campaignId = job.getJobCampaignId();
            String clientId = job.getJobClientId();

            String type = job.getPeriodicTaskTag();
            if (type.equals(Preferences.CAMPAIGN_SCHEDULE)) {
                getScheduleChannelInfo(clientId, campaignId);
            } else {
                createCampaignPlayer(campaignId);
            }

        }
    }

    @Override
    /*DISABLED THE BACK PRESS */
    public void onBackPressed() {

    }

}
