package com.digitalwall.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.digitalwall.services.DownloadResult;
import com.digitalwall.services.JSONResult;
import com.digitalwall.services.JSONTask;
import com.digitalwall.utils.DateUtils;
import com.digitalwall.utils.DeviceInfo;
import com.digitalwall.utils.DownloadUtils;
import com.digitalwall.utils.PlayerUtils;
import com.digitalwall.utils.Preferences;
import com.digitalwall.utils.Utils;
import com.mixpanel.android.java_websocket.client.WebSocketClient;
import com.mixpanel.android.java_websocket.handshake.ServerHandshake;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@SuppressWarnings("deprecation")
@SuppressLint("SetTextI18n")
public class PlayerActivity extends BaseActivity implements JSONResult,
        SmartScheduler.JobScheduledCallback, FetchListener, DownloadResult {

    private JSONTask getChannelListInfo;

    public WebSocketClient webSocketClient;

    private TextView tv_display_key;
    private RelativeLayout rl_main;
    public String display_key;
    public String clientId;
    public String autoCampaignId;

    private SmartScheduler jobScheduler;

    private ProgressDialog progressBar;

    private CampaignSource campaignDB;
    private ScheduleDb schedulesDB;

    private boolean playLiveData = false;
    private int count;
    private List<Request> requests = new ArrayList<>();

    private Fetch fetch;

    private boolean downloadAutoCampaign;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fetch = Fetch.newInstance(this);
        fetch.setAllowedNetwork(Fetch.NETWORK_ALL);
        fetch.addFetchListener(this);

        campaignDB = new CampaignSource(this);
        schedulesDB = new ScheduleDb(this);

        jobScheduler = SmartScheduler.getInstance(this);

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please wait player is configuring.");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        display_key = ApiConfiguration.getAuthToken(this, ApiConfiguration.PREF_KEY_DISPLAY_ID);
        clientId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_CLIENT_ID);
        autoCampaignId = Preferences.getStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID);

        Log.d("display_key", "......." + display_key);

        /*MAIN LAYOUT*/
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        rl_main.setVisibility(View.GONE);

        /*DISPLAY KEY TEXT_VIEW*/
        tv_display_key = (TextView) findViewById(R.id.tv_display_key);
        tv_display_key.setVisibility(View.GONE);

        /*CHECK FOR THE PLAYER INFO*/

        final ScheduleModel scheduleModel = schedulesDB.getCurrentAviableCampaign();
        if (scheduleModel != null) {

            createCampaignPlayer(autoCampaignId);
            scheduleACampaign(scheduleModel);

            /*if (campaignDB.isCampaignDataAvailable(scheduleModel.getCampaignId()))
                createCampaignPlayer(scheduleModel.getCampaignId());
            else {

                playLiveData = true;
                getScheduleChannelInfo(clientId, scheduleModel.getCampaignId());
            }*/

        } else if (!Utils.isValueNullOrEmpty(autoCampaignId)) {

            if (campaignDB.isCampaignDataAvailable(autoCampaignId))
                createCampaignPlayer(autoCampaignId);
            else
                getAutoCampaignChannelInfo(autoCampaignId);

        } else {
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

                        switch (type) {
                            case "PLAYERCREATED":
                                configureAutoCampaignPlayer(jObject);
                                break;
                            case "SCHEDULECREATED":
                                configureScheduleCampaignPlayer(jObject);
                                playLiveData = false;
                                break;
                            case "SCHEDULEDELETED":
                                deleteAScheduleCampaign(jObject);
                                break;
                            case "CLEARCACHE":
                                deleteLocalFiles();
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

    public void setDateToWebSocket(String message) {
        if (webSocketClient != null && webSocketClient.isOpen())
            webSocketClient.send(message);
    }

    private void deleteAScheduleCampaign(JSONObject jObject) throws JSONException {

        String scheduleId = jObject.getString("scheduleID");

        /*DELETE A CAMPAIGN*/
        deleteScheduleByScheduleId(scheduleId);
    }

    private void configureScheduleCampaignPlayer(JSONObject jObject) throws JSONException {

        /*SCHEDULE ID*/
        String scheduleId = jObject.getString("scheduleID");

        /*CLIENT ID*/
        clientId = jObject.getString("clientID");

        /*GET CHANNEL INFO FOR SCHEDULE CAMPAIGN*/
        getScheduleCampaignInfo(clientId, scheduleId);
    }


    private void configureAutoCampaignPlayer(JSONObject jObject) throws JSONException {

        /*CAMPAIGN ID*/
        autoCampaignId = jObject.getString("campID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_AUTO_CAMPAIGN_ID, autoCampaignId);

        /*CLIENT ID*/
        clientId = jObject.getString("clientID");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_CLIENT_ID, clientId);

        /*VOLUME*/
        int deviceVolume = jObject.getInt("volume");
        Preferences.setIntSharedPref(this, Preferences.PREF_KEY_VOLUME, deviceVolume);

        /*CLIENT ID*/
        String deviceOrientation = jObject.getString("orientation");
        Preferences.setStringSharedPref(this, Preferences.PREF_KEY_ORIENTATION, deviceOrientation);

        /*GET CHANNEL INFO FOR AUTO CAMPAIGN*/
        getAutoCampaignChannelInfo(autoCampaignId);
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
            Log.d("...", "...1 code here" + code);
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    tv_display_key.setVisibility(View.GONE);
                    rl_main.setVisibility(View.VISIBLE);
                    CampaignModel model = new CampaignModel(jObject);

                    saveAutoCampaignData(model);
                } else {

                    tv_display_key.setVisibility(View.VISIBLE);
                    rl_main.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

          /*GET THE SCHEDULE CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_UPDATE_SCHEDULE_INFO_CODE) {
            try {
                JSONObject jObject = (JSONObject) result;
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    ArrayList<ScheduleModel> scheduleModels = getScheduleCampaignModelList(jObject);
                    for (int i = 0; i < scheduleModels.size(); i++) {
                        schedulesDB.insertData(scheduleModels.get(i));
                    }

                    String campaignId = jObject.optString("campaignId");
                    initilizeScheduleCampaign(campaignId);
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
                    ArrayList<ScheduleModel> scheduleModels =
                            getScheduleCampaignModelList(jObject);
                    for (int i = 0; i < scheduleModels.size(); i++) {
                        ScheduleModel model = scheduleModels.get(i);
                        schedulesDB.insertData(model);
                    }

                    String campaignId = jObject.optString("campaignId");
                    initilizeScheduleCampaign(campaignId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

         /*GET THE  CAMPAIGN INFO*/
        if (code == ApiConfiguration.GET_CAMPAIGN_INFO_CODE) {
            Log.d("...", "...3 code here" + code);
            try {
                JSONObject jObject = (JSONObject) result;
                Log.d("...", "...3 code here" + jObject);
                String status = jObject.optString("status");
                if (status.equalsIgnoreCase("success")) {
                    CampaignModel model = new CampaignModel(jObject);
                    if (playLiveData) {
                        PlayerUtils.setLiveData(PlayerActivity.this, rl_main, model);
                    } else
                        saveScheduleCampaignData(model);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private ArrayList<ScheduleModel> getScheduleCampaignModelList(JSONObject jsonObject) throws JSONException {

        ArrayList<ScheduleModel> scheduleModels = new ArrayList<>();
        JSONArray mScheduleJsonArray = jsonObject.optJSONArray("schedules");

        for (int i = 0; i < mScheduleJsonArray.length(); i++) {
            JSONObject mScheduleCampaignModelJson = mScheduleJsonArray.optJSONObject(i);

            mScheduleCampaignModelJson.put("_id", jsonObject.optString("_id"));
            mScheduleCampaignModelJson.put("campaignId", jsonObject.optString("campaignId"));
            mScheduleCampaignModelJson.put("jobId", DeviceInfo.randomJobId());
            mScheduleCampaignModelJson.put("startDate", mScheduleCampaignModelJson.optString("startDate"));
            mScheduleCampaignModelJson.put("endDate", mScheduleCampaignModelJson.optString("endDate"));
            mScheduleCampaignModelJson.put("sTime", mScheduleCampaignModelJson.optString("startTime"));
            mScheduleCampaignModelJson.put("eTime", mScheduleCampaignModelJson.optString("endTime"));
            mScheduleCampaignModelJson.put("startTime", DateUtils.
                    convertTimeToSeconds(mScheduleCampaignModelJson.optString("startTime")));
            mScheduleCampaignModelJson.put("endTime", DateUtils.
                    convertTimeToSeconds(mScheduleCampaignModelJson.optString("endTime")));

            ScheduleModel scheduleModel = new ScheduleModel(mScheduleCampaignModelJson);

            scheduleModels.add(scheduleModel);

        }
        return scheduleModels;
    }


    private void initilizeScheduleCampaign(String campaignId) {

        ArrayList<ScheduleModel> mList = schedulesDB.getAllScheduleList();

        for (int i = 0; i < mList.size(); i++) {
            ScheduleModel model = mList.get(i);
            scheduleACampaign(model);
        }

        /*DOWNLOAD AND SYNC THE SCHEDULE INFO*/
        if (!campaignDB.isCampaignDataAvailable(campaignId)) {
            playLiveData = false;
            getScheduleChannelInfo(clientId, campaignId);
        }


    }


    private void scheduleACampaign(ScheduleModel model) {

        Calendar sCal = DateUtils.getCalendarDate(model.getStartDate(), model.getsTime());
        setSchedulerPlayer(model.getJobid(), model.getCampaignId(), clientId,
                Preferences.CAMPAIGN_SCHEDULE, sCal.getTimeInMillis(), model.getId());

        //*SCHEDULE THE EVENT WITH END TIME*//*
        Calendar eCal = DateUtils.getCalendarDate(model.getEndDate(), model.geteTime());
        int eJobId = model.getJobid() + 12;
        setSchedulerPlayer(eJobId, autoCampaignId, clientId,
                Preferences.CAMPAIGN_AUTO, eCal.getTimeInMillis(), model.getId());
    }

    private ArrayList<AssetsModel> mAssetList;

    private void saveScheduleCampaignData(CampaignModel model) {
        downloadAutoCampaign = false;

        campaignDB.insertData(model);
        if (model.getChannelList().size() > 0) {
            ChannelSource channelDB = new ChannelSource(this);

            for (int i = 0; i < model.getChannelList().size(); i++) {
                ChannelModel channelModel = model.getChannelList().get(i);
                channelDB.insertData(channelModel, model.getCampaignId());
                if (channelModel.getAssetsList().size() > 0)
                    enqueueDownloads(channelModel.getAssetsList(), channelModel.getChannelId());
            }
        }
    }


    private void saveAutoCampaignData(CampaignModel model) {
        downloadAutoCampaign = true;

        campaignDB.insertData(model);
        if (model.getChannelList().size() > 0) {
            ChannelSource channelDB = new ChannelSource(this);

            for (int i = 0; i < model.getChannelList().size(); i++) {
                ChannelModel channelModel = model.getChannelList().get(i);
                channelDB.insertData(channelModel, model.getCampaignId());
                /*mAssetList = channelModel.getAssetsList();
                if (mAssetList.size() > 0) {
                    for (int j = 0; j < mAssetList.size(); j++) {
                        AssetsModel asset = mAssetList.get(j);
                        String type = asset.getAssetType();
                        if (type.equals("video"))
                            asset.setAssetUrl("https://www.rmp-streaming.com/media/bbb-360p.mp4");
                        asset.setChannel_id(channelModel.getChannelId());
                        new DownloadFileFromURL(PlayerActivity.this, this, asset).execute();
                    }
                }*/

                if (channelModel.getAssetsList().size() > 0)
                    enqueueDownloads(channelModel.getAssetsList(), channelModel.getChannelId());

            }
        }
    }


    private void createCampaignPlayer(String campaignId) {

        CampaignModel campaignModel = campaignDB.getCampaignByCampaignId(campaignId);

        /*SET THE CHANNEL LIST INFO*/
        ChannelSource channelDB = new ChannelSource(this);
        ArrayList<ChannelModel> mChannelList = channelDB.selectAllChannelByCampaign(campaignId);
        if (mChannelList != null) {

            campaignModel.setChannelList(mChannelList);
             /*SET THE ASSET LIST INFO*/
            AssetsSource assetsDB = new AssetsSource(this);
            for (int i = 0; i < mChannelList.size(); i++) {
                ChannelModel channel = mChannelList.get(i);
                ArrayList<AssetsModel> mAssetsList = assetsDB.getAssetListByChannelId(channel.getChannelId());
                channel.setAssetsList(mAssetsList);
            }

            ArrayList<ChannelModel> cList = campaignModel.getChannelList();
            if (cList.size() > 0 && cList.get(0).getAssetsList() != null) {
                tv_display_key.setVisibility(View.GONE);
                rl_main.setVisibility(View.VISIBLE);
                PlayerUtils.setAutoCampaignPlayerData(this, rl_main, campaignModel);
            } else {
                getScheduleChannelInfo(campaignModel.getClientId(), campaignId);
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


    private void deleteScheduleByScheduleId(String scheduleId) {
        schedulesDB.deleteScheduleById(scheduleId);
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

        PlayerActivity.this.runOnUiThread(new Runnable() {
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

            if (type.equals(Preferences.CAMPAIGN_AUTO)) {
                deleteScheduleByScheduleId(scheduleId);
                createCampaignPlayer(campaignId);
            } else if (type.equals(Preferences.CAMPAIGN_SCHEDULE)) {
                if (!campaignDB.isCampaignDataAvailable(campaignId)) {
                    playLiveData = true;
                    getScheduleChannelInfo(clientId, campaignId);
                } else {
                    createCampaignPlayer(campaignId);
                }
            }
        }
    }

    @Override
    /*DISABLED THE BACK PRESS */
    public void onBackPressed() {

    }


    private void enqueueDownloads(ArrayList<AssetsModel> mList, String channelId) {
        requests = DownloadUtils.getAutoCampaignList(this, mList, channelId, downloadAutoCampaign);
        fetch.enqueue(requests);
    }

    @Override
    public void onUpdate(long id, int status, int progress, long downloadedBytes,
                         long fileSize, int error) {

        if (status == Fetch.STATUS_DONE) {
            count++;
        } else if (status == Fetch.STATUS_ERROR) {
            count++;
        } else if (error != Fetch.NO_ERROR) {
            count++;
        }

        if (requests.size() > 0) {
            float per = (count / requests.size()) * 100;
            if (per < 100)
                Log.v("DOWNLOAD", " DOWNLOADED [" + count + "/" + requests.size() + "]");
            else {
                if (downloadAutoCampaign) {
                    createCampaignPlayer(autoCampaignId);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            }
        }

    }

    @Override
    public void successDownload(String result) {
        count++;

        if (mAssetList.size() > 0) {
            float per = (count / mAssetList.size()) * 100;
            if (per < 100)
                Log.v("DOWNLOAD", " DOWNLOADED [" + count + "/" + mAssetList.size() + "]");
            else {
                Log.v("DOWNLOAD", " DOWNLOAD COMPLETED");

                if (downloadAutoCampaign) {
                    createCampaignPlayer(autoCampaignId);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            }
        }
    }
    public void deleteLocalFiles(){
        File dir = new File(DownloadUtils.getSaveDir());
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
