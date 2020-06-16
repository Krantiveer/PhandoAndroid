package com.perseverance.phando.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService

/**
 * Created by QAIT\amarkhatri.
 */
abstract class BaseViewModel(application: Application):AndroidViewModel(application) {
    val apiService: ApiService by lazy {
        ApiClient.getClientDetail().create(ApiService::class.java)
    }
}