package com.perseverance.phando.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.db.Category

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Category WHERE language =:language")
    fun allCategory(language: Int): LiveData<List<Category>?>

    @Query("SELECT * FROM Category")
    fun allGenres(): List<Category>

    @Query("SELECT * FROM Category WHERE language =:language")
    fun allCategoryList(language: Int): List<Category>

    @Query("SELECT * FROM Category WHERE id = :categoryId AND language =:language")
    fun getCategory(categoryId: String, language: Int): Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Category>)

    @Query("DELETE FROM Category")
    fun deleteAll()

    @Query("SELECT DISTINCT id from Category")
    fun getCategoryIdList(): LiveData<List<String>>

    @Query("DELETE FROM Category  WHERE id NOT IN (:categoryIdList)")
    fun deleteOldCategory(categoryIdList: List<String>)
}
