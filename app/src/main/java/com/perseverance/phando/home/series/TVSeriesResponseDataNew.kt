package com.perseverance.phando.home.series

import com.google.gson.annotations.SerializedName

data class TVSeriesResponseDataNew(
        @SerializedName("circular_thumbnail")val circularThumbnail: String,
        val description: String,
        val detail: String,
        val genres: List<String>,
        @SerializedName("genres_resource")val genresResource: List<GenresResourceX>,
        val id: Int,
        @SerializedName("is_free")val isFree: Int,
        @SerializedName("is_live")val isLive: Int,
        val keyword: String,
        @SerializedName("maturity_rating")val maturityRating: String,
        @SerializedName("phando_media_id")val phandoMediaId: Any,
        val poster: String,
        @SerializedName("poster_vertical")val posterVertical: String,
        val price: Int,
        @SerializedName("publish_year")val publishYear: String,
        val rating: Int,
        val seasons: List<Season>,
        @SerializedName("share_url")val shareUrl: String,
        val thumbnail: String,
        @SerializedName("thumbnail_vertical")val thumbnailVertical: String,
        val title: String,
        val type: String
)
