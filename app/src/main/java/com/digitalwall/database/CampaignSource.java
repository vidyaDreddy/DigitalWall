package com.digitalwall.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ChannelModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CampaignSource {

    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {DBConstants.CAMPAIGN_ID,
            DBConstants.CAMPAIGN_NAME, DBConstants.CAMPAIGN_CLIENT_ID,
            DBConstants.CAMPAIGN_CLIENT_ID, DBConstants.CAMPAIGN_TYPE, DBConstants.CAMPAIGN_ORIENTATION};

    public CampaignSource(Context context) {
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

    public long insertData(CampaignModel model) {

        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.CAMPAIGN_ID, model.getCampaignId());
        values.put(DBConstants.CAMPAIGN_NAME, model.getCampaignName());
        values.put(DBConstants.CAMPAIGN_TYPE, model.getCampaignType());

        open();
        insertValue = mDatabase.insert(DBConstants.TABLE_CAMPAIGN, null, values);
        close();

        return insertValue;
    }

    public boolean isCampaignDataAvailable(String campaignId) {

        boolean isChannelAvailable = false;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CAMPAIGN, new String[]{DBConstants.CAMPAIGN_ID},
                DBConstants.CAMPAIGN_ID + " = ?", new String[]{campaignId}, null,
                null, null);
        if (cursor.getCount() > 0) {
            isChannelAvailable = true;
        }
        cursor.close();
        close();
        return isChannelAvailable;
    }

    /* Get all brand models */
    public ArrayList<CampaignModel> selectAll() {

        ArrayList<CampaignModel> campaignList = new ArrayList<>();

        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CAMPAIGN, mColumns, null, null, null, null, null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("_id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_ID)));
                    jsonObject.put("campaignName", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_NAME)));
                    jsonObject.put("type", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_TYPE)));
                    jsonObject.put("clientID", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_CLIENT_ID)));
                    jsonObject.put("orientation", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_ORIENTATION)));

                    CampaignModel model = new CampaignModel(jsonObject);

                    campaignList.add(model);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        close();
        return campaignList;
    }

    /* Get model data depends on brand name */
    public CampaignModel getCampaignByCampaignId(String campaign_id) {

        CampaignModel campaignModel = null;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CAMPAIGN, mColumns,
                DBConstants.CAMPAIGN_ID + " = ?", new String[]{campaign_id}, null, null,
                null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", "success");
                    jsonObject.put("_id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_ID)));
                    jsonObject.put("campaignName", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_NAME)));
                    jsonObject.put("clientID", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_ID)));
                    jsonObject.put("type", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_TYPE)));
                    jsonObject.put("orientation", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CAMPAIGN_ORIENTATION)));

                    campaignModel = new CampaignModel(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        close();

        return campaignModel;
    }


    /* Get brand count */
    public int getColumnDataCount() {

        int brandCount = -1;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CAMPAIGN, new String[]{DBConstants.CAMPAIGN_ID},
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            brandCount = cursor.getCount();
        }
        cursor.close();
        close();
        return brandCount;
    }

    /* Update brands depends on brand name */
    public int updateCampaignData(CampaignModel model) {

        int updateValue = -1;
        ContentValues values = new ContentValues();

        values.put(DBConstants.CAMPAIGN_ID, model.getCampaignId());
        values.put(DBConstants.CAMPAIGN_NAME, model.getCampaignType());
        values.put(DBConstants.CAMPAIGN_TYPE, model.getCampaignType());

        open();

        updateValue = mDatabase.update(DBConstants.TABLE_CAMPAIGN, values,
                DBConstants.CAMPAIGN_ID + " = ?", new String[]{model.getCampaignId()});
        close();
        return updateValue;
    }

    public int deleteAll() {

        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_CAMPAIGN, null, null);
        close();
        return deleteValue;
    }
}