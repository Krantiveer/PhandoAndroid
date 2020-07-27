package com.perseverance.phando.notification

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by trilokinathyadav on 2/15/2018.
 */

@Dao
interface NotificationDao {


    @Query("SELECT * FROM NotificationData")
    fun getNotifications(): LiveData<List<NotificationData>>

    @Query("SELECT * FROM NotificationData")
    fun getAllNotifications(): List<NotificationData>

    @Query("DELETE FROM NotificationData WHERE dbID = :id")
    fun deleteNotifications(id:String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: NotificationData)


}
