package com.perseverance.phando.home.mediadetails

import android.app.Application
import androidx.lifecycle.*
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.mylist.UpdateMyListResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.mediadetails.payment.MediaplaybackData
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.NullResponseError
import com.videoplayer.DownloadInfo
import com.videoplayer.VideoSdkUtil
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Response

/**
 * Created by QAIT\TrilokiNath on 28/3/18.
 */
class MediaDetailViewModel(application: Application) : AndroidViewModel(application) {

    private var mediaDetailRepository: MediaDetailRepository = MediaDetailRepository(application)

    val reloadTrigger = MutableLiveData<Video>()
    val nextEpisodeReloadTrigger = MutableLiveData<Video>()

    var mediaMetadata: LiveData<DataLoadingStatus<MediaplaybackData>> = Transformations.switchMap(reloadTrigger) {
        mediaDetailRepository.callForVideoDetails(it)
    }
    var nextEpisodeMediaMetadata: LiveData<DataLoadingStatus<MediaplaybackData>> = Transformations.switchMap(nextEpisodeReloadTrigger) {
        mediaDetailRepository.callForVideoDetails(it)
    }
    var isInWishlist = MutableLiveData<Int>().apply {
        0
    }
    var isLiked = MutableLiveData<Int>().apply {
        0
    }
    var isDisliked = MutableLiveData<Int>().apply {
        0
    }
    var message = MutableLiveData<String>().apply {
        ""
    }
    var downloadStatus = MutableLiveData<DownloadInfo>().apply {
        null
    }

    fun refreshDownloadStatus(url: String) {
        downloadStatus.value = VideoSdkUtil.getDownloadedInfo(getApplication(), url)
    }

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }

    fun addToMyList(id: String, type: String, value: String = if (isInWishlist.value == 1) "0" else "1") {
        val call = apiService.updateMyList(id, type, value) as Call<UpdateMyListResponse>
        call.enqueue(object : retrofit2.Callback<UpdateMyListResponse> {
            override fun onResponse(call: Call<UpdateMyListResponse>?, response: Response<UpdateMyListResponse>?) {
                if (response == null || response.errorBody() != null) {
                    onFailure(call, NullResponseError())
                } else {
                    response.body()?.let {
                        when (it.status) {
                            "success" -> {
                                message.value = it.message
                                isInWishlist.value = it.status_code

                            }
                            "error" -> {
                                message.value = it.message

                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<UpdateMyListResponse>?, t: Throwable?) {
                message.value = "Unable to update wishlist"
            }

        })
    }

    fun addToLike(id: String, type: String, value: String = if (isLiked.value == 1) "0" else "1") {
        val call = apiService.likedislike(id, type, "like", value) as Call<UpdateMyListResponse>
        call.enqueue(object : retrofit2.Callback<UpdateMyListResponse> {
            override fun onResponse(call: Call<UpdateMyListResponse>?, response: Response<UpdateMyListResponse>?) {
                if (response == null || response.errorBody() != null) {
                    onFailure(call, NullResponseError())
                } else {
                    response.body()?.let {
                        when (it.status) {
                            "success" -> {
                                message.value = it.message
                                isLiked.value = it.status_code
                                isDisliked.value = if (isLiked.value == 1) 0 else isDisliked.value

                            }
                            "error" -> {
                                message.value = it.message

                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<UpdateMyListResponse>?, t: Throwable?) {
                message.value = "Unable to update wishlist"
            }

        })
    }

    fun addToDislike(id: String, type: String, value: String = if (isDisliked.value == 1) "0" else "1") {
        val call = apiService.likedislike(id, type, "dislike", value) as Call<UpdateMyListResponse>
        call.enqueue(object : retrofit2.Callback<UpdateMyListResponse> {
            override fun onResponse(call: Call<UpdateMyListResponse>?, response: Response<UpdateMyListResponse>?) {
                if (response == null || response.errorBody() != null) {
                    onFailure(call, NullResponseError())
                } else {
                    response.body()?.let {
                        when (it.status) {
                            "success" -> {
                                message.value = it.message
                                isDisliked.value = it.status_code
                                isLiked.value = if (isDisliked.value == 1) 0 else isLiked.value

                            }
                            "error" -> {
                                message.value = it.message

                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<UpdateMyListResponse>?, t: Throwable?) {
                message.value = "Unable to update wishlist"
            }

        })
    }


    fun getVideoDetailMutableLiveData(): LiveData<DataLoadingStatus<MediaplaybackData>> = mediaMetadata

    fun refreshMediaMetadata(baseVideo: Video?) {
        baseVideo?.let {
            reloadTrigger.value = it
        }

    }

    fun getNextEpisodeVideoDetailMutableLiveData(): LiveData<DataLoadingStatus<MediaplaybackData>> = nextEpisodeMediaMetadata

    fun getNextEpisodeMediaMetadata(baseVideo: Video?) {
        baseVideo?.let {
            nextEpisodeReloadTrigger.value = it
        }

    }


    fun setContinueWatchingTime(id: String, time: String) {
        val call = apiService.setContinueWatchingTime(id, time) as Call<UpdateMyListResponse>
        call.enqueue(object : retrofit2.Callback<UpdateMyListResponse> {
            override fun onResponse(call: Call<UpdateMyListResponse>?, response: Response<UpdateMyListResponse>?) {
                if (response == null || response.errorBody() != null) {

                } else {
                    response.body()?.let {

                    }

                }
            }

            override fun onFailure(call: Call<UpdateMyListResponse>?, t: Throwable?) {

            }

        })
    }

    fun saveUserDownload(param: HashMap<String, String>) = liveData(Dispatchers.IO) {
        emit(mediaDetailRepository.saveUserDownload(param))
    }

    fun removeUserDownload(param: ArrayList<String>) = liveData(Dispatchers.IO) {
        emit(mediaDetailRepository.removeUserDownload(param))
    }

    fun getUserDownload() = liveData(Dispatchers.IO) {
        emit(mediaDetailRepository.getUserDownload())
    }

    fun getMediaUrl(documentId: String) = liveData(Dispatchers.IO) {
        emit(mediaDetailRepository.getMediaUrl(documentId))
    }
    fun getMediaUrlAndStartDownload(documentId: String) = liveData(Dispatchers.IO) {
        emit(mediaDetailRepository.getMediaUrlAndStartDownload(documentId))
    }

    interface Callback {
        fun showData(data: String)
    }

    fun loadUser() {
        fetchUser { data ->
            run {
                print(data)
            }
        }
    }

    fun loadUser1() {
        fetchUser { data ->
            print(data)
        }
    }

    fun fetchUser(f: (data: String) -> Unit) {
        val data = "User Info"
        f(data)
    }

}