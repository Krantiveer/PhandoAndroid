package com.perseverance.phando.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.APIData

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface APIDataDao {

    @Query("SELECT * FROM APIData WHERE url =:url")
    fun getDataForUrl(url: String): APIData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apiData: APIData)

    @Query("DELETE FROM APIData")
    fun deleteAll()

}
