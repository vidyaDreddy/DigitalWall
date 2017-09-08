package com.digitalwall.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalwall.R;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.scheduler.Job;
import com.digitalwall.scheduler.SmartScheduler;
import com.digitalwall.services.ApiConfiguration;
import com.digitalwall.services.JSONResult;
import com.digitalwall.services.JSONTask;
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
import com.tonyodev.fetch.listener.FetchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


@SuppressWarnings("deprecation")
@SuppressLint("SetTextI18n")
public class DashboardActivity extends BaseActivity implements JSONResult,
        SmartScheduler.JobScheduledCallback, FetchListener {

    private JSONTask getChannelListInfo;
    public WebSocketClient webSocketClient;

    private BusWrapper busWrapper;
    private NetworkEvents networkEvents;

    private LinearLayout ll_display_key;
    private RelativeLayout rl_main;

    private AudioManager mAudioManager;
    private SmartScheduler jobScheduler;
    public Fetch fetch;

    private ArrayList<AssetsModel> assetList;

    public String display_key;
    public String clientId;
    public String autoCampaignId;
    public String orientation;

    private int deviceVolume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        clientId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_CLIENT_ID);
        autoCampaignId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID);

        fetch = Fetch.newInstance(this);
        fetch.setAllowedNetwork(Fetch.NETWORK_ALL);
        fetch.addFetchListener(this);

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
        }
    }


    private void initilizeView() {

        /*SET DEVICE VOLUME*/
        deviceVolume = Preferences.getIntSharedPref(this, Preferences.PREF_KEY_VOLUME);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, deviceVolume, 0);

        /*MAIN LAYOUT*/
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_main.setVisibility(View.GONE);

        ll_display_key = (LinearLayout) findViewById(R.id.ll_display_key);
        ll_display_key.setVisibility(View.VISIBLE);

        /*DISPLAY KEY*/
        display_key = ApiConfiguration.getAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID);
        TextView tv_display_key = (TextView) findViewById(R.id.tv_display_key);
        tv_display_key.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_font));
        tv_display_key.setText(display_key);

        /*DISPLAY KEY LABEL*/
        TextView tv_display_key_label = (TextView) findViewById(R.id.tv_display_key_label);
        tv_display_key_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_font));

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
        String scheduleId = jObject.getString("scheduleID");
    }


    private void getAutoCampaignData(String campaignId) {


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


    private void getScheduleCampaignData(String clientId, String campaignId) {

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
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    ll_display_key.setVisibility(View.GONE);
                    rl_main.setVisibility(View.VISIBLE);
                    CampaignModel model = new CampaignModel(jObject);
                } else {
                    ll_display_key.setVisibility(View.VISIBLE);
                    rl_main.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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


    @Override
    public void failedJsonResult(int code) {

        /*GET THE AUTO CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_AUTO_CAMPAIGN_INFO_CODE) {
            Log.v("AUTO CAMPAIGN:", "FAILED TO GET THE DETAILS");
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


    private void setSchedulerPlayer(int JOB_ID, String campaignId, String clientId,
                                    String tag, long time, String scheduleId) {

        // Check if any periodic job is currently scheduled
        if (jobScheduler.contains(JOB_ID)) {
            jobScheduler.removeJob(JOB_ID);
            return;
        }

        /*JOB CREATED*/
        Job.Builder builder = new Job.Builder(JOB_ID, campaignId, this, tag, clientId, scheduleId)
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
            String campaignId = job.getJobCampaignId();
            String clientId = job.getJobClientId();
            String scheduleId = job.getJobScheduleId();
            String type = job.getPeriodicTaskTag();
        }
    }

    @Override
    public void onUpdate(long id, int status, int progress, long downloadedBytes, long fileSize, int error) {
        switch (status) {
            case Fetch.STATUS_ERROR:
                Log.i("DOWNLOAD ERROR", "ASSET ID:" + id);
                fetch.retry(id);
                break;
            case Fetch.STATUS_DOWNLOADING:
                Log.i("DOWNLOADING", "ASSET ID:" + id + " Pro" + progress);
                break;
            case Fetch.STATUS_DONE:
                Log.i("DOWNLOADED", "ASSET ID:" + id);
                break;
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
