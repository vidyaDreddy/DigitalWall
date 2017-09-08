package com.digitalwall.utils;

import android.net.Uri;
import android.os.Environment;

import com.digitalwall.activities.DashboardActivity;
import com.digitalwall.model.AssetsModel;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vidhayadhar
 * on 08/09/17.
 */

public class AssetUtils {


    public static void initilizeDownloader(DashboardActivity parent, ArrayList<AssetsModel> assetList) {

        for (int i = 0; i < assetList.size(); i++) {
            AssetsModel asset = assetList.get(i);

            long assetDownId = asset.getDownloadId();
            String url = asset.getAssetUrl();
            String path = asset.getAsset_local_url();

            RequestInfo info = parent.fetch.get(assetDownId);
            if (info != null) {
                File file = new File(info.getFilePath());
                if (file.exists()) {
                    if (info.getProgress() != 100)
                        parent.fetch.retry(assetDownId);
                } else {
                    parent.fetch.remove(assetDownId);
                    enqueueDownload(parent, assetDownId, url, path);
                }
            } else {
                enqueueDownload(parent, assetDownId, url, path);
            }
        }
    }

    private static void enqueueDownload(DashboardActivity parent, long assetDownId,
                                        String url, String filePath) {
        Request request = new Request(url, filePath);
        request.setFileId(assetDownId);
        parent.fetch.enqueue(request);
    }


}
