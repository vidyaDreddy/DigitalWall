package com.bizooku.database;

/**
 * Created by Shankar
 * on 8/9/2017.
 */

public class DBConstants {

    /**
     * Beacon events table details
     */

    public static final String TABLE_ASSETS = "assets";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ASSET_ID = "asset_id";
    public static final String COLUMN_ASSET_TYPE = "asset_type";
    public static final String COLUMN_ASSET_URL = "asset_url";
    public static final String COLUMN_ASSET_DURATION = "asset_duration";


    public static final String CREATE_TABLE_ASSET= "create table " + TABLE_ASSETS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_ASSET_ID + " text not null, " +
            COLUMN_ASSET_TYPE + " text not null, " +
            COLUMN_ASSET_URL + " text not null, " +
            COLUMN_ASSET_DURATION + " long not null);";
}
