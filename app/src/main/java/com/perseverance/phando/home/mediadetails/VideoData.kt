package com.perseverance.phando.home.mediadetails

import com.google.gson.annotations.SerializedName

/**
 * Created by QAIT\Triloki Nath on 22/3/18.
 */
data class VideoData(
        @SerializedName("title")
        val title: String?,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("videoUrl")
        val videoUrl: String,
        @SerializedName("videoAdUrl")
        val videoAdUrl: String,
        @SerializedName("seekTo")
        var seekTo: Long = 0
)