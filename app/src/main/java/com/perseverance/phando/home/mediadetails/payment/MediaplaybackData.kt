package com.perseverance.phando.home.mediadetails.payment

import com.perseverance.phando.home.mediadetails.MediaMetadata

data class MediaplaybackData(
    val data: MediaMetadata,
    val mediaCode: String,
    val note: String?,
    val message: String,
    val purchase_option: List<PurchaseOption>,
    val status: String
)