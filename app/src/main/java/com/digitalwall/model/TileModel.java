package com.digitalwall.model;

/**
 * Created by vidhayadhar
 */

public class TileModel {

    private String assetType;
    private String assetUrl;
    private String assetId;
    private String assetAnimation;
    private long assetDuration;


    public TileModel(String assetType, String assetUrl, String assetId, long assetDuration, String assetAnimation) {
        this.assetType = assetType;
        this.assetUrl = assetUrl;
        this.assetId = assetId;
        this.assetDuration = assetDuration;
        this.assetAnimation=assetAnimation;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetUrl() {
        return assetUrl;
    }

    public void setAssetUrl(String assetUrl) {
        this.assetUrl = assetUrl;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public long getAssetDuration() {
        return assetDuration;
    }

    public void setAssetDuration(long assetDuration) {
        this.assetDuration = assetDuration;
    }

    public String getAssetAnimation() {
        return assetAnimation;
    }

    public void setAssetAnimation(String assetAnimation) {
        this.assetAnimation = assetAnimation;
    }
}
