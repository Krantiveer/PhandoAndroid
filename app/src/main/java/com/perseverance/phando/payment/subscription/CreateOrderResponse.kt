package com.perseverance.phando.payment.subscription

data class CreateOrderResponse(
    val app_name: String,
    val description: String,
    val gateway_order_id: String,
    val id: Int,
    val key: String,
    val order_details: PackageInfo,
    val status: Any,
    val user_email: String,
    val user_id: Int,
    val user_mobile: String,
    val is_subscribed: Int
)