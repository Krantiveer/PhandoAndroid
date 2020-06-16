package com.perseverance.phando.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.Language

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface LanguageDao {

    @Query("SELECT * FROM Language WHERE language =:language")
    fun allLanguage(language: Int): LiveData<List<Language>?>

    @Query("SELECT * FROM Language")
    fun allLanguage(): List<Language>

    @Query("SELECT * FROM Language WHERE language =:language")
    fun allLanguageList(language: Int): List<Language>

    @Query("SELECT * FROM Language WHERE id = :categoryId AND language =:language")
    fun getLanguage(categoryId:String,language: Int): Language

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Language>)

    @Query("DELETE FROM Language")
    fun deleteAll()

    @Query("SELECT DISTINCT id from Language")
    fun getLanguageIdList(): LiveData<List<String>>

    @Query("DELETE FROM Language  WHERE id NOT IN (:categoryIdList)")
    fun deleteOldLanguage(categoryIdList: List<String>)
}
