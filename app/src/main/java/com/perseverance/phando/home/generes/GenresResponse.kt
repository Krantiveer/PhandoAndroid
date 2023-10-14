package com.perseverance.phando.home.generes


import com.google.gson.annotations.SerializedName

data class GenresResponse(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("poster_orientation") var posterOrientation: String? = null

)