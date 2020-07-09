package com.perseverance.phando.home.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.gson.Gson
import com.perseverance.phando.Constants
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.profile.login.SocialLoggedInUser
import com.perseverance.phando.retrofit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserProfileRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun fetchProfileData(): MutableLiveData<DataLoadingStatus<UserProfileData>> {
        var data:MutableLiveData<DataLoadingStatus<UserProfileData>> = MutableLiveData<DataLoadingStatus<UserProfileData>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))
        val call = apiService.userProfile
        call.enqueue(object : Callback<UserProfileData> {
            override fun onResponse(call: Call<UserProfileData>, response: Response<UserProfileData>) {
             if (response.isSuccessful){
                 data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
             }else{
                 val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                 data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
             }

            }

            override fun onFailure(call: Call<UserProfileData>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                   // data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, null))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load profile data"))
                }

            }
        })

        return data
    }
    fun dologin(cred:Cred): MutableLiveData<DataLoadingStatus<LoginResponse>>  {

        var data:MutableLiveData<DataLoadingStatus<LoginResponse>> = MutableLiveData<DataLoadingStatus<LoginResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))

        val call = apiService.doLogin(cred)
        call.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>) {

                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to login"))
                }
            }
        })
        return data
    }
    fun doSociallogin(socialLoggedInUser: SocialLoggedInUser): MutableLiveData<DataLoadingStatus<LoginResponse>>  {

        var data:MutableLiveData<DataLoadingStatus<LoginResponse>> = MutableLiveData<DataLoadingStatus<LoginResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))

        val call = apiService.doSocialLogin(socialLoggedInUser)
        call.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>) {

                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to login"))
                }
            }
        })
        return data
    }


    fun doRegister(map:Map<String,String>): MutableLiveData<DataLoadingStatus<LoginResponse>>  {

        var data:MutableLiveData<DataLoadingStatus<LoginResponse>> = MutableLiveData<DataLoadingStatus<LoginResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))

        val call = apiService.doRegister(map)
        call.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>) {

                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to register user"))
                }
            }
        })
        return data
    }

    fun getOTP(map:Map<String,String>): MutableLiveData<DataLoadingStatus<BaseResponse>>  {

        var data:MutableLiveData<DataLoadingStatus<BaseResponse>> = MutableLiveData<DataLoadingStatus<BaseResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))

        val call = apiService.getOTP(map)
        call.enqueue(object : Callback<BaseResponse> {

            override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>) {

                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to get otp"))
                }
            }
        })
        return data
    }

    fun verifyOTP(map:Map<String,String>): MutableLiveData<DataLoadingStatus<LoginResponse>>  {

        var data:MutableLiveData<DataLoadingStatus<LoginResponse>> = MutableLiveData<DataLoadingStatus<LoginResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Loading data"))

        val call = apiService.verifyOTP(map)
        call.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>) {

                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to verify otp"))
                }
            }
        })
        return data
    }

    fun verifyOTPForForgotPassword(map :Map<String,String>): MutableLiveData<DataLoadingStatus<BaseResponse>> {
        var data:MutableLiveData<DataLoadingStatus<BaseResponse>> = MutableLiveData<DataLoadingStatus<BaseResponse>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, "Updating order status"))
        val call = apiService.verifyPasswordOTP(map)

        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful){
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                }else{
                    val errorModel  = Gson().fromJson(response.errorBody().string(),ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }

            }

            override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException){
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                }else{
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }
        })

        return data
    }
    suspend fun removeUserDownload(param: ArrayList<String>): DataLoadingStatus<BaseResponse> {
        try {
            val response = apiService.removeUserDownload(param).execute()
            if (response.isSuccessful) {
                return DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to remove download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to remove download")
            }
        }
    }
}