package com.perseverance.phando.payment.subscription

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SubscriptionRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun fetchPackage(): MutableLiveData<DataLoadingStatus<Package>> {
        var data: MutableLiveData<DataLoadingStatus<Package>> = MutableLiveData<DataLoadingStatus<Package>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        val call = apiService.packageDetails

        call.enqueue(object : Callback<Package> {
            override fun onResponse(call: Call<Package>, response: Response<Package>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<Package>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException) {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                } else {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }
        })

        return data
    }

    fun createOrder(map: Map<String, String>): MutableLiveData<DataLoadingStatus<CreateOrderResponse>> {
        var data: MutableLiveData<DataLoadingStatus<CreateOrderResponse>> = MutableLiveData<DataLoadingStatus<CreateOrderResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Creating order"))

        val call = apiService.createOrder(map)
        call.enqueue(object : Callback<CreateOrderResponse> {
            override fun onResponse(call: Call<CreateOrderResponse>, response: Response<CreateOrderResponse>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<CreateOrderResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException) {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                } else {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }
        })

        return data
    }

    fun updateOrderOnServer(map: Map<String, String>): MutableLiveData<DataLoadingStatus<BaseResponse>> {
        var data: MutableLiveData<DataLoadingStatus<BaseResponse>> = MutableLiveData<DataLoadingStatus<BaseResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Updating order status"))
        val call = apiService.updateOrderStatus(map)

        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
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