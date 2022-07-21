package com.perseverance.phando.home.profile

data class User(
        val braintree_id: Any,
        val card_brand: Any,
        val card_last_four: Any,
        val code: Any,
        val created_at: String,
        val deleted_at: Any,
        val dob: Any,
        val paypal_subscriptions: ArrayList<Any>,
        val email: String,
        val facebook_id: Any,
        val google_id: Any,
        val id: Int,
        val image: String,
        val is_admin: Int,
        val lastname: String,
        val mobile: String,
        val name: String,
        val payment_custom_token: String,
        val payment_custom_token_expiry: String,
        val stripe_id: Any,
        val trial_ends_at: Any,
        val updated_at: String,
        val verification_token: Any,
        val verified: Int
)