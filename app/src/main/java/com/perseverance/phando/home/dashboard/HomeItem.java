package com.perseverance.phando.home.dashboard;

import com.perseverance.phando.db.Category;

import java.util.List;

/**
 * Created by QAIT\TrilokiNath on 10/1/18.
 */

public class HomeItem {
    private Category category;
    private List homeItems;
    private ViewType viewType = ViewType.HOME_ITEM_VIEW;


    public HomeItem(Category category, List homeItems) {
        this.category = category;
        this.homeItems = homeItems;
    }

    public HomeItem() {

    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List getHomeItems() {
        return homeItems;
    }

    public void setHomeItems(List homeItems) {
        this.homeItems = homeItems;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }
}
