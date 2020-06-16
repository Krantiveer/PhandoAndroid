package com.perseverance.phando.home.profile

data class UserProfileData(
    val active: Int,
    val code: String,
    val current_date: String,
    //val current_subscription: CurrentSubscription,
    val end: Any,
    val id: Any,
    val is_subscribed: Int,
    val payid: Any,
    val payment: Any,
    val start: Any,
    val user: User
)