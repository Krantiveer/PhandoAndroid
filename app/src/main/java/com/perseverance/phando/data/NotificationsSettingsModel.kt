package com.perseverance.phando.data

import com.perseverance.phando.db.Video

data class NotificationsSettingsModel(
    val message: String,
    val status: String,
    val data:ArrayList<NotificationsData> = arrayListOf()
)

data class NotificationsData(
    val id: String,
    val title: String,
    var value: Boolean
)