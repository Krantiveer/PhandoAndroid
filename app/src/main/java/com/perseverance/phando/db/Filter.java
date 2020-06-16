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
    private String key ="";

    @SerializedName("title")
    private String title;

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
}
