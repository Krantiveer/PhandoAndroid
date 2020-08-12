package com.perseverance.phando.home.series

data class SeriesData(
        val actors: List<Any>,
        val audio_language: List<Any>,
        val detail: String,
        val episodes: List<Episode>,
        val trailer: SeriesTrailer?,
        val id: Int,
        val is_free: Int,
        val phando_media_id: Any,
        val poster: String,
        val poster_vertical: String,
        val season_title: String,
        val title: String,
        val price: Int,
        val publish_year: String,
        val season_no: Int,
        val thumbnail: String,
        val thumbnail_vertical: String,
        val tv_series_id: Int,
        val type: String
)