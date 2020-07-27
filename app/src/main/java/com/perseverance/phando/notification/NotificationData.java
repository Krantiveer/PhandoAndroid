package com.perseverance.phando.notification;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.perseverance.phando.db.Video;


/**
 * Created by TrilokiNath on 18-09-2017.
 */
@Entity
public class NotificationData extends Video {
    @PrimaryKey(autoGenerate = true)
    int dbID;

    public NotificationData(BaseVideo video){
        super(video);
    }
    public NotificationData(){
    }
}
