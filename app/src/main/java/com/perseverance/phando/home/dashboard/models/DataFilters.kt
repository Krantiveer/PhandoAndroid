package com.perseverance.phando.home.dashboard.models

data class DataFilters(
        var type: String = "", var genre_id: String = "", var limit: Int = 10, var offset: Int = 0, var filter: String = "", var query: String=""
)