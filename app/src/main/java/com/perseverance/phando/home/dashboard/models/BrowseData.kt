package com.perseverance.phando.home.dashboard.models

import com.google.gson.annotations.SerializedName
import com.perseverance.phando.db.Video
import java.io.Serializable

data class BrowseData (val displayType: String, val id: Int?,val list: List<Video>, val title: String,val image_orientation:Int?=0)