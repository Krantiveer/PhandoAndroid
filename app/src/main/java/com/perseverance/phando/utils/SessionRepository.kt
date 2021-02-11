package com.perseverance.phando

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.perseverance.phando.retrofit.IPApiService
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

suspend fun getPublicIpAddress(): String {
    val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
    val retrofit = Retrofit.Builder()
            .baseUrl("https://api.ipify.org/?format=json")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(IPApiService::class.java)
    return retrofit.getRemoteApi().await()
}