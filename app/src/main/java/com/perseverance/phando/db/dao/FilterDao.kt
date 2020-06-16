package com.perseverance.phando.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.Filter

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface FilterDao {

    @Query("SELECT * FROM Filter")
    fun allFilter(): List<Filter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Filter>)

    @Query("DELETE FROM Filter")
    fun deleteAll()

}
