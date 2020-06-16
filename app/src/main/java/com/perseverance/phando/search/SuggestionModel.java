package com.perseverance.phando.search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SuggestionModel implements Serializable {

    @SerializedName("suggestions")
    private List<String> suggestions;

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public String toString() {
        return "SuggestionModel{" +
                "suggestions=" + suggestions +
                '}';
    }
}
