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

    @SerializedName("language_text")
    private String language_text;

    @SerializedName("isSelected")
    private boolean isSelected = false;

    @SerializedName("isLanguageSelected")
    private boolean isLanguageSelected = false;

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

    public String getLanguage_text(){
        return language_text;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLanguage_text(String language_text){
        this.language_text = language_text;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language1 = (Language) o;
        return id.equals(language1.id) &&
                language.equals(language1.language) &&
                isLanguageSelected == language1.isLanguageSelected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, language, isLanguageSelected);
    }

    public boolean isLanguageSelected() {
        return isLanguageSelected;
    }

    public void setLanguageSelected(boolean languageSelected) {
        isLanguageSelected = languageSelected;
    }
}
