package com.perseverance.phando.db;

import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.perseverance.phando.db.dao.RoomDataTypeConvertor;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TrilokiNath on 18-09-2017.
 */
public class BaseVideo implements Serializable {
    @NotNull
    @SerializedName("id")
    private String entryId="";

    @SerializedName("title")
    private String title;

    @SerializedName("detail")
    private String description;

    @SerializedName("tags")
    private String tags;

    @SerializedName("type")
    private String mediaType;


    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("poster")
    private String poster;

    @SerializedName("path")
    private String path;

    @SerializedName("duration")
    private int duration;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("publish_year")
    private String publishYear;

    @SerializedName("series_name")
    private String seriesName;

    @SerializedName("stream_type")
    private String streamType;

    @SerializedName("cdn_url1")
    private String cdnUrl;

    @SerializedName("is_free")
    private int isFree;

    @SerializedName("is_wishlist")
    private int isWishlist;



    @SerializedName("maturity_rating")
    private String maturity_rating = "4.2";

    @SerializedName("rating")
    private String rating = "4.2";


    @SerializedName("last_watch_time")
    private int lastWatchTime = 0;

    @TypeConverters(RoomDataTypeConvertor.class)
    @SerializedName("genres")
    private ArrayList<String> genre = new ArrayList<>();

    public int getLastWatchTime() {
        return lastWatchTime;
    }

    public void setLastWatchTime(int lastWatchTime) {
        this.lastWatchTime = lastWatchTime;
    }

    @SerializedName("circular_thumbnail")
    private String circular_thumbnail;

    public String getCircular_thumbnail() {
        return circular_thumbnail;
    }

    public void setCircular_thumbnail(String circular_thumbnail) {
        this.circular_thumbnail = circular_thumbnail;
    }


    private String videoUrl;
    private String videoAdUrl;
    private int languageCode;

    public BaseVideo(BaseVideo video) {
        this.entryId = video.entryId;
        this.title = video.title;

        this.description = video.description;
        this.tags = video.tags;
        this.thumbnail = video.thumbnail;
        this.path = video.path;
        this.duration = video.duration;
        this.cdnUrl = video.cdnUrl;
        this.isFree = video.isFree;
        this.isWishlist = video.isWishlist;
        this.rating = video.rating;
        this.maturity_rating = video.maturity_rating;
        this.publishYear = video.publishYear;
        this.mediaType = video.mediaType;
        this.genre = video.genre;
        this.mediaType = video.mediaType;
    }

    public BaseVideo() {
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoAdUrl() {
        return videoAdUrl;
    }

    public void setVideoAdUrl(String videoAdUrl) {
        this.videoAdUrl = videoAdUrl;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }



    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
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

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(int languageCode) {
        this.languageCode = languageCode;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCdnUrl() {
        return cdnUrl;
    }

    public void setCdnUrl(String cdnUrl) {
        this.cdnUrl = cdnUrl;
    }


    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getIsWishlist() {
        return isWishlist;
    }

    public void setIsWishlist(int isWishlist) {
        this.isWishlist = isWishlist;
    }

    public String getMaturity_rating() {
        return maturity_rating;
    }

    public void setMaturity_rating(String maturity_rating) {
        this.maturity_rating = maturity_rating;
    }

    @Override
    public String toString() {
        return "BaseVideo{" +
                "entryId='" + entryId + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", poster='" + poster + '\'' +
                ", rating='" + rating + '\'' +
                ", genre=" + genre +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoAdUrl='" + videoAdUrl + '\'' +
                ", languageCode=" + languageCode +
                '}';
    }

    public String getFormatedDuration() {
       int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString;
    }
}
