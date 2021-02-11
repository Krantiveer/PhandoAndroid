package com.perseverance.phando.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface IPApiService {
    @GET(".")
    fun getRemoteApi(): Call<String>
}