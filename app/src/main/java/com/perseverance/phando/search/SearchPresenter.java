package com.perseverance.phando.search;

import com.perseverance.phando.home.dashboard.models.DataFilters;

/**
 * Created by TrilokiNath on 20-09-2017.
 */

public interface SearchPresenter {
    void search(int pageCount, String query, boolean showProgress, DataFilters dataFilters);
}
