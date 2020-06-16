package com.perseverance.phando.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.WatchLaterVideo

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface WatchLaterVideoDao {



    @Query("SELECT * FROM WatchLaterVideo WHERE languageCode =:languageCode")
    fun getWatchLaterVideos(languageCode:Int): LiveData<List<WatchLaterVideo>>

    @Query("SELECT * FROM WatchLaterVideo WHERE entryId = :id AND languageCode =:languageCode")
    fun getWatchLaterVideosLiveData(id:String,languageCode: Int): LiveData<WatchLaterVideo>

    @Query("SELECT * FROM WatchLaterVideo WHERE entryId = :id AND languageCode =:languageCode")
    fun getWatchLaterVideos(id:String,languageCode: Int): WatchLaterVideo

    @Query("DELETE FROM WatchLaterVideo WHERE entryId = :id AND languageCode =:languageCode")
    fun deleteWatchLaterVideos(id:String,languageCode: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: WatchLaterVideo)

    @Query("DELETE FROM WatchLaterVideo")
    fun deleteAll()

}
