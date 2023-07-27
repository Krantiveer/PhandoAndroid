package com.perseverance.phando.home.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.perseverance.phando.db.Video
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.NullResponseError
import com.perseverance.phando.videoplayer.VideosModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by QAIT\TrilokiNath on 13/3/18.
 */
class MediaListViewModel(application: Application) : AndroidViewModel(application) {

    val videoListMutableLiveData by lazy { MutableLiveData<VideosModel>() }

    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }
    private val apiServiceLogin by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }


    fun callForVideos(id: String, pageCount: Int, limit: Int, type: String = "") {

        val call: Call<List<Video>> = apiServiceLogin.getVideosByCategory(id, "$pageCount,$limit","android", type)
        call.enqueue(object : Callback<List<Video>> {

            override fun onResponse(call: Call<List<Video>>?, response: Response<List<Video>>?) {
                if (response == null || response.body() == null) {
                    onFailure(call, NullResponseError())
                } else {
                    videoListMutableLiveData.postValue(VideosModel(response.body(), null, pageCount, null))
                }
            }

            override fun onFailure(call: Call<List<Video>>?, t: Throwable?) {

                videoListMutableLiveData.postValue(VideosModel(arrayListOf(), null, pageCount, t))

            }
        })
    }

    fun callForEpisodes(id: String, pageCount: Int, limit: Int) {

        val call: Call<List<Video>> = apiServiceLogin.getEpisodesBySeason(id)
        call.enqueue(object : Callback<List<Video>> {

            override fun onResponse(call: Call<List<Video>>?, response: Response<List<Video>>?) {
                if (response == null || response.body() == null) {
                    onFailure(call, NullResponseError())
                } else {
                    videoListMutableLiveData.postValue(VideosModel(response.body(), null, pageCount, null))
                }
            }

            override fun onFailure(call: Call<List<Video>>?, t: Throwable?) {
                videoListMutableLiveData.postValue(VideosModel(arrayListOf(), null, pageCount, null))
            }
        })
    }


}
