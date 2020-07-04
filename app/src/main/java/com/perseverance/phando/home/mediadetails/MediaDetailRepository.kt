package com.perseverance.phando.home.mediadetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.db.BaseVideo
import com.perseverance.phando.home.dashboard.mylist.UpdateMyListResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadataResponse
import com.perseverance.phando.home.mediadetails.downloads.MediaUrlResponse
import com.perseverance.phando.home.mediadetails.payment.MediaplaybackData
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MediaDetailRepository(private val application: Application) {

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun callForVideoDetails(video: BaseVideo): MutableLiveData<DataLoadingStatus<MediaplaybackData>> {
        var data: MutableLiveData<DataLoadingStatus<MediaplaybackData>> = MutableLiveData<DataLoadingStatus<MediaplaybackData>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, ""))
        val call = apiService.getMediaMatadata(video.entryId, video.mediaType) as Call<MediaplaybackData>
        call.enqueue(object : Callback<MediaplaybackData> {
            override fun onResponse(call: Call<MediaplaybackData>?, response: Response<MediaplaybackData>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody().string(), ErrorModel::class.java)
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, errorModel.message))
                }
            }

            override fun onFailure(call: Call<MediaplaybackData>?, t: Throwable?) {
                if (t is ApiClient.NoConnectivityException) {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR))
                } else {
                    data.postValue(DataLoadingStatus(LoadingStatus.ERROR, "Unable to load data"))
                }
            }

        })
        return data
    }

    suspend fun saveUserDownload(param: HashMap<String, String>): DataLoadingStatus<BaseResponse> {
        try {
            val response = apiService.saveUserDownload(param).execute()
            if (response.isSuccessful) {
                return DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
               return DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }
        }
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
    suspend fun getUserDownload(): DataLoadingStatus<DownloadMetadataResponse> {
        try {
            val response = apiService.userDownload.execute()
            if (response.isSuccessful) {
                return DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to get user download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to get user download")
            }
        }
    }
    suspend fun getMediaUrl(documentId:String): DataLoadingStatus<MediaUrlResponse> {

        try {
            val response = apiService.getMediaUrl(documentId).execute()
            if (response.isSuccessful) {
                return DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                return DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                return DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }
        }
    }
}