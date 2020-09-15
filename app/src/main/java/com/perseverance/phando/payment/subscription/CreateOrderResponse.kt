package com.perseverance.phando.payment.subscription

data class CreateOrderResponse(
        val app_name: String?=null,
        val description: String?=null,
        val gateway_order_id: String?=null,
        val id: Int?=null,
        val key: String?=null,
        val order_details: PackageInfo?=null,
        val status: String?=null,
        val message: String?=null,
        val user_email: String?=null,
        val user_id: Int?=null,
        val user_mobile: String?=null,
        val is_subscribed: Int?=null
)