package com.perseverance.phando.home.dashboard.models

data class Filter(
        val id: String,
        val name: String,
        var isSelected :Boolean =false
)