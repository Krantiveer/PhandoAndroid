package com.perseverance.phando.home.mediadetails.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentInfo(
        val media_id: Int,
        val payment_type: String,
        val type: String
) : Parcelable