package com.perseverance.phando.home.series

data class EpisodeX(
    val audio_language: List<String>,
    val circular_thumbnail: String,
    val detail: String,
    val duration: Int,
    val episode_no: Int,
    val id: Int,
    val is_active: Int,
    val is_free: Int,
    val is_live: Int,
    val last_watch_time: Int,
    val phando_media_id: String,
    val poster: String,
    val poster_vertical: String,
    val price: Int,
    val publish_year: Any,
    val season_no: Int,
    val thumbnail: String,
    val thumbnail_vertical: String,
    val title: String,
    val tv_series_id: Int,
    val type: String
)