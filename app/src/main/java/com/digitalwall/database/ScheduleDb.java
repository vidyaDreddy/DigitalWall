package com.digitalwall.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.digitalwall.model.CampaignModel;
import com.digitalwall.model.ScheduleModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shankar
 * on 8/26/2017.
 */

public class ScheduleDb {
    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {
            DBConstants.SCHEDULES_START_DATE,
            DBConstants.SCHEDULES_END_DATE,
            DBConstants.SCHEDULES_S_TIME,
            DBConstants.SCHEDULES_E_TIME,
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

    public long insertData(ScheduleModel model) {
        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.SCHEDULES_START_DATE, model.getStartDate());
        values.put(DBConstants.SCHEDULES_END_DATE, model.getEndDate());
        values.put(DBConstants.SCHEDULES_S_TIME, model.getsTime());
        values.put(DBConstants.SCHEDULES_E_TIME, model.geteTime());
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

    /* Get model data depends on brand name */
    public ScheduleModel getScheduleByCampaignId(String schedule_id) {

        ScheduleModel campaignModel = null;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_SCHEDULES, mColumns,
                DBConstants.SCHEDULES_ID + " = ?", new String[]{schedule_id}, null, null,
                null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
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
                    jsonObject.put("sTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_S_TIME)));
                    jsonObject.put("eTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_E_TIME)));
                    jsonObject.put("campaignId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_CAMPAIGN_ID)));
                    jsonObject.put("jobId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_JOB_ID)));


                    campaignModel = new ScheduleModel(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        close();

        return campaignModel;
    }

    public ArrayList<ScheduleModel> getAllScheduleList() {


        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_SCHEDULES, mColumns, null, null, null, null, null);

        ArrayList<ScheduleModel> campaignList = new ArrayList<>();
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
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
                    jsonObject.put("sTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_S_TIME)));
                    jsonObject.put("eTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_E_TIME)));
                    jsonObject.put("campaignId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_CAMPAIGN_ID)));
                    jsonObject.put("jobId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_JOB_ID)));

                    ScheduleModel model = new ScheduleModel(jsonObject);
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

    public ScheduleModel getCurrentAvailableCampaign() {

        ScheduleModel scheduleModel = null;
        open();

        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("_id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_ID)));
                    jsonObject.put("startDate", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_START_DATE)));
                    jsonObject.put("endDate", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_END_DATE)));
                    jsonObject.put("sTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_S_TIME)));
                    jsonObject.put("eTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_E_TIME)));
                    jsonObject.put("startTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_START_TIME)));
                    jsonObject.put("endTime", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_END_TIME)));
                    jsonObject.put("campaignId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_CAMPAIGN_ID)));
                    jsonObject.put("jobId", cursor.getString(cursor
                            .getColumnIndex(DBConstants.SCHEDULES_JOB_ID)));

                    scheduleModel = new ScheduleModel(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("haaaa no rec", "....");
        }

        cursor.close();
        close();
        return scheduleModel;
    }


    public int updateScheduleData(ScheduleModel model) {

        int updateValue = -1;
        ContentValues values = new ContentValues();

        values.put(DBConstants.SCHEDULES_START_DATE, model.getStartDate());
        values.put(DBConstants.SCHEDULES_END_DATE, model.getEndDate());
        values.put(DBConstants.SCHEDULES_S_TIME, model.getsTime());
        values.put(DBConstants.SCHEDULES_E_TIME, model.geteTime());
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

    public int deleteScheduleById(String schedules_id) {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_SCHEDULES, DBConstants.SCHEDULES_ID + " = ?",
                new String[]{"" + schedules_id});
        close();
        return deleteValue;
    }

    public int deleteScheduleByJobId(int job_id) {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_SCHEDULES, DBConstants.SCHEDULES_JOB_ID + " = ?",
                new String[]{"" + job_id});
        close();
        return deleteValue;
    }

    public int deleteOldSchudules() {
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = DATE_FORMAT.format(today);
        Log.d("currentDate", ",,,,,,,,," + date);

        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_SCHEDULES, DBConstants.SCHEDULES_START_DATE + "< ?",
                new String[]{"" + date});
        close();
        return deleteValue;
    }

    public int deleteAllSchedules() {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_SCHEDULES, null, null);
        close();
        return deleteValue;
    }
}
