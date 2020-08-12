package com.perseverance.phando.search

data class SearchResult(
        val created_at: String,
        val description: String,
        val detail: String,
        val duration: String,
        val genres: List<String>,
        val genres_resource: List<GenresResource>,
        val id: Int,
        val is_free: Int,
        val keyword: String,
        val maturity_rating: String,
        val phando_media_id: String,
        val poster: String,
        val poster_vertical: String,
        val price: Int,
        val publish_year: Int,
        val rating: Int,
        val release_date: String,
        val thumbnail: String,
        val thumbnail_vertical: String,
        val title: String,
        val type: String
)