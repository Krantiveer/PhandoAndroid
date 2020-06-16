package com.perseverance.phando.search;

import com.perseverance.phando.db.Video;
import com.perseverance.phando.retrofit.SuperView;

import java.util.List;

/**
 * Created by TrilokiNath on 20-09-2017.
 */

public interface SearchView extends SuperView {
    void onSearchResultSuccess(List<Video> videos);

    void onSearchResultError(String errorMessage);
}
