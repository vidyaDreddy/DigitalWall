package com.digitalwall.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.digitalwall.model.ChannelModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ColumnSource {

    private SQLiteDatabase mDatabase;
    private DatabaseHandler mHandler;
    private Context mContext;
    private String mColumns[] = {DBConstants.CHANNELS_ID,
            DBConstants.CHANNEL_HEIGHT, DBConstants.CHANNEL_WIDTH,
            DBConstants.CHANNEL_COLOR, DBConstants.CHANNEL_LEFT, DBConstants.CHANNEL_TOP, DBConstants.CHANNEL_VOLUME};

    public ColumnSource(Context context) {
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

    public long insertData(ChannelModel model) {
        long insertValue = -1;

        ContentValues values = new ContentValues();
        values.put(DBConstants.CHANNELS_ID, model.getChannelId());
        values.put(DBConstants.CHANNEL_HEIGHT, model.getChannelHeight());
        values.put(DBConstants.CHANNEL_WIDTH, model.getChannelWidth());
        values.put(DBConstants.CHANNEL_COLOR, model.getChannelColor());
        values.put(DBConstants.CHANNEL_LEFT, model.getChannelLeftMargin());
        values.put(DBConstants.CHANNEL_TOP, model.getChannelTopMargin());
        values.put(DBConstants.CHANNEL_VOLUME, model.getVolume());
        open();
        insertValue = mDatabase.insert(DBConstants.TABLE_CHANNELS, null,
                values);
        close();

        return insertValue;
    }

    public boolean isChannelDataAvailable(ChannelModel model) {
        boolean isChannelAvailable = false;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CHANNELS, new String[]{DBConstants.CHANNELS_ID},
                DBConstants.CHANNELS_ID + " = ?", new String[]{model.getChannelId()}, null,
                null, null);
        if (cursor.getCount() > 0) {
            isChannelAvailable = true;
        }
        cursor.close();
        close();
        return isChannelAvailable;
    }

    /* Get all brand models */
    public ArrayList<ChannelModel> selectAll() {
        ArrayList<ChannelModel> channelsList = null;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CHANNELS, mColumns,
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            channelsList = new ArrayList<ChannelModel>();
            while (cursor.moveToNext()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNELS_ID)));
                    jsonObject.put("height", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNEL_HEIGHT)));
                    jsonObject.put("width", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNEL_WIDTH)));
                    jsonObject.put("left", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNEL_LEFT)));
                    jsonObject.put("top", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNEL_TOP)));
                    jsonObject.put("volume", cursor.getString(cursor
                            .getColumnIndex(DBConstants.CHANNEL_VOLUME)));
                    ChannelModel model = null;
                    model = new ChannelModel(jsonObject);
                    channelsList.add(model);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        close();
        return channelsList;
    }


    /* Get brand count */
    public int getColumnDataCount() {
        int brandCount = -1;
        open();
        Cursor cursor = mDatabase.query(DBConstants.TABLE_CHANNELS, new String[]{DBConstants.CHANNELS_ID},
                null, null, null, null, null);
        if (cursor.getCount() > 0) {
            brandCount = cursor.getCount();
        }
        cursor.close();
        close();
        return brandCount;
    }

    /* Update brands depends on brand name */
    public int updateChannelData(ChannelModel model) {
        int updateValue = -1;
        ContentValues values = new ContentValues();

        values.put(DBConstants.CHANNELS_ID, model.getChannelId());
        values.put(DBConstants.CHANNEL_HEIGHT, model.getChannelHeight());
        values.put(DBConstants.CHANNEL_WIDTH, model.getChannelWidth());
        values.put(DBConstants.CHANNEL_COLOR, model.getChannelColor());
        values.put(DBConstants.CHANNEL_LEFT, model.getChannelLeftMargin());
        values.put(DBConstants.CHANNEL_TOP, model.getChannelTopMargin());
        values.put(DBConstants.CHANNEL_VOLUME, model.getVolume());

        open();
        updateValue = mDatabase.update(DBConstants.TABLE_CHANNELS, values,
                DBConstants.CHANNELS_ID + " = ?", new String[]{model.getChannelId()});
        close();
        return updateValue;
    }

    public int deleteAll() {
        int deleteValue = -1;
        open();
        deleteValue = mDatabase.delete(DBConstants.TABLE_CHANNELS, null, null);
        close();
        return deleteValue;
    }
}