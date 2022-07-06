package com.perseverance.phando.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by TrilokiNath on 11/12/15.
 */
@Entity
public class Language implements Serializable {

    @PrimaryKey
    @NotNull
    @SerializedName("id")
    private String id = "";

    @SerializedName("language")
    private String language;

    @SerializedName("isSelected")
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language1 = (Language) o;
        return id.equals(language1.id) &&
                language.equals(language1.language) &&
                isSelected == language1.isSelected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, isSelected);
    }
}
