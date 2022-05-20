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


    @Query("SELECT * FROM NotificationData ORDER BY dbID DESC")
    fun getNotifications(): LiveData<List<NotificationData>>

    @Query("SELECT COUNT(*) FROM NotificationData WHERE read =0")
    fun getUnreadNotifications(): Int

    @Query("UPDATE NotificationData SET read = 1 WHERE  dbID = :id")
    fun markNotificationRead(id: Long)

    @Query("UPDATE NotificationData SET read = 1")
    fun markAllNotificationRead()

    @Query("SELECT COUNT(*) FROM NotificationData")
    fun getAllNotifications(): Int

    @Query("DELETE FROM NotificationData WHERE dbID = :id")
    fun deleteNotifications(id: Long): Int

    @Query("DELETE FROM NotificationData")
    fun deleteAllNotifications()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: NotificationData)


}
