package com.perseverance.phando.AdsUtil;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdModelDao {

    @Query("SELECT * FROM ad_table")
    List<AdModel> getAll();

    @Query("SELECT * FROM ad_table WHERE screenId IN (:screenId) ORDER BY priority asc")
    List<AdModel> loadAllByIds(String screenId);

    @Query("SELECT * FROM ad_table WHERE screenId IN (:screenId)")
    List<AdModel> loadAllById(String screenId);

    @Insert
    void insertAll(List<AdModel> adModelList);

    @Delete
    void delete(AdModel adModel);

    @Query("DELETE FROM ad_table")
    void deleteAll();
}
