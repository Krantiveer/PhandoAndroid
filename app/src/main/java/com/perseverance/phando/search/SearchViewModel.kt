package com.perseverance.phando.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.perseverance.phando.base.BaseViewModel
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus

/**
 * Created by QAIT\amarkhatri.
 */
class SearchViewModel(application: Application) : BaseViewModel(application) {

    private var userProfileRepository: SearchRepository = SearchRepository(application)
    private val reloadTrigger = MutableLiveData<DataFilters>()

    var data: LiveData<DataLoadingStatus<List<SearchResult>>>? = Transformations.switchMap(reloadTrigger) {
        userProfileRepository.searchData(it)
    }

    fun getResultData(): LiveData<DataLoadingStatus<List<SearchResult>>>? = data

    fun refreshSearch(dataFilters: DataFilters) {
        reloadTrigger.value = dataFilters
    }

}