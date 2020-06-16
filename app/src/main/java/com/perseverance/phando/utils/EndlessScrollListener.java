package com.perseverance.phando.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.perseverance.phando.constants.BaseConstants;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    public static String TAG = EndlessScrollListener.class.getSimpleName();

    private int previousTotal; // The total number of items in the dataset after the last load
    private boolean loading; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
        init();
    }

    public void init() {
        currentPage = 0;
        previousTotal = 0;
        loading = true;
        visibleThreshold = 1;
    }

    public void rollback(int page) {
        currentPage = page;
        previousTotal = 0;
        loading = true;
        visibleThreshold = 1;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            //MyLog.e("totalItemCount: " + totalItemCount + " previousTotal: " + previousTotal);
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            currentPage+= BaseConstants.LIMIT_VIDEOS;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);
}