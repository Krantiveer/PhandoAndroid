package com.perseverance.phando.home.dashboard.browse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus


class BrowseFragmentViewModel(application : Application) : AndroidViewModel(application) {
    private var browseListRepository: BrowseListRepository = BrowseListRepository(application)
    private val reloadTrigger = MutableLiveData<DataFilters>()

    var data :LiveData<DataLoadingStatus<List<BrowseData>>> = Transformations.switchMap(reloadTrigger) {
        browseListRepository.browseListData(it)
    }
    fun getBrowseList() : LiveData<DataLoadingStatus<List<BrowseData>>> = data


    fun refreshData(dataFilters:DataFilters) {
        reloadTrigger.value = dataFilters
    }
    fun getCategoryTabList() : MutableLiveData<DataLoadingStatus<List<CategoryTab>>> = browseListRepository.categoryTabListData()

}
