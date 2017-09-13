package com.digitalwall.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.digitalwall.activities.DashboardActivity;
import com.digitalwall.model.AssetsModel;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.listener.FetchListener;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 10/09/17.
 */

public class Downloader implements FetchListener {
    private DashboardActivity parent;
    private int count;

    private ArrayList<AssetsModel> assetList;
    private ArrayList<AssetsModel> downloadList;


    public Downloader(DashboardActivity parent, ArrayList<AssetsModel> assetList) {

        Log.v("DOWNLOADER CLASS:", "ASSETS LIST COUNT:" + assetList.size());
        this.parent = parent;
        this.assetList = assetList;
        parent.fetch.addFetchListener(this);
    }


    public void setAssetDownloader() {

        for (int i = 0; i < assetList.size(); i++) {
            AssetsModel asset = assetList.get(i);

            final long assetDownId = generateDownloadId(asset.getAssetId());
            final String url = asset.getAssetUrl();
            if (Utils.isValueNullOrEmpty(asset.getAsset_local_url())) {
                asset.setAsset_local_url(Utils.getFileDownloadPath(url));
                parent.assetsSource.updateModelData(asset);
            }

            final String path = asset.getAsset_local_url();

            RequestInfo info = parent.fetch.get(assetDownId);
            if (info != null) {
                File file = new File(info.getFilePath());
                if (!file.exists()) {
                    parent.fetch.remove(assetDownId);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enqueueDownload(assetDownId, url, path);
                        }
                    }, 500);
                } else if (info.getProgress() == 100)
                    count++;
                else
                    parent.fetch.retry(assetDownId);

            } else
                enqueueDownload(assetDownId, url, path);

        }

        if (count == assetList.size())
            Log.v("DOWNLOAD COMPLETED", "COUNT :" + count);
        else
            Log.v("DOWNLOAD SKIPPED", "COUNT :" + count);

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
                //parent.fetch.retry(id);
                break;
            case Fetch.STATUS_DOWNLOADING:
                Log.i("DOWNLOADING", "ASSET ID:" + id + " Pro:" + progress);
                break;
            case Fetch.STATUS_DONE:
                Log.i("DOWNLOADED", "ASSET ID:" + id);
                count++;
                break;
            case Fetch.STATUS_REMOVED:
                Log.i("DOWNLOAD REMOVED", "ASSET ID:" + id);
                break;
        }
        if (status != Fetch.STATUS_REMOVED) {
            float per = (count / assetList.size()) * 100;
            if (per < 100)
                Log.i("DOWNLOAD", " DOWNLOADED [" + count + "/" + assetList.size() + "]");
            else {
                Log.i("DOWNLOAD COMPLETED", "COUNT :" + count);
            }
        }
    }


    private long generateDownloadId(String assetId) {
        int code = assetId.hashCode();
        return (long) code;
    }

}
