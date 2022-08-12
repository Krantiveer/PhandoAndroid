package com.perseverance.phando.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.browse.BrowseFragmentViewModel
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_category_list.*

class CategoryListFragment : BaseFragment(), CategoryListAdapter.AdapterClick {

    override var screenName = BaseConstants.CATEGORY_SCREEN

    private var categoryTabListList: ArrayList<CategoryTab> = ArrayList<CategoryTab>()

    private val browseFragmentViewModel by lazy {
        ViewModelProvider(this).get(BrowseFragmentViewModel::class.java)
    }

    private val homeActivityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
    }

    var mListAdapter: CategoryListAdapter? = null

    private val dataFilters = DataFilters()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeActivityViewModel.title.value = "All Category"

        browseFragmentViewModel.getCategoryTabList()
            .observe(viewLifecycleOwner, categoryTabDataViewModelObserver)

    }

    private fun setAdapter() {
        mListAdapter = CategoryListAdapter(requireContext(), categoryTabListList, this)
        rvList.layoutManager =
            LinearLayoutManager(requireContext())
        rvList.adapter = mListAdapter
    }


    private val categoryTabDataViewModelObserver = Observer<DataLoadingStatus<List<CategoryTab>>> {

        when (it?.status) {
            LoadingStatus.ERROR -> {
                progressBar.gone()
                txtNoData.text = it.message
            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                it.data?.let { browseDataList ->
                    if (browseDataList.isNotEmpty()) {
                        categoryTabListList = browseDataList as ArrayList<CategoryTab>
                        setAdapter()
                    } else {
                        txtNoData.visible()
                    }
                }

            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onItemClick(data: CategoryTab) {
        dataFilters.apply {
            type = data.type
            genre_id = ""
            filter = ""
            filter_type = ""
        }
        val intent = Intent(requireContext(), DashboardListActivity::class.java)
        intent.putExtra("FILTER", Gson().toJson(dataFilters))
        intent.putExtra("TITLE", data.displayName)
        requireContext().startActivity(intent)
    }

}