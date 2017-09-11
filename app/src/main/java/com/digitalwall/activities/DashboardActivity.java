package com.digitalwall.activities;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalwall.R;
import com.digitalwall.database.AssetsSource;
import com.digitalwall.database.CampaignSource;
import com.digitalwall.database.ChannelSource;
import com.digitalwall.database.ScheduleDb;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.digitalwall.model.ScheduleModel;
import com.digitalwall.scheduler.Job;
import com.digitalwall.scheduler.SmartScheduler;
import com.digitalwall.services.ApiConfiguration;
import com.digitalwall.services.JSONResult;
import com.digitalwall.services.JSONTask;
import com.digitalwall.utils.AssetUtils;
import com.digitalwall.utils.ChannelUtils;
import com.digitalwall.utils.DateUtils;
import com.digitalwall.utils.Downloader;
import com.digitalwall.utils.PlayerUtils;
import com.digitalwall.utils.Preferences;
import com.digitalwall.utils.Utils;
import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.mixpanel.android.java_websocket.client.WebSocketClient;
import com.mixpanel.android.java_websocket.handshake.ServerHandshake;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.tonyodev.fetch.Fetch;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;


public class DashboardActivity extends BaseActivity implements JSONResult,
        SmartScheduler.JobScheduledCallback {

    private JSONTask getChannelListInfo;
    public WebSocketClient webSocketClient;

    private BusWrapper busWrapper;
    private NetworkEvents networkEvents;

    public LinearLayout ll_display_key;
    public RelativeLayout rl_main;
    public TextView tv_display_key;
    public TextView tv_reg_note;


    private AudioManager mAudioManager;
    private SmartScheduler jobScheduler;
    public Fetch fetch;

    private ArrayList<AssetsModel> assetList;

    public String display_key;
    public String clientId;
    public String autoCampaignId;
    public String orientation;

    private int deviceVolume;

    public CampaignSource campaignSource;
    public ChannelSource channelSource;
    public AssetsSource assetsSource;
    public ScheduleDb scheduleSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        campaignSource = new CampaignSource(DashboardActivity.this);
        channelSource = new ChannelSource(DashboardActivity.this);
        assetsSource = new AssetsSource(DashboardActivity.this);
        scheduleSource = new ScheduleDb(DashboardActivity.this);


        clientId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_CLIENT_ID);
        autoCampaignId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID);
        deviceVolume = Preferences.getIntSharedPref(this, Preferences.PREF_KEY_VOLUME);
        display_key = ApiConfiguration.getAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID);


        fetch = Fetch.newInstance(this);
        fetch.setAllowedNetwork(Fetch.NETWORK_ALL);
        // fetch.addFetchListener(this);

        jobScheduler = SmartScheduler.getInstance(this);

        busWrapper = Utils.getOttoBusWrapper(new Bus());
        networkEvents = new NetworkEvents(this, busWrapper).enableInternetCheck().enableWifiScan();


        initilizeView();

        /*CONNECT WITH WEB SOCKET*/
        connectWithWebSocket();

    }

    @Override
    protected void onResume() {
        super.onResume();
        busWrapper.register(this);
        networkEvents.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        busWrapper.unregister(this);
        networkEvents.unregister();
    }

    @Subscribe
    public void onEvent(ConnectivityChanged event) {
        ConnectivityStatus wifiStatus = event.getConnectivityStatus();
        if (wifiStatus == ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET ||
                wifiStatus == ConnectivityStatus.MOBILE_CONNECTED) {
            if (webSocketClient != null && webSocketClient.isClosed())
                connectWithWebSocket();

            /*DOWNLOAD ASSETS*/
            downloadAssetsInBackground();
        }
    }

    /*ALL ASSETS DOWNLOADED IN BACKGROUND*/
    private void downloadAssetsInBackground() {
        assetList = assetsSource.selectAll();
        if (assetList != null && assetList.size() > 0) {
            Downloader downloader = new Downloader(this, assetList);
            downloader.setAssetDownloader();
        }
    }


    private void initilizeView() {

        /*SET DEVICE VOLUME*/
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, deviceVolume, 0);

        /*MAIN LAYOUT*/
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_main.setVisibility(View.GONE);

        ll_display_key = (LinearLayout) findViewById(R.id.ll_display_key);
        ll_display_key.setVisibility(View.GONE);

        /*DIGITAL WALL  LABEL*/
        TextView tv_digital_wall_label = (TextView) findViewById(R.id.tv_digital_wall_label);
        tv_digital_wall_label.setTypeface(Utils.setRobotoTypeface(this));
        tv_digital_wall_label.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.text_font_label));

        /*DISPLAY KEY*/
        tv_display_key = (TextView) findViewById(R.id.tv_display_key);
        tv_display_key.setTypeface(Utils.setRobotoTypeface(this));
        tv_display_key.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_font));

        tv_reg_note = (TextView) findViewById(R.id.tv_reg_note);
        tv_reg_note.setTypeface(Utils.setRobotoTypeface(this));
        tv_reg_note.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().
                getDimension(R.dimen.text_font_small));
        tv_reg_note.setText(Utils.getStrings(this, R.string.txt_reg_note));


        ArrayList<ScheduleModel> mSchList = scheduleSource.getAllScheduleList();
        if (mSchList != null && mSchList.size() > 0) {

            //DELETE THE OLD SCHEDULERS
            int status = scheduleSource.deleteOldSchudules();
            Log.v("SCHEDULER", "DELETE OLD SCHEDULES STATUS:" + status);

            Log.v("SCHEDULER", "SCHEDULE'S LIST" + mSchList.size());
        }


        ScheduleModel scheduleModel = scheduleSource.getCurrentAvailableCampaign();
        if (scheduleModel != null) {

            playAutoCampaignWithSavedData(autoCampaignId);
            setTheJobSchedulerData(scheduleModel);

        } else if (!Utils.isValueNullOrEmpty(autoCampaignId)) {
            Utils.hideRegisterPlayerView(this);
            if (campaignSource.isCampaignDataAvailable(autoCampaignId))
                playAutoCampaignWithSavedData(autoCampaignId);
            else
                getAutoCampaignData(autoCampaignId);
        } else
            Utils.showRegisterPlayerNoteView(this);


    }


    private void connectWithWebSocket() {

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
                        switch (type) {
                            case "PLAYERCREATED":
                                configurePlayer(jObject);
                                break;
                            case "SCHEDULECREATED":
                                configureScheduleCampaign(jObject);
                                break;
                            case "SCHEDULEDELETED":
                                deleteScheduleCampaign(jObject);
                                break;
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

    private void configurePlayer(JSONObject jObject) throws JSONException {

        /*CAMPAIGN ID*/
        autoCampaignId = jObject.getString("campID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID, autoCampaignId);

        /*CLIENT ID*/
        clientId = jObject.getString("clientID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_CLIENT_ID, clientId);

        /*VOLUME*/
        deviceVolume = jObject.getInt("volume");
        Preferences.setIntSharedPref(this, Preferences.PREF_KEY_VOLUME, deviceVolume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, deviceVolume, 0);

        /*ORIENTATION*/
        orientation = jObject.getString("orientation");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_ORIENTATION, orientation);


        getAutoCampaignData(autoCampaignId);
    }

    private void configureScheduleCampaign(JSONObject jObject) throws JSONException {

        /*SCHEDULE ID*/
        String scheduleId = jObject.getString("scheduleID");

        /*GET CHANNEL INFO FOR SCHEDULE CAMPAIGN*/
        getScheduleData(scheduleId);
    }


    private void deleteScheduleCampaign(JSONObject jObject) throws JSONException {
      /*  String scheduleId = jObject.getString("scheduleID");
        int status = scheduleSource.deleteScheduleById(scheduleId);*/

    }


    private void getAutoCampaignData(String campaignId) {

        runOnUiThread(new Runnable() {
            public void run() {
                Utils.showPlayerSyncView(DashboardActivity.this);
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

    private void getScheduleData(String campaignId) {

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


    private void getScheduleCampaignData(String campaignId) {

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

        /*GET THE AUTO CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE) {
            try {
                Log.v("AUTO CAMPAIGN:", "RESULT :" + result);
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    CampaignModel model = new CampaignModel(jObject);
                    saveAutoCampaignDataInDB(model);
                } else {
                    Log.v("AUTO CAMPAIGN:", "RESULT FAILED :" + status);
                    Utils.showPlayerSyncFailedView(DashboardActivity.this);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Utils.showPlayerSyncFailedView(DashboardActivity.this);
            }
        }

        /*GET THE SCHEDULE INFO*/
        else if (code == ApiConfiguration.GET_SCHEDULE_INFO_CODE) {
            Log.v("SCHEDULE:", "RESULT :" + result);
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {

                    String campaignId = jObject.optString("campaignId");
                    if (!campaignSource.isCampaignDataAvailable(campaignId)) {
                        getScheduleCampaignData(campaignId);
                    }

                    ArrayList<ScheduleModel> scheduleModels = ChannelUtils.
                            getScheduleCampaignModelList(jObject);
                    for (int i = 0; i < scheduleModels.size(); i++) {
                        scheduleSource.insertData(scheduleModels.get(i));
                    }
                    scheduleTheCampaigns();
                } else {
                    Log.v("SCHEDULE:", "RESULT FAILED :" + status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*GET THE SCHEDULE CAMPAIGN INFO*/
        else if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {
            Log.v("CAMPAIGN:", "RESULT :" + result);
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    CampaignModel campaignModel = new CampaignModel(jObject);
                    saveNormalCampaignDataInDB(campaignModel);
                } else {
                    Log.v("CAMPAIGN:", "RESULT FAILED :" + status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void scheduleTheCampaigns() {
        ArrayList<ScheduleModel> mList = scheduleSource.getAllScheduleList();
        if (mList != null && mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                ScheduleModel model = mList.get(i);
                setTheJobSchedulerData(model);
            }
        }
    }


    private void setTheJobSchedulerData(ScheduleModel model) {

        /*SCHEDULE THE EVENT WITH START TIME*/
        Calendar sCal = DateUtils.getCalendarDate(model.getStartDate(), model.getsTime());
        initilizeScheduler(model.getJobid(), model.getCampaignId(),
                Preferences.CAMPAIGN_SCHEDULE,
                sCal.getTimeInMillis(), model.getJobid(), model.getId());

        //*SCHEDULE THE EVENT WITH END TIME*//*
      /*  Calendar eCal = DateUtils.getCalendarDate(model.getEndDate(), model.geteTime());
        int eJobId = model.getJobid() + 12;
        initilizeScheduler(eJobId, autoCampaignId,
                Preferences.CAMPAIGN_AUTO, eCal.getTimeInMillis(), model.getJobid(), model.getId());*/
    }

    private void saveNormalCampaignDataInDB(CampaignModel model) {
        campaignSource.insertData(model);
        if (model.getChannelList().size() > 0)
            for (int i = 0; i < model.getChannelList().size(); i++) {
                ChannelModel channelModel = model.getChannelList().get(i);
                channelSource.insertData(channelModel, model.getCampaignId());
                if (channelModel.getAssetsList().size() > 0) {
                    for (int j = 0; j < channelModel.getAssetsList().size(); j++) {
                        AssetsModel assetsModel = channelModel.getAssetsList().get(j);
                        assetsSource.insertData(assetsModel, channelModel.getChannelId());
                    }
                }
            }
        Log.d("Assets Size", "Assets Size" + assetsSource.selectAll().size());
        downloadAssetsInBackground();
    }


    /**
     * This method is used to save data in the db
     */
    private void saveAutoCampaignDataInDB(CampaignModel model) {

        campaignSource.insertData(model);

        if (model.getChannelList().size() > 0)
            for (int i = 0; i < model.getChannelList().size(); i++) {
                ChannelModel channelModel = model.getChannelList().get(i);
                channelSource.insertData(channelModel, model.getCampaignId());
                if (channelModel.getAssetsList().size() > 0) {
                    for (int j = 0; j < channelModel.getAssetsList().size(); j++) {
                        AssetsModel assetsModel = channelModel.getAssetsList().get(j);
                        assetsSource.insertData(assetsModel, channelModel.getChannelId());
                    }
                }
            }
        Log.d("Assets Size", "Assets Size" + assetsSource.selectAll().size());
        assetList = assetsSource.selectAll();
        if (assetList != null && assetList.size() > 0) {
            AssetUtils util = new AssetUtils(this, model.getCampaignId(), assetList);
            util.setAutoCampaignDownloader();
        }
    }


    public void playAutoCampaignWithSavedData(String campaignId) {

        Utils.hideRegisterPlayerView(DashboardActivity.this);

        CampaignModel campaignModel = campaignSource.getCampaignByCampaignId(campaignId);
        ArrayList<ChannelModel> mChannelList = channelSource.selectAllChannelByCampaign(campaignId);
        if (mChannelList != null && mChannelList.size() > 0) {
            campaignModel.setChannelList(mChannelList);

            for (int i = 0; i < mChannelList.size(); i++) {
                ChannelModel channel = mChannelList.get(i);
                ArrayList<AssetsModel> mAssetsList = assetsSource.getAssetListByChannelId
                        (channel.getChannelId());
                channel.setAssetsList(mAssetsList);
            }
            ArrayList<ChannelModel> cList = campaignModel.getChannelList();
            if (cList != null && cList.size() > 0) {
                ll_display_key.setVisibility(View.GONE);
                rl_main.setVisibility(View.VISIBLE);
                PlayerUtils.setAutoCampaignPlayerData(this, rl_main, campaignModel);
            }
        }
    }


    public void playAScheduleCampaign(String campaignId) {

        CampaignModel campaignModel = campaignSource.getCampaignByCampaignId(campaignId);
        if (campaignModel != null) {

            ArrayList<ChannelModel> mChannelList = channelSource.selectAllChannelByCampaign(campaignId);
            if (mChannelList != null && mChannelList.size() > 0) {
                campaignModel.setChannelList(mChannelList);

                for (int i = 0; i < mChannelList.size(); i++) {
                    ChannelModel channel = mChannelList.get(i);
                    ArrayList<AssetsModel> mAssetsList = assetsSource.getAssetListByChannelId
                            (channel.getChannelId());
                    channel.setAssetsList(mAssetsList);
                }
                PlayerUtils.setAutoCampaignPlayerData(this, rl_main, campaignModel);
            }
        }

    }


    @Override
    public void failedJsonResult(int code) {

        /*GET THE AUTO CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE) {
            Log.v("AUTO CAMPAIGN:", "FAILED TO GET THE DETAILS");
            getAutoCampaignData(autoCampaignId);
        }

        /*GET THE SCHEDULE INFO*/
        else if (code == ApiConfiguration.GET_SCHEDULE_INFO_CODE) {
            Log.v("SCHEDULE:", "FAILED TO GET THE DETAILS");
        }

         /*GET THE SCHEDULE CAMPAIGN INFO*/
        else if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {
            Log.v("CAMPAIGN:", "FAILED TO GET THE DETAILS");
        }
    }


    private void initilizeScheduler(int JOB_ID, String campaignId, String tag,
                                    long time, int JobEId, String scheduleId) {

        // Check if any periodic job is currently scheduled
        if (jobScheduler.contains(JOB_ID)) {
            jobScheduler.removeJob(JOB_ID);
            return;
        }
        String jOBENDID = String.valueOf(JobEId);
        /*JOB CREATED*/
        Job.Builder builder = new Job.Builder(JOB_ID, campaignId, this, tag, jOBENDID, scheduleId)
                .setIntervalMillis(time);

        Job job = builder.build();
        jobScheduler.addJob(job);
    }

    @Override
    public void onJobScheduled(Context context, final Job job) {

        DashboardActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                navigateToScheduleScreen(job);
            }

        });

    }

    private void navigateToScheduleScreen(final Job job) {

        if (job != null) {
            int jobId = job.getJobId();
            int jobRefId = Integer.valueOf(job.getJobClientId());
            String campaignId = job.getJobCampaignId();
            String scheduleId = job.getJobScheduleId();
            String type = job.getPeriodicTaskTag();

            switch (type) {
                case Preferences.CAMPAIGN_AUTO:
                    Log.v("SCHEDULER", "ENDED");

                    playAutoCampaignWithSavedData(autoCampaignId);
                    int status = scheduleSource.deleteScheduleByJobId(jobRefId);
                    if (status == -1)
                        Log.v("SCHEDULER", " FAILED TO DELETED");
                    else
                        Log.v("SCHEDULER", "DELETED");

                    break;
                case Preferences.CAMPAIGN_SCHEDULE:
                    Log.v("SCHEDULER", "STARTED");
                    if (campaignSource.isCampaignDataAvailable(campaignId)) {
                        playAScheduleCampaign(campaignId);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fetch != null)
            fetch.release();
    }

    @Override
    /*DISABLED THE BACK PRESS */
    public void onBackPressed() {

    }


}
