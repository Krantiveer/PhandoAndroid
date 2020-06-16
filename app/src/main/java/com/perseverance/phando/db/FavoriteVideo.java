package com.perseverance.phando.db;

import androidx.room.Entity;

/**
 * Created by TrilokiNath on 18-09-2017.
 */
@Entity(primaryKeys = {"entryId"})
public class FavoriteVideo extends BaseVideo {

    public FavoriteVideo(BaseVideo video) {
        super(video);
    }

    public FavoriteVideo() {
    }


}
