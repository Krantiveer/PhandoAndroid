package com.perseverance.phando.home.series;

import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.perseverance.phando.db.dao.RoomDataTypeConvertor;
import com.perseverance.phando.retrofit.SuperModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrilokiNath on 19-09-2017.
 */

public class SeriesModel extends SuperModel {

    List<Series> seriesList;

    public SeriesModel(List<Series> seriesList, Throwable throwable) {
        super(throwable);
        this.seriesList = seriesList;
    }

    public class Series {
        @SerializedName("id")
        private String id;

        @SerializedName("season_no")
        private String seasonNumber;

        @SerializedName("thumbnail")
        private String thumbnail;

        @SerializedName("poster")
        private String poster;

        @SerializedName("is_free")
        private int isFree;

        @SerializedName("type")
        private String type;

        @SerializedName("detail")
        private String description;

        @SerializedName("rating")
        private String rating;

        @TypeConverters(RoomDataTypeConvertor.class)
        @SerializedName("genres")
        private ArrayList<String> genre;

        public ArrayList<String> getGenre() {
            return genre;
        }

        public void setGenre(ArrayList<String> genre) {
            this.genre = genre;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(String seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }

        public int getIsFree() {
            return isFree;
        }

        public void setIsFree(int isFree) {
            this.isFree = isFree;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}


