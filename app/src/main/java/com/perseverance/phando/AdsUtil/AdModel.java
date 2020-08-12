package com.perseverance.phando.AdsUtil;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "ad_table")
public class AdModel {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id = "-1";

    @SerializedName("ad_type")
    private String adType;

    @SerializedName("pub_id")
    private String publisherId;

    @SerializedName("screen_id")
    private String screenId;

    @SerializedName("adunit")
    private String adUnit;

    @SerializedName("count")
    private String count;

    @SerializedName("adsinterval")
    private String adsInterval;

    @SerializedName("partner")
    private String partnerName;

    @SerializedName("app_id")
    private String appId;

    @SerializedName("priority")
    private String priority;

    @SerializedName("status")
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getAdUnit() {
        return adUnit;
    }

    public void setAdUnit(String adUnit) {
        this.adUnit = adUnit;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAdsInterval() {
        return adsInterval;
    }

    public void setAdsInterval(String adsInterval) {
        this.adsInterval = adsInterval;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AdModel{" +
                ", adType='" + adType + '\'' +
                ", publisherId='" + publisherId + '\'' +
                ", screenId='" + screenId + '\'' +
                ", partnerName='" + partnerName + '\'' +
                ", appId='" + appId + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
