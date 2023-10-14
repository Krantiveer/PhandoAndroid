package com.perseverance.phando.home.dashboard.models

data class CategoryTab(
        val displayName: String,
        val filters: ArrayList<FilterForAdopter>,
        val type: String,
        val icon: String,
        var show: Boolean = true,
        var showFilter: Boolean = false,
        var isFilter: Boolean = false
)