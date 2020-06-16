package com.perseverance.phando.home.series

data class TVSeriesResponseData(
    val description: String,
    val detail: String,
    val genres: List<String>,
    val genres_resource: List<GenresResource>,
    val id: Int,
    val is_free: Int,
    val keyword: String,
    val maturity_rating: String,
    val phando_media_id: Any,
    val poster: String,
    val poster_vertical: String,
    val price: Int,
    val publish_year: String,
    val rating: Int,
    val seasons: List<SeriesData>,
    val thumbnail: String,
    val thumbnail_vertical: String,
    val title: String,
    val type: String
)