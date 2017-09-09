package com.digitalwall.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.digitalwall.model.AssetsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AssetsSource {

    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {DBConstants.COLUMN_ASSET_ID,
            DBConstants.COLUMN_ASSET_TYPE, DBConstants.COLUMN_ASSET_URL,
            DBConstants.COLUMN_ASSET_DURATION, DBConstants.COLUMN_ASSET_LOCAL_URL,
            DBConstants.COLUMN_ASSET_ANIMATION, DBConstants.COLUMN_ASSET_DOWNLOAD_ID,
            DBConstants.CHANNELS_ID};

    public AssetsSource(Context context) {
        if (context != null) {
            mContext = context;
            mHandler = new DatabaseHandler(mContext);
        }
    }

    private void open() {
        if (mHandler != null) {
            mDatabase = mHandler.getWritableDatabase();
        }
    }

    private void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public long insertData(AssetsModel model, String channelId) {
        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.CHANNELS_ID, channelId);
        values.put(DBConstants.COLUMN_ASSET_ID, model.getAssetId());
        values.put(DBConstants.COLUMN_ASSET_TYPE, model.getAssetType());
        values.put(DBConstants.COLUMN_ASSET_URL, model.getAssetUrl());
        values.put(DBConstants.COLUMN_ASSET_DURATION, model.getAssetDuration());
        values.put(DBConstants.COLUMN_ASSET_DOWNLOAD_ID, "" + model.getDownloadId());
        if (model.getAsset_local_url() != null)
            values.put(DBConstants.COLUMN_ASSET_LOCAL_URL, model.getAsset_local_url());
        else
            values.put(DBConstants.COLUMN_ASSET_LOCAL_URL, "");
        values.put(DBConstants.COLUMN_ASSET_ANIMATION, model.getAsset_animation());
        open();
        insertValue = mDatabase.insert(DBConstants.TABLE_ASSETS, null,
                values);
        close();

        return insertValue;
    }

    public boolean isChannelDataAvailable(AssetsModel model) {
        boolean isChannelAvailable = false;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_ASSETS, new String[]{DBConstants.COLUMN_ASSET_ID},
                DBConstants.COLUMN_ASSET_ID + " = ?", new String[]{model.getAssetId()}, null,
                null, null);
        if (cursor.getCount() > 0) {
            isChannelAvailable = true;
        }
        cursor.close();
        close();
        return isChannelAvailable;
    }

    /* Get model data depends on brand name */
    public ArrayList<AssetsModel> getAssetListByChannelId(String channel_id) {
        ArrayList<AssetsModel> assetsList = null;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_ASSETS, mColumns,
                DBConstants.CHANNELS_ID + " = ?", new String[]{channel_id}, null, null,
                null);
        if (cursor.getCount() > 0) {
            assetsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("_id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_ID)));
                    jsonObject.put("duration", cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_DURATION)));
                    jsonObject.put("type", cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_TYPE)));
                    jsonObject.put("url", cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_URL)));
                    jsonObject.put("local_url", cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_LOCAL_URL)));
                    jsonObject.put("campaignAnimation", cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_ANIMATION)));
                    jsonObject.put("downloadId", Long.parseLong(cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_DOWNLOAD_ID))));
                    jsonObject.put("channel_id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNELS_ID)));
                    AssetsModel model = null;
                    model = new AssetsModel(jsonObject);
                    assetsList.add(model);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        close();
        return assetsList;
    }


    /* Get assets count by brand */
    public int getAssetsDataCountByChannelID(String channel_id) {
        int assetsCount = -1;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_ASSETS, new String[]{DBConstants.CHANNELS_ID},
                DBConstants.CHANNELS_ID + " = ?", new String[]{channel_id}, null, null,
                null);
        if (cursor.getCount() > 0) {
            assetsCount = cursor.getCount();
        }
        cursor.close();
        close();
        return assetsCount;
    }

    /* Update model depends on model & brand name */
    public int updateModelData(AssetsModel model) {
        int updateValue = -1;
        ContentValues values = new ContentValues();

        values.put(DBConstants.CHANNELS_ID, model.getChannel_id());
        values.put(DBConstants.COLUMN_ASSET_ID, model.getAssetId());
        values.put(DBConstants.COLUMN_ASSET_TYPE, model.getAssetType());
        values.put(DBConstants.COLUMN_ASSET_URL, model.getAssetUrl());
        values.put(DBConstants.COLUMN_ASSET_DURATION, model.getAssetDuration());
        values.put(DBConstants.COLUMN_ASSET_LOCAL_URL, model.getAsset_local_url());
        values.put(DBConstants.COLUMN_ASSET_ANIMATION, model.getAsset_animation());
        values.put(DBConstants.COLUMN_ASSET_DOWNLOAD_ID, "" + model.getDownloadId());

        open();
        updateValue = mDatabase.update(DBConstants.TABLE_ASSETS, values,
                DBConstants.COLUMN_ASSET_ID + " = ?" + " AND " + DBConstants.CHANNELS_ID + " = ?",
                new String[]{model.getAssetId(), model.getChannel_id()});
        close();
        return updateValue;
    }

    public int deleteAll() {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_ASSETS, null, null);
        close();
        return deleteValue;
    }

    /* Get all assets models */
    public ArrayList<AssetsModel> selectAll() {

        ArrayList<AssetsModel> assetsModelsList = new ArrayList<>();
        ArrayList<String> assetsModelsIDsList = new ArrayList<>();

        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_ASSETS, mColumns, null, null, null, null, null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                try {
                    if (!assetsModelsIDsList.contains(cursor.getString(cursor
                            .getColumnIndex(DBConstants.COLUMN_ASSET_ID)))) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("_id", cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_ID)));
                        jsonObject.put("duration", cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_DURATION)));
                        jsonObject.put("type", cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_TYPE)));
                        jsonObject.put("url", cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_URL)));
                        jsonObject.put("local_url", cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_LOCAL_URL)));
                        jsonObject.put("campaignAnimation", cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_ANIMATION)));
                        jsonObject.put("downloadId", Long.parseLong(cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_DOWNLOAD_ID))));
                        jsonObject.put("channel_id", cursor.getString(cursor
                                .getColumnIndex(DBConstants.CHANNELS_ID)));

                        AssetsModel model = new AssetsModel(jsonObject);
                        assetsModelsIDsList.add(cursor.getString(cursor
                                .getColumnIndex(DBConstants.COLUMN_ASSET_ID)));
                        assetsModelsList.add(model);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        close();
        return assetsModelsList;
    }
}