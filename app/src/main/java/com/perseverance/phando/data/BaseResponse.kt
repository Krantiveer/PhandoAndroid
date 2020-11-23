package com.perseverance.phando.data

data class BaseResponse(
        var message: String,
        val status: String
){
    override fun toString(): String {
        return "BaseResponse(message='$message', status='$status')"
    }
}