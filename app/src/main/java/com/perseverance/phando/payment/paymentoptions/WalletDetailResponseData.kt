package com.perseverance.phando.payment.paymentoptions

data class WalletDetailResponseData(
        val status: String,
        val message: String,
        val `data`: WalletDetail?=null
)