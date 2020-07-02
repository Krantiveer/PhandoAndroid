package com.perseverance.phando.home.dashboard.browse


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.gson.Gson
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Category
import com.perseverance.phando.db.Filter
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.BaseNetworkErrorFragment
import com.perseverance.phando.home.dashboard.filter.FilterAdapter
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.list.HomeFragmentParentListAdapter
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.profile.ProfileActivity
import com.perseverance.phando.home.profile.UserProfileData
import com.perseverance.phando.home.profile.UserProfileViewModel
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseVideoListActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.PreferencesUtils
import com.perseverance.phando.utils.Util
import com.perseverance.phando.utils.Utils
import com.qait.sadhna.LoginActivity
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_browse_new.*
import kotlinx.android.synthetic.main.fragment_browse_new.progressBar


abstract class  BaseBrowseFragmentNew : BaseNetworkErrorFragment(), AdapterClickListener {

    private val browseFragmentViewModel by lazy {
        ViewModelProviders.of(this).get(BrowseFragmentViewModel::class.java)
    }
    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    private var adapter: HomeFragmentParentListAdapter? = null
    private var browseFragmentCategoryTabListAdapter: BrowseFragmentCategoryTabListAdapter? = null
    private var categoryTabListList:ArrayList<CategoryTab> = ArrayList<CategoryTab>()
    val dataFilters = DataFilters()
    var categoryTab :CategoryTab?=null
    var nestedScrollView : NestedScrollView?=null

    val browseDataViewModelObserver = Observer<DataLoadingStatus<List<BrowseData>>> {

        when (it?.status) {
            LoadingStatus.LOADING -> {
                progressBar.visible()
            }
            LoadingStatus.ERROR -> {
                progressBar.gone()
            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                adapter = HomeFragmentParentListAdapter(activity as Context, this, childFragmentManager)
                recyclerViewUpcomingVideos.adapter = adapter
                it.data?.let { browseDataList->
                    if (browseDataList.isNotEmpty()){
                        val headerBrowseData = browseDataList.get(0)
                        headerBrowseData?.let { browseData ->
                            setBannerSlider(browseDataList,browseData)
                            /*if (browseData.displayType == "TOP_BANNER") {
                                homeHeaderView?.setData(browseData.list)
                                (browseDataList as ArrayList).removeAt(0)
                                homeHeaderView.visible()
                            }else{
                                homeHeaderView.gone()
                            }*/
                        }
                        nestedScrollView?.visible()
                    }else{
                        nestedScrollView?.gone()

                        Toast.makeText(activity,"No data to display!",Toast.LENGTH_LONG).show()
                    }

                    adapter?.addAll(browseDataList)
                    filterRecyclerView.scrollToPosition(0)

                }


            }

        }

    }

    abstract fun setBannerSlider(browseData1: List<BrowseData>, browseData: BrowseData)

    val categoryTabDataViewModelObserver = Observer<DataLoadingStatus<List<CategoryTab>>> {

        when (it?.status) {

            LoadingStatus.SUCCESS -> {


                it.data?.let { browseDataList->
                    if (browseDataList.isNotEmpty()){
                        categoryTabListList = browseDataList as ArrayList<CategoryTab>
                        categoryTabListList.map {
                            it.show=true
                            it.showFilter=false

                        }
                        browseFragmentCategoryTabListAdapter = BrowseFragmentCategoryTabListAdapter(activity as Context,this)
                        filterRecyclerView.adapter =  browseFragmentCategoryTabListAdapter
                        browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
                        //filterRecyclerView.itemAnimator = SlideInLeftAnimator()

                    }else{

                    }


                }

            }

        }

    }

    val profileObserver = Observer<DataLoadingStatus<UserProfileData>> {

        when (it?.status) {
            LoadingStatus.ERROR -> {
                it.message?.let {
                   // Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
                }
            }

            LoadingStatus.SUCCESS -> {
                PreferencesUtils.saveObject("profile",it.data)
                Utils.displayCircularProfileImage(activity, it.data?.user?.image,
                        R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, imgHeaderProfile)

            }

        }

    }

    private var sheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_browse_new, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        recyclerViewUpcomingVideos.layoutManager = LinearLayoutManager(activity)
        val filterRecyclerViewLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        filterRecyclerView.layoutManager = filterRecyclerViewLayoutManager
        browseFragmentViewModel.getCategoryTabList().observe(viewLifecycleOwner, categoryTabDataViewModelObserver)
        browseFragmentViewModel.getBrowseList().observe(viewLifecycleOwner, browseDataViewModelObserver)
        browseFragmentViewModel.refreshData(dataFilters)
        imgHeaderImage.setOnClickListener {

            dataFilters.apply {
                type = ""
                genre_id = ""
                filter = ""

            }
            browseFragmentViewModel.refreshData(dataFilters)
            setFilterGravity(Gravity.CENTER)
            categoryTabListList.map {

                    it.show=true
                    it.showFilter=false

            }
            browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
            categoryTab=null
        }

