package com.perseverance.phando.home.mediadetails.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PurchaseOption(
        val currency: String,
        val discount_percentage: Int,
        val key: String,
        val note: String,
        var mediaTitle: String?,
        val payment_info: PaymentInfo,
        val final_price : Int,
        val value: Float,
        val isWallet : Boolean=false
) : Parcelable