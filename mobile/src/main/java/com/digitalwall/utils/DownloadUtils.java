package com.digitalwall.utils;

import android.net.Uri;
import android.os.Environment;

import com.digitalwall.activities.BaseActivity;
import com.digitalwall.database.AssetsSource;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.model.Download;
import com.tonyodev.fetch.request.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidhayadhar
 * on 30/08/17.
 */

public class DownloadUtils {


    public static List<Request> getAutoCampaignList(BaseActivity parent, ArrayList<AssetsModel>
            mList, String channelId, boolean autoCampaign) {
        List<Request> requests = new ArrayList<>();
        for (AssetsModel model : mList) {

           /* String type = model.getAssetType();
            if (type.equals("video"))
                model.setAssetUrl("https://www.rmp-streaming.com/media/bbb-360p.mp4");*/
            String file_url;
            if (autoCampaign)
                file_url = getAutoCampaignFilePath(model.getAssetUrl());
            else
                file_url = getCampaignFilePath(model.getAssetUrl());


            Request request = new Request(model.getAssetUrl(), file_url);
            requests.add(request);

            model.setChannel_id(channelId);
            model.setAsset_local_url(file_url);
            AssetsSource assetsSource = new AssetsSource(parent);
            assetsSource.insertData(model);
        }
        return requests;
    }

    private static String getAutoCampaignFilePath(String url) {

        Uri uri = Uri.parse(url);
        String extension = url.substring(url.lastIndexOf("."));
        String fileName = uri.getLastPathSegment() + extension;
        String dir = getSaveDir();
        return (dir + "/Auto/" + System.nanoTime() + "_" + fileName);
    }

    private static String getCampaignFilePath(String url) {

        Uri uri = Uri.parse(url);
        String extension = url.substring(url.lastIndexOf("."));
        String fileName = uri.getLastPathSegment() + extension;
        String dir = getSaveDir();
        return (dir + "/Campaign/" + System.nanoTime() + "_" + fileName);
    }

    private static String getSaveDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/Digiwall";
    }

}