        closeButton.setOnClickListener(View.OnClickListener {
            if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        })
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        // callback for do something
        sheetBehavior?.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {


                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                            filters.adapter = null
                            filters.layoutManager = LinearLayoutManager(activity)
                            val allFilterAdapter = FilterAdapter(activity as Context, this@BaseBrowseFragmentNew)
                            filters.adapter = allFilterAdapter
                            val filterList = categoryTab?.filters as ArrayList
                            allFilterAdapter.addAll(filterList)

                    }
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        imgHeaderProfile.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                val intent = Intent(context, LoginActivity::class.java)
                startActivityForResult(intent,LoginActivity.REQUEST_CODE_LOGIN)
            } else {

                val intent = Intent(context, ProfileActivity::class.java)
                startActivity(intent)
            }

        }
        Util.hideKeyBoard(requireActivity())
        val strProfile = PreferencesUtils.getStringPreferences("profile")
        val userProfileData = Gson().fromJson(strProfile,UserProfileData::class.java)
        userProfileData?.let {
            Utils.displayCircularProfileImage(context, it.user?.image,
                    R.drawable.ic_user_avatar,R.drawable.ic_user_avatar,imgHeaderProfile)
        }
        observeUserProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==LoginActivity.REQUEST_CODE_LOGIN && resultCode== Activity.RESULT_OK){
//            val intent = Intent(context, ProfileActivity::class.java)
//            startActivity(intent)

        }
    }

    abstract fun setTopBannserHeight()

    private fun setFilterGravity(viewGravity: Int) {
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
            gravity = viewGravity
        }
        filterContainer.gravity = viewGravity
    }


    override fun onItemClick(data: Any) {
        when (data) {
            is Video -> {
                if (Utils.isNetworkAvailable(activity)) {
                    if ("T".equals(data.mediaType)) {
                        val intent = Intent(activity, SeriesActivity::class.java)
                        intent.putExtra(Key.CATEGORY, data)
                        startActivity(intent)
                    } else {
                        startActivity(MediaDetailActivity.getDetailIntent(activity as Context, data))
                        Utils.animateActivity(activity, "next")
                    }
                } else {
                    DialogUtils.showMessage(activity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                }
            }
            is BrowseData -> {
                data.id.let {
                    val intent = Intent(activity, BaseVideoListActivity::class.java).apply {
                        putExtra("id", data.id.toString())
                        putExtra("title", data.title)
                        putExtra("type", dataFilters.type)
                    }
                    startActivity(intent)
                }

            }
            is Filter -> {
                dataFilters.apply {

                    filter = data.key

                }
                //globleFilter.text = data.title
                browseFragmentViewModel.refreshData(dataFilters)
                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
            is Category -> {

                dataFilters.apply {
                    genre_id = data.id


                }
               // genresFilter.text = data.name
                browseFragmentViewModel.refreshData(dataFilters)
                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }

            is CategoryTab->{

                if (data.isFilter){
                    if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                }else{
                    categoryTab?.let {
                        if (it.displayName==data.displayName){
                            return
                        }
                    }
                    categoryTab = data
                    categoryTabListList.map {
                        if (it == data){
                            it.show=true
                            it.showFilter=true
                            if (it.filters.isNotEmpty()){
                                it.filters.map {
                                    it.isSelected=false
                                }
                                it.filters?.get(0)?.isSelected = true
                            }
                        }else{
                            it.show=false
                            it.showFilter=false
                        }

                    }
                    browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
                    dataFilters.apply {
                        type = categoryTab!!.type
                        genre_id=""
                        filter=""
                        
                    }
                    browseFragmentViewModel.refreshData(dataFilters)
                    setFilterGravity(Gravity.LEFT)
                }
            }
            is com.perseverance.phando.home.dashboard.models.Filter ->{
                dataFilters.apply {
                    genre_id = if (type!="GENRES") data.id else ""
                    filter = if (type=="GENRES") data.id else ""

                }
                categoryTabListList.map {
                    it.filters.map {
                        if (it.id==data.id){
                            it.isSelected=true
                        }else{
                            it.isSelected=false
                        }
                    }

                }
                browseFragmentCategoryTabListAdapter?.notifyDataSetChanged()
                browseFragmentViewModel.refreshData(dataFilters)


                if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                } else {
                    sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }

            }
        }

    }

    private fun observeUserProfile() {
        userProfileViewModel.getUserProfile().observe(viewLifecycleOwner, profileObserver)
        userProfileViewModel.refreshUserProfile()
    }

    override fun onResume() {
        super.onResume()
        userProfileViewModel.refreshUserProfile()
    }
}
