package com.digitalwall.database;

/**
 * Created by Shankar
 * on 8/9/2017.
 */

public class DBConstants {

    /**
     * Beacon events table details
     */

    public static final String TABLE_ASSETS = "digital_assets";
    public static final String TABLE_CHANNELS = "digital_channels";

    public static final String CHANNELS_ID = "_id";
    public static final String COLUMN_ASSET_ID = "asset_id";
    public static final String COLUMN_ASSET_TYPE = "asset_type";
    public static final String COLUMN_ASSET_URL = "asset_url";
    public static final String COLUMN_ASSET_DURATION = "asset_duration";
    public static final String COLUMN_ASSET_LOCAL_URL = "asset_local_url";

    public static final String CHANNEL_HEIGHT = "height";
    public static final String CHANNEL_WIDTH = "width";
    public static final String CHANNEL_COLOR = "color";
    public static final String CHANNEL_LEFT = "left";
    public static final String CHANNEL_TOP = "top";
    public static final String CHANNEL_VOLUME = "volume";


    /*In db version 11 added one fields in this table i.e. KEY_BRAND_LINK_REWRITE */
    public static final String CREATE_TABLE_COLUMNS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CHANNELS
            + "("
            + CHANNELS_ID
            + " TEXT NOT NULL PRIMARY KEY, "
            + CHANNEL_HEIGHT
            + " TEXT, "
            + CHANNEL_COLOR
            + " TEXT, "
            + CHANNEL_LEFT
            + " TEXT, "
            + CHANNEL_TOP
            + " TEXT, "
            + CHANNEL_VOLUME
            + " TEXT, "
            + CHANNEL_WIDTH + " TEXT " + ");";


    public static final String CREATE_TABLE_ASSET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ASSETS
            + "("
            + COLUMN_ASSET_ID
            + " TEXT NOT NULL PRIMARY KEY, "
            + COLUMN_ASSET_TYPE
            + " TEXT NOT NULL, "
            + COLUMN_ASSET_URL
            + " TEXT NOT NULL, "
            + COLUMN_ASSET_LOCAL_URL
            + " TEXT NOT NULL, "
            + COLUMN_ASSET_DURATION
            + " TEXT NOT NULL, "
            + CHANNELS_ID
            + " TEXT NOT NULL, "
            + "FOREIGN KEY ( "
            + CHANNELS_ID
            + " ) REFERENCES "
            + TABLE_CHANNELS
            + " ( " + CHANNELS_ID + " )" + ");";

}
/*"CREATE TABLE IF NOT EXISTS " + TABLE_ASSETS + "(" +
            CHANNELS_ID + " integer primary key autoincrement, " +
            COLUMN_ASSET_ID + " text not null, " +
            COLUMN_ASSET_TYPE + " text not null, " +
            COLUMN_ASSET_URL + " text not null, " +
            COLUMN_ASSET_DURATION + " long not null);";*/