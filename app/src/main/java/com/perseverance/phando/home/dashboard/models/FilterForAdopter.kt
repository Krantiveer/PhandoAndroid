package com.perseverance.phando.home.dashboard.models

data class FilterForAdopter(
        val id: String,
        val name: String,
        val filter_type: String,
        var isSelected: Boolean = false
)