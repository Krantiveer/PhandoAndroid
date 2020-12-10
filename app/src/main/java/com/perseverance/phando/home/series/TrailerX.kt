package com.perseverance.phando.home.series

data class TrailerX(
    val episodeId: Int,
    val file_url: String,
    val id: Int,
    val media_url: String,
    val phando_media_id: String,
    val reference: String,
    val seasonId: Int,
    val share_url: String,
    val status: Int,
    val thumbnail: String,
    val thumbnail_large: String,
    val thumbnail_medium: String,
    val thumbnail_small: String,
    val title: String,
    val type: String,
    val duration_str: String?
)