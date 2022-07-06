package com.perseverance.phando.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.browse.BrowseFragmentViewModel
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.home.videolist.BaseVideoListActivity
import kotlinx.android.synthetic.main.activity_dashboard_list.*

class DashboardListFragment : BaseFragment(), DashboardListAdapter.AdapterClick {

    override var screenName = BaseConstants.Dashboard_SCREEN

    private var mArrayList: ArrayList<BrowseData> = ArrayList<BrowseData>()


    private val browseFragmentViewModel by lazy {
        ViewModelProvider(this).get(BrowseFragmentViewModel::class.java)
    }

    private val homeActivityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
    }

    private var dataFilters = DataFilters()
    var mListAdapter: DashboardListAdapter? = null

    private fun setAdapter() {
        mListAdapter = DashboardListAdapter(requireContext(), mArrayList, this)
        rvList.layoutManager =
            GridLayoutManager(requireContext(), 2)

        rvList.adapter = mListAdapter
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

}