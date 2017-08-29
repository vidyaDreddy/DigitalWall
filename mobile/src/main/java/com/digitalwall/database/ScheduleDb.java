package com.digitalwall.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digitalwall.model.ScheduleCampaignModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shankar on 8/26/2017.
 */

public class ScheduleDb {
    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {
            DBConstants.SCHEDULES_START_DATE,
            DBConstants.SCHEDULES_END_DATE,
            DBConstants.SCHEDULES_START_TIME,
            DBConstants.SCHEDULES_END_TIME,
            DBConstants.SCHEDULES_CAMPAIGN_ID,
            DBConstants.SCHEDULES_JOB_ID,
            DBConstants.SCHEDULES_ID};

    public ScheduleDb(Context context) {
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

    public long insertData(ScheduleCampaignModel model) {
        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.SCHEDULES_START_DATE, model.getStartDate());
        values.put(DBConstants.SCHEDULES_END_DATE, model.getEndDate());
        values.put(DBConstants.SCHEDULES_START_TIME, model.getStartTime());
        values.put(DBConstants.SCHEDULES_END_TIME, model.getEndTime());
        values.put(DBConstants.SCHEDULES_CAMPAIGN_ID, model.getCampaignId());
        values.put(DBConstants.SCHEDULES_JOB_ID, model.getJobid());
        values.put(DBConstants.SCHEDULES_ID, model.getId());
        open();
        insertValue = mDatabase.insert(DBConstants.TABLE_SCHEDULES, null,
                values);
        close();

        return insertValue;
    }

    public ScheduleCampaignModel selectAll() {

        ScheduleCampaignModel scheduleCampaignModel = null;
        open();

        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String date = DATE_FORMAT.format(today);
        Log.d("currentDate", ",,,,,,,,," + date);
        long currentTime = (today.getSeconds() + (today.getMinutes() * 60) + (today.getHours() * 60 * 60));
        Log.d("currentTime", ",,,,,,,,," + currentTime);


        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + DBConstants.TABLE_SCHEDULES +
                        " WHERE " +
                        DBConstants.SCHEDULES_START_DATE + " <= '" + date +
                        "' AND " +
                        DBConstants.SCHEDULES_END_DATE + " >= '" + date + "'" +
                        " AND " +
                        DBConstants.SCHEDULES_START_TIME + " <= " + currentTime +
                        " AND " +
                        DBConstants.SCHEDULES_END_TIME + " > " + currentTime
                , null);

        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
               /* Log.d("cursor campaignIDs","........."+cursor.getString(cursor.getColumnIndex(DBConstants.SCHEDULES_CAMPAIGN_ID)));
                Log.d("cursor startdate","........."+cursor.getString(cursor.getColumnIndex(DBConstants.SCHEDULES_START_DATE)));
                Log.d("cursor end  date","........."+cursor.getString(cursor.getColumnIndex(DBConstants.SCHEDULES_END_DATE)));
                return cursor.getString(cursor.getColumnIndex(DBConstants.SCHEDULES_CAMPAIGN_ID));*/
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("_id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_ID)));
                    jsonObject.put("startDate", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_START_DATE)));
                    jsonObject.put("endDate", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_END_DATE)));
                    jsonObject.put("startTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_START_TIME)));
                    jsonObject.put("endTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_END_TIME)));
                    jsonObject.put("campaignId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_CAMPAIGN_ID)));
                    jsonObject.put("jobId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_JOB_ID)));

                    scheduleCampaignModel = new ScheduleCampaignModel(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("haaaa no rec", "....");
        }

        cursor.close();
        close();
        return scheduleCampaignModel;
    }

    public int deleteRecordById(String schedules_id) {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_SCHEDULES, DBConstants.SCHEDULES_ID + " = ?",
                new String[]{"" + schedules_id});
        close();
        return deleteValue;
    }

    /* Update brands depends on brand name */
    public int updateSechdleDateData(ScheduleCampaignModel model) {

        int updateValue = -1;
        ContentValues values = new ContentValues();

        values.put(DBConstants.SCHEDULES_START_DATE, model.getStartDate());
        values.put(DBConstants.SCHEDULES_END_DATE, model.getEndDate());
        values.put(DBConstants.SCHEDULES_START_TIME, model.getStartTime());
        values.put(DBConstants.SCHEDULES_END_TIME, model.getEndTime());
        values.put(DBConstants.SCHEDULES_CAMPAIGN_ID, model.getCampaignId());
        values.put(DBConstants.SCHEDULES_JOB_ID, model.getJobid());
        values.put(DBConstants.SCHEDULES_ID, model.getId());

        open();

        updateValue = mDatabase.update(DBConstants.TABLE_SCHEDULES, values,
                DBConstants.SCHEDULES_ID + " = ?", new String[]{model.getId()});
        close();
        return updateValue;
    }


    public int deleteAll() {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_SCHEDULES, null, null);
        close();
        return deleteValue;
    }
}
