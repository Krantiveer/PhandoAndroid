package com.perseverance.phando.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.home.profile.model.CountryCode

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface CountryCodeDao {

    @Query("SELECT * FROM CountryCode")
    fun getAll(): List<CountryCode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<CountryCode>)

    @Query("DELETE FROM CountryCode")
    fun deleteAll()

}
