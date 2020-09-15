package com.perseverance.phando.payment.paymentoptions

data class WalletHistoryResponseData(
        val status: String="",
        val message: String="",
        val data: List<WalletRechargeHistory> = arrayListOf()
)