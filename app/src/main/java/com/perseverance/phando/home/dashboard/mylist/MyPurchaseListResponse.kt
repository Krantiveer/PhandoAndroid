package com.perseverance.phando.home.dashboard.mylist

import com.perseverance.phando.db.Language
import com.perseverance.phando.db.Video

data class MyPurchaseListResponse(
        val message: String,
        val status: String,
        val data:List<Video> = arrayListOf()
)