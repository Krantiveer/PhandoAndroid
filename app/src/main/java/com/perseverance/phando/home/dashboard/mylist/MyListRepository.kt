package com.perseverance.phando.home.dashboard.mylist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyListRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun callForVideos(pageCountlimit: String): MutableLiveData<DataLoadingStatus<List<Video>>> {
        var data: MutableLiveData<DataLoadingStatus<List<Video>>> = MutableLiveData<DataLoadingStatus<List<Video>>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        // val call = apiService.getMyVideoList("$pageCount,$limit")
        val call = apiService.getMyVideoList(pageCountlimit)

        call.enqueue(object : Callback<List<Video>> {
            override fun onResponse(call: Call<List<Video>>, response: Response<List<Video>>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }
            }

            override fun onFailure(call: Call<List<Video>>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException) {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                } else {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }
        })

        return data
    }

}