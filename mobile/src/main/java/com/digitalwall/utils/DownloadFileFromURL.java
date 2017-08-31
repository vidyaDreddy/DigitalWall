package com.digitalwall.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.digitalwall.activities.BaseActivity;
import com.digitalwall.database.AssetsSource;
import com.digitalwall.model.AssetsModel;
import com.digitalwall.services.DownloadResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by vidhayadhar
 * on 21/08/17.
 */

public class DownloadFileFromURL extends AsyncTask<String, String, String> {


    private BaseActivity parent;
    private AssetsModel model;

    private WeakReference<DownloadResult> WEAK_JSON_RESULT;

    public DownloadFileFromURL(BaseActivity parent, DownloadResult result, AssetsModel model) {
        this.WEAK_JSON_RESULT = new WeakReference<>(result);
        this.model = model;
        this.parent = parent;
    }


    private File file;

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(model.getAssetUrl());
            URLConnection connection = url.openConnection();
            connection.connect();
            int lenghtOfFile = connection.getContentLength();


            InputStream input = new BufferedInputStream(url.openStream(), 8192);




           /* String filePath = DownloadUtils.cleanFilePath(DownloadUtils.
                    generateFilePath(model.getAssetUrl(), fileName));*/

            File home1 = new File(Environment.getExternalStorageDirectory() + "/" + "DigitalWall");
            if (!home1.exists()) {
                boolean status = home1.mkdir();
                Log.v("DOWNLOADED", "FOLDER Status" + status);
            }

            String type;
            if (model.getAssetType().equalsIgnoreCase("image"))
                type = ".jpg";
            else
                type = ".mp4";

            //String filenameOutput = model.getAssetId() + "_" + new Date() + type;
            String fileName = DownloadUtils.getAutoCampaignFilePathNew(model.getAssetUrl());
            file = new File(home1, fileName);


            OutputStream output = new FileOutputStream(file);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return "";
        }
        return file.getAbsolutePath();
    }

    @Override
    protected void onPostExecute(String file_url) {

        Log.v("DOWNLOADED", "URL PATH" + file_url);

        DownloadResult activity = WEAK_JSON_RESULT.get();
        if (activity != null)
            activity.successDownload(file_url);

        model.setAsset_local_url(file_url);
        AssetsSource assetsSource = new AssetsSource(parent);
        assetsSource.insertData(model);
    }
}
