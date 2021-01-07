package com.perseverance.phando.payment.paymentoptions

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.payment.subscription.CreateOrderResponse
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletDetailRepository {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }


    suspend fun refreshWallet(): WalletDetailResponseData {
        try {
            val response = apiService.walletDetails.execute()
            if (response.isSuccessful) {
                return response.body()
            } else {
                return WalletDetailResponseData(status = "error",message = "Unable to get wallet info")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return WalletDetailResponseData(status = "error",message = BaseConstants.NETWORK_ERROR)
            } else {
                return WalletDetailResponseData(status = "error",message = "Unable to get wallet info")
            }
        }


    }

    suspend fun getWalletHistory(): WalletHistoryResponseData {
        try {
            val response = apiService.walletHistory.execute()
            if (response.isSuccessful) {
                return  response.body()
            } else {
                return WalletHistoryResponseData(status = "error",message =  "Unable to get wallet history")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return WalletHistoryResponseData("error", BaseConstants.NETWORK_ERROR)
            } else {
                return WalletHistoryResponseData("error", "Unable to get wallet history")
            }
        }
    }


    suspend fun activateWallet(param: HashMap<String, String>): BaseResponse {
        try {
            val response = apiService.activateWallet(param).execute()
            if (response.isSuccessful) {
                return response.body()
            } else {
                return BaseResponse("error", "Unable to activate wallet")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return BaseResponse("error", BaseConstants.NETWORK_ERROR)
            } else {
                return BaseResponse("error", "Unable to activate wallet")
            }
        }
    }

    suspend fun getTC(): TCResponseData? {
        try {
            val response = apiService.tc.execute()
            if (response.isSuccessful) {
                return  response.body()
            } else {
                return TCResponseData(status = "error",message =  "Unable to get wallet history")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return TCResponseData(null,"error", BaseConstants.NETWORK_ERROR)
            } else {
                return TCResponseData(null, "error", "Unable to get wallet history")
            }
        }
    }

    suspend fun createOrder(map: Map<String, String?>): CreateOrderResponse {
        try {
            val response = apiService.createOrder(map).execute()
            return if (response.isSuccessful) {
                response.body()
            } else {
                CreateOrderResponse(status = "error",message =  "Unable to create order")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return CreateOrderResponse(status = "error",message =  BaseConstants.NETWORK_ERROR)
            } else {
                return CreateOrderResponse(null, "error", "Unable to create order")
            }
        }
    }

    suspend fun updateOrderOnServer(map: Map<String, String>): BaseResponse {
        var data: MutableLiveData<DataLoadingStatus<BaseResponse>> = MutableLiveData<DataLoadingStatus<BaseResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Updating order status"))


        try {
            val response = apiService.updateOrderStatus(map).execute()
            if (response.isSuccessful) {
                return  response.body()
            } else {
                return BaseResponse(status = "error",message =  "Unable to create order")
            }
        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return BaseResponse(status = "error",message =  BaseConstants.NETWORK_ERROR)
            } else {
                return BaseResponse(status = "error", message = "Unable to update order")
            }
        }
    }

}