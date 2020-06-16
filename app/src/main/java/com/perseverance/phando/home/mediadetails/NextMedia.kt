package com.perseverance.phando.home.mediadetails

data class NextMedia(
    val id: Int,
    val thumbnail: String,
    val type: String,
    val next_episode_start_time: Long
)