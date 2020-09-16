package com.perseverance.phando.payment.paymentoptions

data class WalletDetailResponseData(
        val status: String,
        val message: String,
        val `data`: WalletDetail?=null,
        val deactivate_wallet_msg: String = "",
        val hint1: String = "",
        val hint2: String = ""
)