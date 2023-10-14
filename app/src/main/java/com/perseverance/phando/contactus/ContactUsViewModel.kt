package com.perseverance.phando.contactus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.NullResponseError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsViewModel(application: Application) : AndroidViewModel(application) {


    private val reloadTrigger = MutableLiveData<String>()

    var responseString = MutableLiveData<SuccessResponse>()

    var message = MutableLiveData<String>().apply {
        ""
    }



    fun refreshMyList(pageCountlimit: String) {
        reloadTrigger.value = pageCountlimit
    }


    private val apiService by lazy { ApiClient.getLoginClient().create(ApiService::class.java) }


     fun contactUs(first_name: String, email_id: String, mobile_number: String, comment: String) {
         val call = apiService.contactUs(first_name, email_id, mobile_number,comment ) as Call<SuccessResponse>
         call.enqueue(object : Callback<SuccessResponse> {
             override fun onResponse(call: Call<SuccessResponse>?, response: Response<SuccessResponse>?) {
                 if (response == null || response.errorBody() != null) {
                     onFailure(call, NullResponseError())
                     response!!.body()?.let {
                         responseString.value = it
                     }
                 } else {
                     response.body()?.let {
                         responseString.value = it
                     }

                 }
             }

             override fun onFailure(call: Call<SuccessResponse>?, t: Throwable?) {
                 t?.printStackTrace()
             }

         })
     }

}
