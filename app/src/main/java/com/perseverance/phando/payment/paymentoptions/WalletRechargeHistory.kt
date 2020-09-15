package com.perseverance.phando.payment.paymentoptions

data class WalletRechargeHistory(
        val wallet_points: Int?=0,
        val datetime: String?="",
        val transaction_id: String?="",
        val payment_summary: String?="",
        val status: String?="",
        val type: String=""
)