package com.perseverance.phando.payment.paymentoptions

data class WalletDetailResponseData(
        val status: String,
        val message: String,
        val `data`: WalletDetail?=null,
        val deactivate_wallet_msg: String = "",
        val hint1: String = "",
        val hint2: String = "",
        val currency_symbol: String="",
        val currency_code: String="",
        val wallet_conversion_points:Int=1
)