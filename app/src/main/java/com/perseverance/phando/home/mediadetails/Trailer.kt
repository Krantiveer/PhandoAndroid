package com.perseverance.phando.home.mediadetails

data class Trailer(
        val media_url: String,
        val id: Int,
        val phando_media_id: String,
        val reference: String,
        val status: Int,
        val thumbnail: String,
        val thumbnail_large: String,
        val thumbnail_medium: String,
        val thumbnail_small: String,
        val type: String,
        var isSelected: Boolean=false
)