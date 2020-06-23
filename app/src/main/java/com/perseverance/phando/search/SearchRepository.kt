package com.perseverance.phando.search

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun searchData(dataFilters:DataFilters): MutableLiveData<DataLoadingStatus<List<SearchResult>>> {

        var data:MutableLiveData<DataLoadingStatus<List<SearchResult>>> = MutableLiveData<DataLoadingStatus<List<SearchResult>>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        //val call: Call<List<SearchResult>> = apiService.searchVideo(dataFilters.query, dataFilters.genre_id, dataFilters.filter, "0,100")

        val call: Call<List<SearchResult>>? =null
                call?.enqueue(object : Callback<List<SearchResult>> {
            override fun onResponse(call: Call<List<SearchResult>>, response: Response<List<SearchResult>>) {
                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }
            }

            override fun onFailure(call: Call<List<SearchResult>>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }
        })

        return data
    }

}