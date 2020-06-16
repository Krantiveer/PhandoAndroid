package com.perseverance.phando.home.profile.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CountryCode(
        @PrimaryKey
    val code: String,
    val id: Int,
    val name: String
)