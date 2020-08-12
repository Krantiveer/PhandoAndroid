package com.perseverance.phando.home.profile

data class PaypalSubscription(
        val card_id: Any,
        val created_at: String,
        val id: Int,
        val method: String,
        val package_id: Int,
        val payment_id: String,
        val price: Int,
        val status: Int,
        val subscription_from: String,
        val subscription_to: String,
        val updated_at: String,
        val user_id: Int,
        val user_name: String
)