package com.perseverance.phando.home.profile.login

data class VerifyOtpParam(
        val country_code: String,
        val mobile: String,
    val otp: String
)