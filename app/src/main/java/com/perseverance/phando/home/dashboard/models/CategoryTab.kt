package com.perseverance.phando.home.dashboard.models

data class CategoryTab(
        val displayName: String,
        val filters: List<Filter>,
        val type: String,
        var show:Boolean=true,
        var showFilter: Boolean=false,
        var isFilter :Boolean =  false
)