package com.perseverance.phando.notification

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by TrilokiNath on 18-09-2017.
 */
@Parcelize
@Entity
data class NotificationData(
        @PrimaryKey
        var dbID: Long = -1,
        var id: Int? = null,
        var read: Int? = 0,
        var title: String? = "",
        var type: String? = "",
        var thumbnail: String? = "",
        var description: String? = "",
        var detail: String? = "",
        var duration: Int? = 0,
        var poster: String? = "",
        var free: Int? = 0,
        var maturity_rating: String? = "",
        var poster_vertical: String? = "",
        var rating: Int? = 0
) : Parcelable {
    // constructor():this(dbID=-1,isRead=0,is_free = 0)
    fun getFormatedDuration(): String? {
        val hours = duration!! / 3600
        val minutes = duration!! % 3600 / 60
        val seconds = duration!! % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}