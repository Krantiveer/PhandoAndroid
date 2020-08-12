package com.perseverance.phando.home.dashboard.browse

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.APIData
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BrowseListRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun browseListData(dataFilters: DataFilters): MutableLiveData<DataLoadingStatus<List<BrowseData>>> {
        var data: MutableLiveData<DataLoadingStatus<List<BrowseData>>> = MutableLiveData<DataLoadingStatus<List<BrowseData>>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))

        val call = apiService.getBrowseDataList(dataFilters.type, dataFilters.genre_id, dataFilters.filter, dataFilters.limit, dataFilters.offset)
        val url = call.request().url().toString()
        val apiDataDao = AppDatabase.getInstance(application)?.apiDataDao()

        val apiData = apiDataDao?.getDataForUrl(url)
        apiData?.data?.let {
            try {
                val type = object : TypeToken<List<BrowseData>>() {}.type
                val cachedData: List<BrowseData> = Gson().fromJson(it, type)
                data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", cachedData))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        call.enqueue(object : Callback<List<BrowseData>> {
            override fun onResponse(call: Call<List<BrowseData>>, response: Response<List<BrowseData>>) {
                if (response.isSuccessful) {
                    val tempData = response.body()
                    apiDataDao?.insert(APIData(url, Gson().toJson(tempData)))
                    // retriving data
                    val apiData = apiDataDao?.getDataForUrl(url)
                    apiData?.data?.let {
                        try {
                            val cachedData: List<BrowseData> = Gson().fromJson(it, object : TypeToken<List<BrowseData>>() {}.type)
                            data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", cachedData))
                        } catch (e: Exception) {
                            data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                        }
                    }
                            ?: data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }
            }

            override fun onFailure(call: Call<List<BrowseData>>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException) {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                } else {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }
        })

        return data
    }

    fun categoryTabListData(): MutableLiveData<DataLoadingStatus<List<CategoryTab>>> {
        var data: MutableLiveData<DataLoadingStatus<List<CategoryTab>>> = MutableLiveData<DataLoadingStatus<List<CategoryTab>>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        val call = apiService.getCategoryTabList()

        call.enqueue(object : Callback<List<CategoryTab>> {
            override fun onResponse(call: Call<List<CategoryTab>>, response: Response<List<CategoryTab>>) {
                if (response == null || response.body() == null) {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                } else {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))

                }
            }

            override fun onFailure(call: Call<List<CategoryTab>>?, t: Throwable?) {
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