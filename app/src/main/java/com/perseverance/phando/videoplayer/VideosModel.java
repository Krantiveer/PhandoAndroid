package com.perseverance.phando.videoplayer;

import com.perseverance.phando.db.Category;
import com.perseverance.phando.db.Video;
import com.perseverance.phando.retrofit.SuperModel;

import java.util.List;

/**
 * Created by QAIT\TrilokiNath on 29/3/18.
 */

public class VideosModel extends SuperModel {

    private List<Video> videos;
    private Category category;
    private int pageCount;

    public VideosModel(List<Video> videos, Category category, int pageCount, Throwable throwable) {
        super(throwable);
        this.videos = videos;
        this.category = category;
        this.pageCount = pageCount;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
