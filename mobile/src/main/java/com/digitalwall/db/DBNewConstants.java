package com.digitalwall.db;

/**
 * Created by Shankar
 * on 8/9/2017.
 */

public class DBNewConstants {


    public static final String TABLE_ASSETS = "digital_assets";
    public static final String TABLE_CHANNELS = "digital_channels";


    /*ASSETS TABLE*/
    public static final String ASSET_CHANNEL_ID = "asset_channel_id";
    public static final String ASSET_ID = "asset_id";
    public static final String ASSET_DOWNLOAD_ID = "asset_id";
    public static final String ASSET_TYPE = "asset_type";
    public static final String ASSET_URL = "asset_url";
    public static final String ASSET_DURATION = "asset_duration";
    public static final String ASSET_LOCAL_URL = "asset_local_url";
    public static final String ASSET_ANIMATION = "asset_animation";

    public static final String CREATE_TABLE_ASSET = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ASSETS
            + "("
            + ASSET_ID
            + " TEXT NOT NULL PRIMARY KEY, "
            + ASSET_TYPE
            + " TEXT NOT NULL, "
            + ASSET_DOWNLOAD_ID
            + " TEXT NOT NULL, "
            + ASSET_URL
            + " TEXT NOT NULL, "
            + ASSET_LOCAL_URL
            + " TEXT NOT NULL, "
            + ASSET_DURATION
            + " TEXT NOT NULL, "
            + ASSET_ANIMATION
            + " TEXT NOT NULL, "
            + ASSET_CHANNEL_ID
            + " TEXT NOT NULL, "
            + "FOREIGN KEY ( "
            + ASSET_CHANNEL_ID
            + " ) REFERENCES "
            + TABLE_CHANNELS
            + " ( " + ASSET_CHANNEL_ID + " )" + ");";


}