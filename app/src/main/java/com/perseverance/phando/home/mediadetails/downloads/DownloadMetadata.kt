package com.perseverance.phando.home.mediadetails.downloads

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class DownloadMetadata(
        @PrimaryKey
        var document_id: String,
        var title: String?,
        var description: String?,
        var thumbnail: String?,
        var media_url: String?=null,
        var status:Int?=0
):Serializable