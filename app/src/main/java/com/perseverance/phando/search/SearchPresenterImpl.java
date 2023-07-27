package com.perseverance.phando.search;

import com.perseverance.phando.constants.BaseConstants;
import com.perseverance.phando.db.Video;
import com.perseverance.phando.home.dashboard.models.DataFilters;
import com.perseverance.phando.retrofit.ApiClient;
import com.perseverance.phando.retrofit.ApiService;
import com.perseverance.phando.retrofit.NullResponseError;
import com.perseverance.phando.utils.MyLog;
import com.perseverance.phando.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by TrilokiNath on 20-09-2017.
 */

public class SearchPresenterImpl implements SearchPresenter, Callback<List<Video>> {

    private SearchView listener;

    public SearchPresenterImpl(SearchView listener) {
        this.listener = listener;
    }

    @Override
    public void search(int pageCount, String query, boolean showProgress, DataFilters dataFilters) {
        if (showProgress) listener.showProgress("Searching videos...");
        ApiService service = ApiClient.getLoginClient().create(ApiService.class);
        Call<List<Video>> call = service.searchVideo(query,"android", dataFilters.getGenre_id(), dataFilters.getFilter(), pageCount + "," + BaseConstants.LIMIT_VIDEOS);
        MyLog.e("Search Video Url: " + call.request().url().toString());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
        if (response == null || response.body() == null) {
            onFailure(call, new NullResponseError());
        } else {
            listener.dismissProgress();
            MyLog.e("Response: " + response.body().toString());
            listener.onSearchResultSuccess(response.body());
        }
    }

    @Override
    public void onFailure(Call<List<Video>> call, Throwable t) {
        listener.dismissProgress();
        listener.onSearchResultError(Utils.getErrorMessage(t));
    }
}
