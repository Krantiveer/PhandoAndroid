package com.perseverance.phando.home.mediadetails

import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.db.Video
import kotlinx.android.synthetic.main.content_detail.*

data class MediaMetadata(
        val actors: List<String>,
        val related: List<Video>,
        val trailers: List<Trailer>,
        val ad_url_desktop: String,
        val ad_url_mobile: String,
        val ad_url_mobile_app: String,
        val audio_language: List<String>,
        val description: String,
        val detail: String,
        val genres: List<String>,
        val id: Int,
        val is_watchlist: Int,
        val is_wishlist: Int,
        val media_id: String,
        val media_type: String,
        val media_url: String,
        val path: String,
        val poster: String,
        val poster_vertical: String,
        val rating: Int,
        val released: String,
        val maturity_rating: String,
        val tags: String,
        val thumbnail: String,
        val thumbnail_vertical: String,
        val title: String,
        val type: String,
        val is_like: Int,
        val is_dislike: Int,
        val is_live: Int?=0,
        val vast_url: String,
        val document_media_id: Int,
        val last_watch_time: Long=0,
        val next_media: NextMedia?=null,
        val intro: IntroInfo?=null,
        val series: Series?=null,
        val cc_files: List<CcFile>?= arrayListOf(),
        val analytics_category_id: String?
){
    fun getOtherText(): String {
        val otherText = StringBuilder()

        rating.let {
            otherText.append(it)
        }
        maturity_rating.let {
            otherText.append(" | $it")
        }
        genres?.let {

            otherText.append(" | " + it.joinToString())
        }
        actors?.let {

            otherText.append(" | " + it.joinToString())
        }
        return otherText.toString()
    }
}