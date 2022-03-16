package com.perseverance.phando.home.dashboard.browse


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.perseverance.patrikanews.utils.*
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Category
import com.perseverance.phando.db.Filter
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.filter.FilterAdapter
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.models.FilterForAdopter
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.list.HomeFragmentParentListAdapter
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
import com.perseverance.phando.home.profile.ProfileActivity
import com.perseverance.phando.home.profile.UserProfileData
import com.perseverance.phando.home.profile.UserProfileViewModel
import com.perseverance.phando.home.profile.login.LoginActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseVideoListActivity
import com.perseverance.phando.notification.NotificationDao
import com.perseverance.phando.notification.NotificationListActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.PreferencesUtils
import com.perseverance.phando.utils.Util
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_browse_new.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseBrowseFragmentNew : BaseFragment(), AdapterClickListener {

    private val browseFragmentViewModel by lazy {
        ViewModelProvider(this).get(BrowseFragmentViewModel::class.java)
    }

    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    private var notificationDao: NotificationDao? = null
    private var adapter: HomeFragmentParentListAdapter? = null
    private var browseFragmentCategoryTabListAdapter: BrowseFragmentCategoryTabListAdapter? = null
    private var categoryTabListList: ArrayList<CategoryTab> = ArrayList<CategoryTab>()
    private val dataFilters = DataFilters()
    var categoryTab: CategoryTab? = null
    var nestedScrollView: NestedScrollView? = null

    private val browseDataViewModelObserver = Observer<DataLoadingStatus<List<BrowseData>>> {

        when (it?.status) {
            LoadingStatus.LOADING -> {
                progressBar.visible()
            }
            LoadingStatus.ERROR -> {
                progressBar.gone()
            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                adapter =
                    HomeFragmentParentListAdapter(activity as Context, this, childFragmentManager)
                recyclerViewUpcomingVideos.adapter = adapter
                it.data?.let { browseDataList ->
                    if (browseDataList.isNotEmpty()) {
                        val headerBrowseData = browseDataList.get(0)
                        headerBrowseData?.let { browseData ->
                            setBannerSlider(browseDataList, browseData)
                            /*if (browseData.displayType == "TOP_BANNER") {
                                homeHeaderView?.setData(browseData.list)
                                (browseDataList as ArrayList).removeAt(0)
                                homeHeaderView.visible()
                            }else{
                                homeHeaderView.gone()
                            }*/
                        }
                        nestedScrollView?.visible()
                    } else {
                        nestedScrollView?.gone()

                        Toast.makeText(activity, "No data to display!", Toast.LENGTH_LONG).show()
                    }
                    adapter?.addAll(browseDataList)
                    filterRecyclerView.scrollToPosition(0)
                }
            }
        }
    }

    abstract fun setBannerSlider(browseData1: List<BrowseData>, browseData: BrowseData)

    private val categoryTabDataViewModelObserver = Observer<DataLoadingStatus<List<CategoryTab>>> {

        when (it?.status) {

            LoadingStatus.SUCCESS -> {
                it.data?.let { browseDataList ->
                    if (browseDataList.isNotEmpty()) {
                        categoryTabListList = browseDataList as ArrayList<CategoryTab>
                        categoryTabListList.map {
                            it.show = true
                            it.showFilter = false

                        }

//                        browseFragmentCategoryTabListAdapter =
//                            BrowseFragmentCategoryTabListAdapter(activity as Context, this)
//                        filterRecyclerView.adapter = browseFragmentCategoryTabListAdapter
//                        browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
//                        //filterRecyclerView.itemAnimator = SlideInLeftAnimator()

                    } else {

                    }

                }

            }

        }

    }

    private val profileObserver = Observer<DataLoadingStatus<UserProfileData>> {

        when (it?.status) {
            LoadingStatus.ERROR -> {
                it.message?.let {
                    // Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
                }
            }
            LoadingStatus.SUCCESS -> {
                Utils.displayCircularProfileImage(activity, it.data?.user?.image,
                    R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, imgHeaderProfile)
                lifecycleScope.launch(Dispatchers.IO) {
                    syncDownload(it.data?.user_downloads)
                }
//               if (it.data?.preferred_language?.isEmpty()!!){
//                   openLanguagePreferenceDialog()
//               }

            }

        }

    }

    private suspend fun syncDownload(userDownloads: List<DownloadMetadata>?) {
        val downloadMetadataDao =
            activity?.let { AppDatabase.getInstance(it)?.downloadMetadataDao() }
        val allData = downloadMetadataDao?.getAllData()
        if (allData != null) {
            if (allData.isEmpty()) {
                userDownloads?.let { downloadMetadataDao.insertAll(it) }
            } else {
                val allDeleted = downloadMetadataDao?.getDeletedDownload()
                if (allDeleted == null || allDeleted.isEmpty()) {
                    return
                }
                val idList = ArrayList<String>()
                allDeleted.map {
                    idList.add(it.document_id)
                }
                userProfileViewModel.removeUserDownload(idList)
            }
        }

    }

    private var sheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_browse_new, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationDao = AppDatabase.getInstance(requireActivity()).notificationDao()
        nestedScrollView = view.findViewById(R.id.nestedScrollView)
        recyclerViewUpcomingVideos.layoutManager = LinearLayoutManager(activity)
        val filterRecyclerViewLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        filterRecyclerView.layoutManager =  StaggeredGridLayoutManager(3,
//            StaggeredGridLayoutManager.VERTICAL)

//        val chipsLayoutManager = ChipsLayoutManager.newBuilder(context)
//            .setChildGravity(Gravity.CENTER)
//            .setScrollingEnabled(false)
//            .setGravityResolver { Gravity.NO_GRAVITY }
//            .setOrientation(ChipsLayoutManager.HORIZONTAL)
//            .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
//            .build()

        filterRecyclerView.layoutManager = filterRecyclerViewLayoutManager

        browseFragmentViewModel.getCategoryTabList()
            .observe(viewLifecycleOwner, categoryTabDataViewModelObserver)
        browseFragmentViewModel.getBrowseList()
            .observe(viewLifecycleOwner, browseDataViewModelObserver)
        browseFragmentViewModel.refreshData(dataFilters)

        imgHeaderImage.setOnClickListener {
            close()
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            if (categoryTab != null) {
                close()
            } else {
                requireActivity().finish()
            }
        }

        txtCategory.setOnClickListener {
            if (categoryTabListList.isNotEmpty())
                requireActivity().openListDialog(categoryTabListList) { data ->
                    txtCategory.text = data.displayName
                    if (data.isFilter) {
                        if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                            sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                        } else {
                            sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        }
                    } else {
                        categoryTab?.let {
                            if (it.displayName == data.displayName) {
                                return@let
                            }
                        }
                        categoryTab = data
                        categoryTabListList.map {
                            if (it == data) {
                                it.show = true
                                it.showFilter = true
                                if (it.filters.isNotEmpty()) {
                                    it.filters.map {
                                        it.isSelected = false
                                    }
                                    it.filters?.get(0)?.isSelected = true
                                }
                            } else {
                                it.show = false
                                it.showFilter = false
                            }
                        }
                        browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
                        dataFilters.apply {
                            type = categoryTab!!.type
                            genre_id = ""
                            filter = ""
                            filter_type = ""
                        }
                        browseFragmentViewModel.refreshData(dataFilters)
//                    setFilterGravity(Gravity.LEFT)
                    }

                }

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
                        val allFilterAdapter =
                            FilterAdapter(activity as Context, this@BaseBrowseFragmentNew)
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
                startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)
            } else {

                val intent = Intent(context, ProfileActivity::class.java)
                startActivity(intent)
            }

        }
        Util.hideKeyBoard(requireActivity())

        notificationContainer.setOnClickListener {
            startActivity(Intent(activity, NotificationListActivity::class.java))
        }

        PreferencesUtils.saveIntegerPreferences("NOTIFICATION_COUNT", 10)
    }

    private fun close() {
        dataFilters.apply {
            type = ""
            genre_id = ""
            filter = ""
            filter_type = ""

        }
        browseFragmentViewModel.refreshData(dataFilters)
        setFilterGravity(Gravity.CENTER)
        categoryTabListList.map {

            it.show = true
            it.showFilter = false

        }
        browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
        categoryTab = null
        txtCategory.text = getString(R.string.category)
    }

    abstract fun setTopBannersHeight()

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

            is CategoryTab -> {
                if (data.isFilter) {
                    if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else {
                        sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                } else {
                    categoryTab?.let {
                        if (it.displayName == data.displayName) {
                            return
                        }
                    }
                    categoryTab = data
                    categoryTabListList.map {
                        if (it == data) {
                            it.show = true
                            it.showFilter = true
                            if (it.filters.isNotEmpty()) {
                                it.filters.map {
                                    it.isSelected = false
                                }
                                it.filters?.get(0)?.isSelected = true
                            }
                        } else {
                            it.show = false
                            it.showFilter = false
                        }
                    }
                    browseFragmentCategoryTabListAdapter?.setItems(categoryTabListList)
                    dataFilters.apply {
                        type = categoryTab!!.type
                        genre_id = ""
                        filter = ""
                        filter_type = ""
                    }
                    browseFragmentViewModel.refreshData(dataFilters)
                    setFilterGravity(Gravity.LEFT)
                }
            }
            is FilterForAdopter -> {
                dataFilters.apply {
                    genre_id = if (type != "GENRES") data.id else ""
                    filter = if (type == "GENRES") data.id else ""
                    filter_type = data.filter_type

                }
                categoryTabListList.map {
                    it.filters.map {
                        if (it.name == data.name) {
                            it.isSelected = true
                        } else {
                            it.isSelected = false
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
        userProfileViewModel.refreshWallet()
        val strProfile = PreferencesUtils.getStringPreferences("profile")
        val userProfileData = Gson().fromJson(strProfile, UserProfileData::class.java)
        userProfileData?.let {
            Utils.displayCircularProfileImage(context, it.user?.image,
                R.drawable.ic_user_profile, R.drawable.ic_user_profile, imgHeaderProfile)
        } ?: Utils.displayCircularProfileImage(context, "",
            R.drawable.ic_user_profile, R.drawable.ic_user_profile, imgHeaderProfile)
        observeUserProfile()
        val allNotifications = notificationDao?.getAllNotifications()
        val unreadNotifications = notificationDao?.getUnreadNotifications()
        notificationContainer.visibility =
            if (allNotifications == null || allNotifications == 0) View.GONE else View.VISIBLE
        if (allNotifications != null && allNotifications > 0) {
            try {
                cart_badge?.let {
                    if (unreadNotifications == 0) {
                        if (it.visibility != View.GONE) {
                            it.visibility = View.GONE;
                        }
                    } else {
                        it.setText(unreadNotifications.toString())
                        if (it.visibility != View.VISIBLE) {
                            it.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun openLanguagePreferenceDialog() {
        val languageId = StringBuilder()

        val boolLanguageArray = BooleanArray(userProfileViewModel.languageList.size)
        val array: Array<String> = Array(userProfileViewModel.languageList.size) {
            userProfileViewModel.languageList[it].language
        }
        val preferredLanguage = userProfileViewModel.getSavesUserProfile()?.preferred_language
        for (index in userProfileViewModel.languageList.indices) {
            val tempLanguage = userProfileViewModel.languageList.get(index)
            if (preferredLanguage?.contains(tempLanguage)!!) {
                boolLanguageArray[index] = true
            }
        }

        MaterialAlertDialogBuilder(appCompatActivity, R.style.AlertDialogTheme)
            .apply {
                setTitle("Select Language")
                setMultiChoiceItems(
                    array,
                    boolLanguageArray,
                    object : DialogInterface.OnMultiChoiceClickListener {

                        override fun onClick(
                            dialog: DialogInterface?,
                            which: Int,
                            isChecked: Boolean
                        ) {
                            boolLanguageArray[which] = isChecked;
                        }

                    })
                setCancelable(false)
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which -> // Do something when click positive button
                        languageId.clear()
                        boolLanguageArray.forEachIndexed { index, isSelected ->
                            if (isSelected) {
                                val audienceCategory = userProfileViewModel.languageList.get(index)
                                val id = audienceCategory.id
                                if (languageId.isEmpty()) {
                                    languageId.append(id)
                                } else {
                                    languageId.append(",$id")
                                }
                            }
                        }

                        val map = HashMap<String, String>()
                        map["languages_ids"] = languageId.toString()
                        progressBar.visible()
                        lifecycleScope.launch {
                            val updateLanguagePreferenceResponse = withContext(Dispatchers.IO) {
                                userProfileViewModel.updateLanguagePreference(map)
                            }
                            progressBar.gone()
                            toast(updateLanguagePreferenceResponse.message)
                            if (updateLanguagePreferenceResponse.status.isSuccess()) {
                                browseFragmentViewModel.refreshData(dataFilters)
                                userProfileViewModel.refreshUserProfile()
                            }
                        }

                    })
                setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->

                })
                create()
                show()

            }
    }
}
