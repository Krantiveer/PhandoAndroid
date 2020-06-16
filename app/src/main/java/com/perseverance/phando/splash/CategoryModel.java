package com.perseverance.phando.splash;

import com.perseverance.phando.db.Category;
import com.perseverance.phando.retrofit.SuperModel;

import java.util.List;

/**
 * Created by QAIT\TrilokiNath on 28/3/18.
 */

public class CategoryModel extends SuperModel {

    private List<Category> categories;

    public CategoryModel(List<Category> categories, Throwable throwable) {
        super(throwable);
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
