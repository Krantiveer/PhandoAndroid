package com.perseverance.phando.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.perseverance.phando.data.NotificationsData
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.generes.GenresResponse
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.NullResponseError
import com.perseverance.phando.utils.MyLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationListViewModel(application: Application) : AndroidViewModel(application) {


    var notificationList = MutableLiveData<ArrayList<NotificationData>?>()


    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }


    fun myNotifications() {
        val call = apiService.getNotifications() as Call<ArrayList<NotificationData>>
        call.enqueue(object : Callback<ArrayList<NotificationData>> {
            override fun onResponse(
                call: Call<ArrayList<NotificationData>>?,
                response: Response<ArrayList<NotificationData>>?,
            ) {
                if (response?.body() == null) {
                    onFailure(call, NullResponseError())
                } else {
                    val data = response.body()
                    if (data?.isNotEmpty() == true) {
                        notificationList.value= data
                    }

                }
            }

            override fun onFailure(call: Call<ArrayList<NotificationData>>?, t: Throwable?) {
                MyLog.e("@@", t!!.message.toString())
            }
        })
    }
}
