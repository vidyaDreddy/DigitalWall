package com.digitalwall.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.digitalwall.activities.DashboardActivity;
import com.digitalwall.database.AssetsSource;
import com.digitalwall.database.ChannelSource;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 08/09/17.
 */

public class AssetUtils implements FetchListener {

    private DashboardActivity parent;
    private int count;

    private ArrayList<AssetsModel> assetList;
    private String campaignId;


    public AssetUtils(DashboardActivity parent, String campaignId, ArrayList<AssetsModel> assetList) {
        this.parent = parent;
        this.campaignId = campaignId;
        this.assetList = assetList;
        parent.fetch.addFetchListener(this);
    }


    public void setAutoCampaignDownloader() {

        for (int i = 0; i < assetList.size(); i++) {
            AssetsModel asset = assetList.get(i);

            long assetDownId = generateDownloadId(asset.getAssetId());
            String url = asset.getAssetUrl();
            if (Utils.isValueNullOrEmpty(asset.getAsset_local_url())) {
                asset.setAsset_local_url(Utils.getFileDownloadPath(url));
                parent.assetsSource.updateModelData(asset);
            }
            String path = asset.getAsset_local_url();

            RequestInfo info = parent.fetch.get(assetDownId);
            if (info != null) {
                File file = new File(info.getFilePath());
                if (file.exists()) {
                    if (info.getProgress() == 100)
                        count++;
                    else
                        parent.fetch.retry(assetDownId);
                } else {
                    parent.fetch.remove(assetDownId);
                    enqueueDownload(assetDownId, url, path);
                }
            } else {
                enqueueDownload(assetDownId, url, path);
            }
        }

        if (count == assetList.size()) {
            Log.v("DOWNLOAD COMPLETED", "COUNT :" + count);
            parent.playAutoCampaignWithSavedData(campaignId);
        }
    }

    private void enqueueDownload(long assetDownId, String url, String filePath) {

        Request request = new Request(url, filePath);
        request.setFileId(assetDownId);
        parent.fetch.enqueue(request);
    }

    @Override
    public void onUpdate(long id, int status, int progress, long downloadedBytes,
                         long fileSize, int error) {

        switch (status) {
            case Fetch.STATUS_ERROR:
                Log.i("DOWNLOAD ERROR", "ASSET ID:" + id);
                parent.fetch.retry(id);
                break;
            case Fetch.STATUS_DOWNLOADING:
                Log.i("DOWNLOADING", "ASSET ID:" + id + " Pro" + progress);
                break;
            case Fetch.STATUS_DONE:
                Log.i("DOWNLOADED", "ASSET ID:" + id);
                count++;
                break;
        }

        float per = (count / assetList.size()) * 100;
        if (per < 100)
            Log.v("DOWNLOAD", " DOWNLOADED [" + count + "/" + assetList.size() + "]");
        else {
            Log.v("DOWNLOAD COMPLETED", "COUNT :" + count);
            parent.playAutoCampaignWithSavedData(campaignId);
        }
    }




    private long generateDownloadId(String assetId) {
        int code = assetId.hashCode();
        return (long) code;
    }
}

