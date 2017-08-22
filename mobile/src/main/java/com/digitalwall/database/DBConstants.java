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
    public static final String TABLE_CAMPAIGN = "digital_campaign";


    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String CAMPAIGN_NAME = "campaign_name";
    public static final String CAMPAIGN_CLIENT_ID = "campaign_client_id";
    public static final String CAMPAIGN_TYPE = "campaign_type";
    public static final String CAMPAIGN_ORIENTATION = "campaign_orientation";


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
    public static final String CREATE_TABLE_CAMPAIGN = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CAMPAIGN
            + "("
            + CAMPAIGN_ID
            + " TEXT NOT NULL PRIMARY KEY, "
            + CAMPAIGN_NAME
            + " TEXT, "
            + CAMPAIGN_TYPE
            + " TEXT, "
            + CAMPAIGN_CLIENT_ID
            + " TEXT, "
            + CAMPAIGN_ORIENTATION + " TEXT " + ");";


    /*In db version 11 added one fields in this table i.e. KEY_BRAND_LINK_REWRITE */
    public static final String CREATE_TABLE_CHANNEL = "CREATE TABLE IF NOT EXISTS "
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
            + CHANNEL_WIDTH
            + " TEXT, "
            + CAMPAIGN_ID
            + " TEXT NOT NULL, "
            + "FOREIGN KEY ( "
            + CAMPAIGN_ID
            + " ) REFERENCES "
            + TABLE_CAMPAIGN
            + " ( " + CAMPAIGN_ID + " )" + ");";


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