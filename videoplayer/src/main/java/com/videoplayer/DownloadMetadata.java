package com.videoplayer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DownloadMetadata implements Serializable {

    @SerializedName("media_id")
    String media_id;
    @SerializedName("type")
    String type;
    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("thumbnail")
    String thumbnail;
    @SerializedName("media_url")
    String media_url;
    @SerializedName("otherText")
    String otherText;

    public DownloadMetadata(String media_id, String type, String title, String description, String thumbnail, String media_url,String otherText) {
        this.media_id = media_id;
        this.type = type;
        this.description = description;
        this.title = title;
        this.thumbnail = thumbnail;
        this.media_url = media_url;
        this.otherText = otherText;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
    }
}
