package com.perseverance.phando.home.series

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
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

    fun callForSeries(tvSeriesId: String): MutableLiveData<DataLoadingStatus<TVSeriesResponseDataNew>> {

        val data: MutableLiveData<DataLoadingStatus<TVSeriesResponseDataNew>> = MutableLiveData()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        val call = apiService.getSeriesDetail(tvSeriesId)

        call.enqueue(object : Callback<TVSeriesResponseDataNew> {
            override fun onResponse(call: Call<TVSeriesResponseDataNew>, response: Response<TVSeriesResponseDataNew>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }
            }

            override fun onFailure(call: Call<TVSeriesResponseDataNew>?, t: Throwable?) {
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