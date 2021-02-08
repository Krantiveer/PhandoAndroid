package com.perseverance.phando.home.mediadetails.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PurchaseOption(
        val key: String,
        val currency: String,
        val currency_symbol:String,
        val final_points : Int,
        val discount_percentage: Int,
        val note: String,
        var mediaTitle: String?,
        val payment_info: PaymentInfo,
        val final_price : Float,
        val value: Float,
        val isWallet : Boolean=false
) : Parcelable

