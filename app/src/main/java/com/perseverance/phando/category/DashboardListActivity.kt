package com.perseverance.phando.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.browse.BrowseFragmentViewModel
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.list.HomeFragmentParentListAdapter
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseVideoListActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.SliderAdapter
import com.perseverance.phando.utils.Utils
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_dashboard_list.*
import kotlinx.android.synthetic.main.layout_header_new.*

class DashboardListActivity : BaseScreenTrackingActivity(),
    AdapterClickListener, DashboardListAdapter.AdapterClick {

    override var screenName = BaseConstants.Dashboard_SCREEN
    private var mArrayList: ArrayList<BrowseData> = ArrayList<BrowseData>()
    private val browseFragmentViewModel by lazy {
        ViewModelProvider(this).get(BrowseFragmentViewModel::class.java)
    }
    private var dataFilters = DataFilters()
    var mListAdapter: DashboardListAdapter? = null
    private var adapter: HomeFragmentParentListAdapter? = null

    private fun setAdapter() {
        rvList.adapter = mListAdapter

        if (mArrayList.isNullOrEmpty()) {
            txtNoData.visible()
        } else {
            txtNoData.gone()
        }
        adapter = HomeFragmentParentListAdapter(this, this)
        rvList.layoutManager = LinearLayoutManager(this)
        adapter?.addAll(mArrayList)
        rvList.adapter = adapter
    }

    private val browseDataViewModelObserver = Observer<DataLoadingStatus<List<BrowseData>>> {
        when (it?.status) {
            LoadingStatus.LOADING -> {
            }
            LoadingStatus.ERROR -> {
                progressBar.gone()

            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                it.data?.let { browseDataList ->
                    if (browseDataList.isNotEmpty()) {

                        mArrayList = browseDataList as ArrayList<BrowseData>
                        if (browseDataList[0].displayType == "TOP_BANNER") {
                            setSlider(browseDataList.get(0).list)
                            mArrayList.removeAt(0)
                            mainContainer.visible()
                        } else {
                            mainContainer.gone()
                        }
                        setAdapter()
                    } else {
                        txtNoData.visible()

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_list)
        txtTitle.text = intent.getStringExtra("TITLE")
        imgBack.setOnClickListener {
            finish()
        }

        dataFilters = Gson().fromJson(intent.getStringExtra("FILTER"), DataFilters::class.java)
        Log.e("@@", dataFilters.toString())

        browseFragmentViewModel.getBrowseList()
            .observe(this, browseDataViewModelObserver)
        browseFragmentViewModel.refreshData(dataFilters)

        imgPrev.setOnClickListener {

            val currPos: Int = slider.currentPagePosition
            if (currPos != 0) {
                slider.currentPagePosition = currPos - 1

            }
        }

        imgNext.setOnClickListener {

            val currPos: Int = slider.currentPagePosition
            slider.currentPagePosition = currPos + 1
        }
    }


    private fun setSlider(sliderDataArrayList: ArrayList<Video>) {
        val adapter = SliderAdapter(this, sliderDataArrayList, this)
        slider.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        slider.setSliderAdapter(adapter)

        slider.scrollTimeInSec = 3
        slider.isAutoCycle = true
        slider.startAutoCycle()
    }


    override fun onItemClick(data: Any) {
        when (data) {
            is BrowseData -> {
                data.id.let {
                    val intent = Intent(this, BaseVideoListActivity::class.java).apply {
                        putExtra("id", data.id.toString())
                        putExtra("title", data.title)
                        putExtra("type", dataFilters.type)
                        putExtra("filter_type", dataFilters.filter_type)
                        putExtra("imageOrientation", data.image_orientation)
                    }
                    startActivity(intent)
                }
            }
            is Video -> {
                if (Utils.isNetworkAvailable(this)) {
                    if ("T".equals(data.type)) {
                        val intent = Intent(this, SeriesActivity::class.java)
                        intent.putExtra(Key.CATEGORY, data)
                        startActivity(intent)
                    } else {
                        startActivity(
                            MediaDetailActivity.getDetailIntent(
                                this as Context,
                                data
                            )
                        )
                        Utils.animateActivity(this, "next")
                    }
                } else {
                    DialogUtils.showMessage(
                        this,
                        BaseConstants.CONNECTION_ERROR,
                        Toast.LENGTH_SHORT,
                        false
                    )
                }
            }
        }
    }

    override fun onItemClick(data: BrowseData) {
        when (data) {
            is BrowseData -> {
                data.id.let {
                    val intent = Intent(this, BaseVideoListActivity::class.java).apply {
                        putExtra("id", data.id.toString())
                        putExtra("title", data.title)
                        putExtra("type", dataFilters.type)
                        putExtra("filter_type", dataFilters.filter_type)
                        putExtra("imageOrientation", data.image_orientation)
                    }
                    startActivity(intent)
                }
            }
        }

    }
}