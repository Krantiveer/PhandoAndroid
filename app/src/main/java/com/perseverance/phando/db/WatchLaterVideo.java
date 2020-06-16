package com.perseverance.phando.db;

import androidx.room.Entity;

/**
 * Created by TrilokiNath on 18-09-2017.
 */
@Entity(primaryKeys = {"entryId"})
public class WatchLaterVideo extends BaseVideo{

     public WatchLaterVideo(BaseVideo video){
          super(video);
     }
     public WatchLaterVideo(){
     }

}
