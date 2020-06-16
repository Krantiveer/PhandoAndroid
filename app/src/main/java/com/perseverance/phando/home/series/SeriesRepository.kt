package com.perseverance.phando.home.series

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SeriesRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun callForSeries(tvSeriesId: String):MutableLiveData<DataLoadingStatus<TVSeriesResponseData>>  {

        var data:MutableLiveData<DataLoadingStatus<TVSeriesResponseData>> = MutableLiveData<DataLoadingStatus<TVSeriesResponseData>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        val call = apiService.getSeriesDetail(tvSeriesId)

        call.enqueue(object : Callback<TVSeriesResponseData> {
            override fun onResponse(call: Call<TVSeriesResponseData>, response: Response<TVSeriesResponseData>) {
                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<TVSeriesResponseData>?, t: Throwable?) {
                data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
            }
        })

        return data
    }
}