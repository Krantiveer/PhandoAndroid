package com.perseverance.phando.home.dashboard.repo

enum class LoadingStatus {
        LOADING,
        SUCCESS,
        ERROR,
        LOGOUT
    }
 class DataLoadingStatus<T>(

         val status: LoadingStatus = LoadingStatus.SUCCESS,
         val message: String? = null,
         val data: T? = null
)