package com.perseverance.phando.home.series

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus

/**
 * Created by QAIT\TrilokiNath on 13/3/18.
 */
class SeriesViewModel(application: Application) : AndroidViewModel(application) {

    private var seriesRepository: SeriesRepository = SeriesRepository(application)

    fun callForSeries(tvSeriesId: String): MutableLiveData<DataLoadingStatus<TVSeriesResponseData>> {

        return seriesRepository.callForSeries(tvSeriesId)
    }


}
