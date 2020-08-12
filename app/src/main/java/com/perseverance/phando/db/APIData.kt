package com.perseverance.phando.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class APIData(
        @PrimaryKey
        val url: String,
        val data: String
)