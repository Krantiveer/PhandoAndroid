package com.perseverance.phando.home.dashboard.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Category
import com.perseverance.phando.db.Filter
import com.perseverance.phando.db.Language
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.generes.GenresResponse
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.perseverance.phando.retrofit.NullResponseError
import com.perseverance.phando.splash.AppInfo
import com.perseverance.phando.splash.AppInfoModel
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.PreferencesUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var appInfoMutableLiveData: MutableLiveData<AppInfoModel>? = null

    var onCategoryClick = MutableLiveData<CategoryTab>()
    var onLanguageClick = MutableLiveData<Boolean>()
    var title = MutableLiveData<String>()
    var generesList = MutableLiveData<ArrayList<GenresResponse>?>()


    private var apiService: ApiService = ApiClient.getLoginClient().create(ApiService::class.java)


    fun categoryClick(value: CategoryTab) {
        onCategoryClick.value = value
    }


    fun languageClick() {
        onLanguageClick.value = true
    }

    fun getAppInfoMutableLiveData(): MutableLiveData<AppInfoModel> {


        if (appInfoMutableLiveData == null) {
            appInfoMutableLiveData = MutableLiveData<AppInfoModel>()
        }

        return appInfoMutableLiveData as MutableLiveData<AppInfoModel>
    }


    fun callForAppInfo() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
            }

            var token = ""

            if (PreferencesUtils.getLoggedStatus().isNotEmpty()) {
                token = task.result
                Log.e("@@token", token)
            }
            val call = apiService.getAppInfo("Android", token)
            call.enqueue(object : Callback<AppInfo> {

                override fun onResponse(call: Call<AppInfo>?, response: Response<AppInfo>?) {
                    if (response?.body() == null) {
                        onFailure(call, NullResponseError())
                    } else {
                        appInfoMutableLiveData!!.postValue(AppInfoModel(response.body(), null))
                    }
                }

                override fun onFailure(call: Call<AppInfo>?, t: Throwable?) {
                    appInfoMutableLiveData!!.postValue(AppInfoModel(null, t))
                }
            })
        })

    }

    fun callForGenres() {
        val call = apiService.generes as Call<List<Category>>
        call.enqueue(object : Callback<List<Category>> {
            override fun onResponse(
                call: Call<List<Category>>?,
                response: Response<List<Category>>?,
            ) {
                if (response?.body() == null) {
                    onFailure(call, NullResponseError())
                } else {
                    val data = response.body()
                    if (data?.isNotEmpty() == true) {
                        val categoryDao = AppDatabase.getInstance(getApplication())?.categoryDao()
                        categoryDao?.deleteAll()
                        categoryDao?.insertAll(data!!)
                    }

                }
            }

            override fun onFailure(call: Call<List<Category>>?, t: Throwable?) {
                MyLog.e("", "Filter not found")
            }
        })
    }
    fun callForFilters() {
        val call: Call<ArrayList<Filter>> = apiService.filters
        call.enqueue(object : Callback<ArrayList<Filter>> {
            override fun onResponse(
                call: Call<ArrayList<Filter>>?,
                response: Response<ArrayList<Filter>>?,
            ) {
                if (response?.body() == null) {
                    onFailure(call, NullResponseError())
                } else {
                    val data = response.body()
                    if (data?.isNotEmpty() == true) {
                        val filterDao = AppDatabase.getInstance(getApplication())?.filterDao()
                        filterDao?.deleteAll()
                        filterDao?.insertAll(data)
                    }

                }
            }

            override fun onFailure(call: Call<ArrayList<Filter>>?, t: Throwable?) {
                MyLog.e("", "Filter not found")
            }
        })
    }
    var mLanguagesList = MutableLiveData<ArrayList<Language>>()

    fun clickLanguage() {
        val call: Call<ArrayList<Language>> = apiService.language
        call.enqueue(object : Callback<ArrayList<Language>> {
            override fun onResponse(
                call: Call<ArrayList<Language>>?,
                response: Response<ArrayList<Language>>?,
            ) {
                if (response == null || response.body() == null) {
                    onFailure(call, NullResponseError())
                } else {

                    val data = response.body()
                    if (data?.isNotEmpty() == true) {
                        val languageDao = AppDatabase.getInstance(getApplication())?.languageDao()
                        languageDao?.deleteAll()
                        languageDao?.insertAll(data)

                        mLanguagesList.value = data
                    }

                }
            }

            override fun onFailure(call: Call<ArrayList<Language>>?, t: Throwable?) {
                MyLog.e("", "Language not found")
            }
        })
    }

    fun callLanguage() {
        val call: Call<ArrayList<Language>> = apiService.language
        call.enqueue(object : Callback<ArrayList<Language>> {
            override fun onResponse(
                call: Call<ArrayList<Language>>?,
                response: Response<ArrayList<Language>>?,
            ) {
                if (response == null || response.body() == null) {
                    onFailure(call, NullResponseError())
                } else {

                    val data = response.body()
                    if (data?.isNotEmpty() == true) {
                        val languageDao = AppDatabase.getInstance(getApplication())?.languageDao()
                        languageDao?.deleteAll()
                        languageDao?.insertAll(data)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Language>>?, t: Throwable?) {
                MyLog.e("", "Language not found")
            }
        })
    }

    fun callForGenresList() {
        val call = apiService.generesList as Call<ArrayList<GenresResponse>>
        call.enqueue(object : Callback<ArrayList<GenresResponse>> {
            override fun onResponse(
                call: Call<ArrayList<GenresResponse>>?,
                response: Response<ArrayList<GenresResponse>>?,
            ) {
                if (response?.body() == null) {
                    onFailure(call, NullResponseError())
                } else {
                    val data = response.body()
                    if (data?.isNotEmpty() == true) {

                        generesList.value= data
                    }

                }
            }

            override fun onFailure(call: Call<ArrayList<GenresResponse>>?, t: Throwable?) {
                MyLog.e("", "Filter not found")
            }
        })
    }

}