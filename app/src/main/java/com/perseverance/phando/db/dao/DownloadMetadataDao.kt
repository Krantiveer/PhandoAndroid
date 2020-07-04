package com.perseverance.phando.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata

@Dao
interface DownloadMetadataDao {

    @Query("SELECT * FROM DownloadMetadata WHERE document_id =:document_id")
    fun getAllDownloadLiveData(document_id: String): DownloadMetadata

    @Query("SELECT * FROM DownloadMetadata WHERE status = 0")
    fun getAllDownloadLiveData(): LiveData<List<DownloadMetadata>>

    @Query("SELECT * FROM DownloadMetadata WHERE status = 0")
    fun getAllDownloadData(): List<DownloadMetadata>

    @Query("SELECT * FROM DownloadMetadata WHERE status = 1")
    fun getDeletedDownload(): List<DownloadMetadata>

    @Query("SELECT * FROM DownloadMetadata")
    fun getAllData(): List<DownloadMetadata>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<DownloadMetadata>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(downloadMetadata: DownloadMetadata)

    @Query("DELETE FROM DownloadMetadata WHERE document_id =:document_id")
    fun deleteById(document_id: String)

    @Query("DELETE FROM DownloadMetadata")
    fun deleteAll()

    @Query("SELECT DISTINCT document_id from DownloadMetadata")
    fun getDownloadIdList(): LiveData<List<String>>

    @Query("DELETE FROM DownloadMetadata  WHERE document_id NOT IN (:idList)")
    fun deleteOldDownload(idList: List<String>)
}