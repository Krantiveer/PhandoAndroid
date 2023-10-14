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
        val is_review: Int,
        val payid: Any,
        val payment: Any,
        val subscription_start_date: String,
        val subscription_end_date: String,
        val current_subscription: SubscriptionData,
        val package_name: String,
        val price: Int,
        val whatsapp_no: String,
        val whatsapp_text: String,
        val email: String,
        val start: Any,
        val user: User,
        val is_language_set: Boolean,
        val user_downloads: List<DownloadMetadata>,
        val preferred_language:List<Language> = arrayListOf()
)

data class SubscriptionData(
        val id: String,
        val payment_id: String,
        val plan: Plan,


        )

data class Plan(
        val currency: String


)