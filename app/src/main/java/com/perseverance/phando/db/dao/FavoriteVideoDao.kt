package com.perseverance.phando.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.FavoriteVideo

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface FavoriteVideoDao {


    @Query("SELECT * FROM FavoriteVideo WHERE languageCode =:languageCode")
    fun getFavoriteVideos(languageCode: Int): LiveData<List<FavoriteVideo>>

    @Query("SELECT * FROM FavoriteVideo WHERE entryId = :id AND languageCode =:languageCode")
    fun getFavoriteVideosLiveData(id:String,languageCode: Int): LiveData<FavoriteVideo>

    @Query("SELECT * FROM FavoriteVideo WHERE entryId = :id AND languageCode =:languageCode")
    fun getFavoriteVideos(id:String,languageCode: Int): FavoriteVideo

    @Query("DELETE FROM FavoriteVideo WHERE entryId = :id AND languageCode =:languageCode")
    fun deleteFavoriteVideos(id:String,languageCode: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: FavoriteVideo)


}
