package com.perseverance.phando.notification

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.perseverance.phando.db.Video
import kotlinx.android.parcel.Parcelize

/**
 * Created by TrilokiNath on 18-09-2017.
 */
@Parcelize
@Entity
data class NotificationData (
        var id: Int?=null,
        var title: String?=null,
        var type: String?=null,
        val circular_thumbnail: String?=null,
        var thumbnail: String?=null,
        val description: String?=null,
        val detail: String?=null,
        val duration: Int?=null,
        val poster: String?=null,
        val genres: List<String>?=null,
        var is_free: Int?=null,
        val is_live: Int?=null,
        val keyword: String?=null,
        val last_watch_time: Int?=null,
        val maturity_rating: String?=null,
        val poster_vertical: String?=null,
        val price: Int?=null,
        val rating: Int?=null,
        val thumbnail_vertical: String?=null
) : Parcelable {

    fun getFormatedDuration(): String? {
        val hours = duration!! / 3600
        val minutes = duration % 3600 / 60
        val seconds = duration % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}