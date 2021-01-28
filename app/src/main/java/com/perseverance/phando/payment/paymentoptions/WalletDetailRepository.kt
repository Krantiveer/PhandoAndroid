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
import retrofit2.awaitResponse

class WalletDetailRepository {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }


    suspend fun refreshWallet(): WalletDetailResponseData {
        return try {
            val response = apiService.walletDetails.awaitResponse()
            if (response.isSuccessful&&response.body()!=null) {
                response.body()!!
            } else {
                WalletDetailResponseData(status = "error",message = "Unable to get wallet info")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                WalletDetailResponseData(status = "error",message = BaseConstants.NETWORK_ERROR)
            } else {
                WalletDetailResponseData(status = "error",message = "Unable to get wallet info")
            }
        }


    }

    suspend fun getWalletHistory(): WalletHistoryResponseData {
        return try {
            val response = apiService.walletHistory.awaitResponse()
            if (response.isSuccessful&&response.body()!=null) {
                response.body()!!
            } else {
                WalletHistoryResponseData(status = "error",message =  "Unable to get wallet history")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                WalletHistoryResponseData("error", BaseConstants.NETWORK_ERROR)
            } else {
                WalletHistoryResponseData("error", "Unable to get wallet history")
            }
        }
    }


    suspend fun activateWallet(param: HashMap<String, String>): BaseResponse {
        return try {
            val response = apiService.activateWallet(param).awaitResponse()
            if (response.isSuccessful&&response.body()!=null) {
                response.body()!!
            } else {
                BaseResponse("error", "Unable to activate wallet")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                BaseResponse("error", BaseConstants.NETWORK_ERROR)
            } else {
                BaseResponse("error", "Unable to activate wallet")
            }
        }
    }

    suspend fun getTC(): TCResponseData? {
        try {
            val response = apiService.tc.awaitResponse()
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
        return try {
            val response = apiService.createOrder(map).awaitResponse()
            if (response.isSuccessful&&response.body()!=null) {
                response.body()!!
            } else {
                CreateOrderResponse(status = "error",message =  "Unable to create order")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                CreateOrderResponse(status = "error",message =  BaseConstants.NETWORK_ERROR)
            } else {
                CreateOrderResponse(null, "error", "Unable to create order")
            }
        }
    }

    suspend fun updateOrderOnServer(map: Map<String, String>): BaseResponse {
        val data: MutableLiveData<DataLoadingStatus<BaseResponse>> = MutableLiveData<DataLoadingStatus<BaseResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Updating order status"))

        return try {
            val response = apiService.updateOrderStatus(map).awaitResponse()
            if (response.isSuccessful&&response.body()!=null) {
                response.body()!!
            } else {
                BaseResponse(status = "error",message =  "Unable to create order")
            }
        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                BaseResponse(status = "error",message =  BaseConstants.NETWORK_ERROR)
            } else {
                BaseResponse(status = "error", message = "Unable to update order")
            }
        }
    }

}