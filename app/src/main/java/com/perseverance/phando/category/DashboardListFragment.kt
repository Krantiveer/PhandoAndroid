package com.perseverance.phando.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
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
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.home.list.HomeFragmentParentListAdapter
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseVideoListActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_dashboard_list.*

class DashboardListFragment : BaseFragment(), DashboardListAdapter.AdapterClick,
    AdapterClickListener {

    override var screenName = BaseConstants.Dashboard_SCREEN

    private var mArrayList: ArrayList<BrowseData> = ArrayList<BrowseData>()

    private var adapter: HomeFragmentParentListAdapter? = null

    private val browseFragmentViewModel by lazy {
        ViewModelProvider(this).get(BrowseFragmentViewModel::class.java)
    }

    private val homeActivityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
    }

    private var dataFilters = DataFilters()
    var mListAdapter: DashboardListAdapter? = null

    private fun setAdapter() {
        if (DashboardListFragmentArgs.fromBundle(requireArguments()).view == "1") {
            adapter =
                HomeFragmentParentListAdapter(activity as Context, this, childFragmentManager)
            rvList.layoutManager = LinearLayoutManager(activity)
            adapter?.addAll(mArrayList)
            rvList.adapter = adapter
        } else {
            mListAdapter = DashboardListAdapter(requireContext(), mArrayList, this)
            rvList.layoutManager =
                GridLayoutManager(requireContext(), 2)

            rvList.adapter = mListAdapter
        }
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
                        setAdapter()
                    } else {
                        txtNoData.visible()

                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeActivityViewModel.title.value =
            DashboardListFragmentArgs.fromBundle(requireArguments()).typeName

        dataFilters.apply {
            type = DashboardListFragmentArgs.fromBundle(requireArguments()).type
            genre_id = ""
            filter = ""
            filter_type = ""
        }
//        dataFilters = Gson().fromJson(intent.getStringExtra("FILTER"), DataFilters::class.java)

        browseFragmentViewModel.getBrowseList()
            .observe(viewLifecycleOwner, browseDataViewModelObserver)
        browseFragmentViewModel.refreshData(dataFilters)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard_list, container, false)
    }

    override fun onItemClick(data: BrowseData) {
        data.id.let {
            val intent = Intent(requireActivity(), BaseVideoListActivity::class.java).apply {
                putExtra("id", data.id.toString())
                putExtra("title", data.title)
                putExtra("type", dataFilters.type)
                putExtra("filter_type", dataFilters.filter_type)
                putExtra("activity", "activity")
                putExtra("imageOrientation", data.image_orientation)
            }
            startActivity(intent)
        }

    }

    override fun onItemClick(data: Any) {
        when (data) {
            is BrowseData -> {
                data.id.let {
                    val intent = Intent(activity, BaseVideoListActivity::class.java).apply {
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
                if (Utils.isNetworkAvailable(activity)) {
                    if ("T".equals(data.type)) {
                        val intent = Intent(activity, SeriesActivity::class.java)
                        intent.putExtra(Key.CATEGORY, data)
                        startActivity(intent)
                    } else {
                        startActivity(MediaDetailActivity.getDetailIntent(activity as Context,
                            data))
                        Utils.animateActivity(activity, "next")
                    }
                } else {
                    DialogUtils.showMessage(activity,
                        BaseConstants.CONNECTION_ERROR,
                        Toast.LENGTH_SHORT,
                        false)
                }
            }

        }
    }

}