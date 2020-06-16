package com.perseverance.phando.home.dashboard.mylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.NullResponseError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by QAIT\TrilokiNath on 13/3/18.
 */
class MyListViewModel(application : Application) : AndroidViewModel(application) {


    private var myListRepository: MyListRepository = MyListRepository(application)
    private val reloadTrigger = MutableLiveData<String>()

    var data : LiveData<DataLoadingStatus<List<Video>>> = Transformations.switchMap(reloadTrigger) {
        myListRepository.callForVideos(it)
    }
    var message = MutableLiveData<String>().apply {
        ""
    }
    fun getMyList() : LiveData<DataLoadingStatus<List<Video>>> = data

    fun refreshMyList(pageCountlimit:String) {
        reloadTrigger.value = pageCountlimit
    }


    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }


    fun removeFromMyList( id:String,type:String){
        val call = apiService.updateMyList(id,type,"0") as Call<UpdateMyListResponse>
        call.enqueue(object : Callback<UpdateMyListResponse> {
            override fun onResponse(call: Call<UpdateMyListResponse>?, response: Response<UpdateMyListResponse>?) {
                if (response == null || response.errorBody() != null) {
                    onFailure(call, NullResponseError())
                } else {
                    response.body()?.let {
                        when(it.status)  {
                            "success" ->{
                                refreshMyList("0,100")
                                message.postValue("Removed from my list")

                            }
                            "error" ->{
                                message.postValue(it.message)

                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call<UpdateMyListResponse>?, t: Throwable?) {
t?.printStackTrace()
            }

        })
    }

}
