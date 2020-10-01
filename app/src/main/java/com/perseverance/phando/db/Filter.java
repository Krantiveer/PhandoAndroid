package com.perseverance.phando.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created by TrilokiNath on 11/12/15.
 */
@Entity
public class Filter implements Serializable {

    @PrimaryKey
    @NotNull
    @SerializedName("key")
    private String key = "";

    @SerializedName("title")
    private String title;

    @SerializedName("filter_type")
    private String filter_type;


    @NotNull
    public String getKey() {
        return key;
    }

    public void setKey(@NotNull String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilter_type() {
        return filter_type;
    }

    public void setFilter_type(String filter_type) {
        this.filter_type = filter_type;
    }
}
