package com.perseverance.phando.search;

import java.util.List;

public interface FetchSuggestionsListener {

    void onFetchedSuggestor(SuggestionModel result);

    void onFetchedSuggestor(List<String> suggestions);

    void onErrorFetchedSuggestor(String errorMessage);

    void onQueryCompleted(String query);
}
