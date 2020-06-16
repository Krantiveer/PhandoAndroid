package com.perseverance.phando.home.dashboard.models

import com.google.gson.annotations.SerializedName
import com.perseverance.phando.db.Video
import java.io.Serializable

data class BrowseData (
        @SerializedName("displayType")
        val displayType: String,

        @SerializedName("id")
        val id: Int,

        @SerializedName("list")
        val list: List<Video>,

        @SerializedName("title")
        val title: String
): Serializable