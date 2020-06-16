package com.perseverance.phando.payment.subscription

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
        @SerializedName("message")
    val message: String,
        @SerializedName("status")
    val status: Int
)