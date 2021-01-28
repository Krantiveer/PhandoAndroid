package com.perseverance.phando.home.mediadetails

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadataResponse
import com.perseverance.phando.home.mediadetails.downloads.MediaUrlResponse
import com.perseverance.phando.home.mediadetails.payment.MediaplaybackData
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.ErrorModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

class MediaDetailRepository(private val application: Application) {
    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }
    fun callForVideoDetails(video: Video): MutableLiveData<DataLoadingStatus<MediaplaybackData>> {
        val data: MutableLiveData<DataLoadingStatus<MediaplaybackData>> = MutableLiveData<DataLoadingStatus<MediaplaybackData>>()
        data.postValue(DataLoadingStatus(LoadingStatus.LOADING, ""))
        val call = apiService.getMediaMatadata(video.id.toString(), video.type) as Call<MediaplaybackData>
        call.enqueue(object : Callback<MediaplaybackData> {
            override fun onResponse(call: Call<MediaplaybackData>?, response: Response<MediaplaybackData>) {
                if (response.isSuccessful) {
                    data.postValue(DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body()))
                } else {
                    val errorModel = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java)
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

    suspend fun setContinueWatchingTime(id: String, time: String) {
        try {
            apiService.setContinueWatchingTime(id, time).awaitResponse()
        } catch (e: Exception) {
        }
    }

    suspend fun saveUserDownload(param: HashMap<String, String>): DataLoadingStatus<BaseResponse> {
        return try {
            val response = apiService.saveUserDownload(param).awaitResponse()
            if (response.isSuccessful) {
                DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }
        }
    }

    suspend fun removeUserDownload(param: ArrayList<String>): DataLoadingStatus<BaseResponse> {
        return try {
            val response = apiService.removeUserDownload(param).awaitResponse()
            if (response.isSuccessful) {
                DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to remove download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to remove download")
            }
        }
    }

    suspend fun getUserDownload(): DataLoadingStatus<DownloadMetadataResponse> {
        return try {
            val response = apiService.userDownload.awaitResponse()
            if (response.isSuccessful) {
                DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to get user download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to get user download")
            }
        }
    }

    suspend fun getMediaUrl(documentId: String): DataLoadingStatus<MediaUrlResponse> {
        return try {
            val response = apiService.getMediaUrl(documentId).awaitResponse()
            if (response.isSuccessful) {
                DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }

        } catch (e: Exception) {
            if (e is ApiClient.NoConnectivityException) {
                DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }
        }
    }

    suspend fun getMediaUrlAndStartDownload(documentId: String): DataLoadingStatus<MediaUrlResponse> {
        try {
            val response = apiService.getMediaUrl(documentId).awaitResponse()
            return if (response.isSuccessful) {
                val urlResponse = DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
                val param = HashMap<String, String>()
                param["document_id"] = documentId
                val response1 = apiService.saveUserDownload(param).awaitResponse()
                if (response1.isSuccessful) {
                    urlResponse
                } else {
                    DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
                }
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }

        } catch (e: Exception) {
            return if (e is ApiClient.NoConnectivityException) {
                DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to download")
            }
        }
    }

    suspend fun updateMediaPlayStartTime(documentId: String): DataLoadingStatus<BaseResponse> {
        try {
            val param = HashMap<String, String>()
            param["document_id"] = documentId
            val response = apiService.mediaplaystarttime(param).awaitResponse()
            return if (response.isSuccessful) {
                DataLoadingStatus(LoadingStatus.SUCCESS, "", response.body())
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to update media play start time")
            }
        } catch (e: Exception) {
            return if (e is ApiClient.NoConnectivityException) {
                DataLoadingStatus(LoadingStatus.ERROR, BaseConstants.NETWORK_ERROR)
            } else {
                DataLoadingStatus(LoadingStatus.ERROR, "Unable to update media play start time")
            }
        }
    }
}