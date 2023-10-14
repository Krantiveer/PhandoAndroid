package com.perseverance.phando.contactus

import com.google.gson.annotations.SerializedName

data class SuccessResponse (

    @SerializedName("message" ) var message : String? = null,
    @SerializedName("status"  ) var status  : String? = null

)