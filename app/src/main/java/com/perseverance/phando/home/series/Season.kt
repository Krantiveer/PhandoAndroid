package com.perseverance.phando.home.series

data class Season(
    val actors: List<String>,
    val audio_language: List<String>,
    val circular_thumbnail: String,
    val description: String,
    val detail: String,
    val directors: List<Any>,
    val episodes: List<Episode>,
    val id: Int,
    val is_active: Int,
    val is_free: Int,
    val is_live: Int,
    val maturity_rating: String,
    val phando_media_id: Any,
    val poster: String,
    val poster_vertical: String,
    val price: Int,
    val publish_year: String,
    val season_no: Int,
    val thumbnail: String,
    val thumbnail_vertical: String,
    val title: String,
    val trailer: SeriesTrailer,
    val trailers: List<TrailerX>?,
    val tv_series_id: Int,
    val type: String
){
    override fun toString(): String {
        return title
    }
}