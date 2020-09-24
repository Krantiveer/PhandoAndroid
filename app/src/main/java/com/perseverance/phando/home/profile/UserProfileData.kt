package com.perseverance.phando.home.profile

import com.perseverance.phando.db.Language
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata

data class UserProfileData(
        val active: Int,
        val code: String,
        val current_date: String,
        val end: Any,
        val id: Any,
        val is_subscribed: Int,
        val payid: Any,
        val payment: Any,
        val start: Any,
        val user: User,
        val user_downloads: List<DownloadMetadata>,
        val preferred_language:List<Language> = arrayListOf()
)