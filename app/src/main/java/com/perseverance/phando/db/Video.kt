package com.perseverance.phando.db

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
        var id: Int? = null,
        var title: String? = null,
        var type: String? = null,
        val circular_thumbnail: String? = null,
        var thumbnail: String? = null,
        var description: String? = null,
        val detail: String? = null,
        val duration: Int? = null,
        val poster: String? = null,
        val genres: List<String>? = null,
        var is_free: Int? = null,
        val is_live: Int? = null,
        val keyword: String? = null,
        val last_watch_time: Int? = null,
        val maturity_rating: String? = null,
        val poster_vertical: String? = null,
        val price: Int? = null,
        var rating: Int? = null,
        val thumbnail_vertical: String? = null
) : Parcelable {

    fun getFormatedDuration(): String? {
        val hours = duration!! / 3600
        val minutes = duration % 3600 / 60
        val seconds = duration % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}