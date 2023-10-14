package com.perseverance.phando.home.dashboard

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.openLanguageDialog
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.Constants
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.category.CategorySideNavAdapter
import com.perseverance.phando.category.DashboardListActivity
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.contactus.Contactus
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.browse.BrowseFragmentViewModel
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.home.dashboard.models.DataFilters
import com.perseverance.phando.home.dashboard.mylist.UserListActivity
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.home.generes.GenresBrowse
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.mediadetails.OfflineMediaListActivity
import com.perseverance.phando.home.profile.ProfileActivity
import com.perseverance.phando.home.profile.UserProfileData
import com.perseverance.phando.home.profile.UserProfileViewModel
import com.perseverance.phando.home.profile.login.LoginActivity
import com.perseverance.phando.notification.NotificationDao
import com.perseverance.phando.notification.NotificationListActivity
import com.perseverance.phando.payment.subscription.SubscriptionPackageActivity
import com.perseverance.phando.search.SearchActivity
import com.perseverance.phando.search.SearchResultActivity
import com.perseverance.phando.settings.SettingsActivity
import com.perseverance.phando.splash.AppInfo
import com.perseverance.phando.splash.AppInfoModel
import com.perseverance.phando.utils.*
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_profile.rate
import kotlinx.android.synthetic.main.fragment_profile.share
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseScreenTrackingActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener, CategorySideNavAdapter.AdapterClick {

    override var screenName = BaseConstants.HOME_SCREEN

    // private var mCustomTabActivityHelper: CustomTabActivityHelper? = null
    private var selectedTab = 0
    private var doubleBackToExitPressedOnce = false


    private val browseFragmentViewModel by lazy {
        ViewModelProvider(this).get(BrowseFragmentViewModel::class.java)
    }

    private val homeActivityViewModel by lazy {
        ViewModelProvider(this@HomeActivity).get(DashboardViewModel::class.java)
    }

    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }


    private val appInfoModelObserver = Observer<AppInfoModel> { appInfoModel ->
        if (appInfoModel!!.throwable == null) {
            onGetAppInfoSuccess(appInfoModel.appInfo)
        }
    }

    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(this@HomeActivity)?.downloadMetadataDao()
    }

    var navController: NavController? = null
    private var notificationDao: NotificationDao? = null

    var mCatListAdapter: CategorySideNavAdapter? = null

    private fun setCatListAdapter() {
        mCatListAdapter = CategorySideNavAdapter(this, categoryTabListList, this)
        rvList.layoutManager =
            LinearLayoutManager(this)
        rvList.adapter = mCatListAdapter
    }

    var isCatVisible = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        notificationDao = AppDatabase.getInstance(this).notificationDao()

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController!!)


        browseFragmentViewModel.getCategoryTabList()
            .observe(this, categoryTabDataViewModelObserver)

        homeActivityViewModel.title.observe(this, Observer {
            txtTitle.text = it.toString()
            if (it.isEmpty()) {
                imgCenter.visible()
            } else {
                imgCenter.gone()
            }
        })

        help?.setOnClickListener(menuOnClickListener)
        tc?.setOnClickListener(menuOnClickListener)

        refundPolicy?.setOnClickListener(menuOnClickListener)
        privacyPolicy?.setOnClickListener(menuOnClickListener)
        tvRate?.setOnClickListener(menuOnClickListener)
        aboutus?.setOnClickListener(menuOnClickListener)
        tvShare?.setOnClickListener(menuOnClickListener)


        /*imgHeaderImage.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
            openProfile()
        }*/



        contactus.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
            startActivity(
                Intent(
                    this,
                    Contactus::class.java
                )
            )
            /*drawer_layout.closeDrawer(Gravity.LEFT)
            openWebview("https://lokdhunstage.phando.com/contactus")*/
        }

        tvGenres.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
            startActivity(
                Intent(
                    this,
                    GenresBrowse::class.java
                )
            )
        }

        txtLanguage.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT)
            //   progressBar.visible()
            openLanguagePreferenceDialog()
        }

        lytMyDownloads.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
             val intent = Intent(this, OfflineMediaListActivity::class.java)
            startActivity(intent)
        }

        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        llProfile.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
            openProfile()
        }

        imgHeaderImage.setOnClickListener {
            drawer_layout.openDrawer(Gravity.RIGHT);
       }

        val allData = downloadMetadataDao.getAllDownloadData()

        if (allData == null || allData.isEmpty()) {
            //txtDownloadOption.gone()
        } else {
           // txtDownloadOption.gone()
        }

       /* txtDownloadOption.setOnClickListener {
            if (allData == null || allData.isEmpty()) {
                toast("Download is empty")
                return@setOnClickListener
            }
            drawer_layout.closeDrawer(Gravity.LEFT);
            val intent = Intent(this@HomeActivity, OfflineMediaListActivity::class.java)
            startActivity(intent)
        }*/

      /*  txtSettings.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.LEFT);
            startActivity(Intent(this,
                SettingsActivity::class.java))
        }*/



        imgNotification.setOnClickListener {
            startActivity(Intent(this, NotificationListActivity::class.java))

        }

        crdPlan.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
            val intent = Intent(this@HomeActivity, SubscriptionPackageActivity::class.java)
            startActivityForResult(intent, 101)
        }

        llCategory.setOnClickListener {
            if (!isCatVisible) {
                imgCatArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_red_arrow_up))
                isCatVisible = true
                rvList.visible()
            } else {
                imgCatArrow.setImageDrawable(resources.getDrawable(R.drawable.ic_red_arrow_down))
                isCatVisible = false
                rvList.gone()
            }

//            drawer_layout.closeDrawer(Gravity.LEFT);
//            homeActivityViewModel.categoryClick()
        }

        txtWishlist.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);
            startActivity(Intent(this@HomeActivity, UserListActivity::class.java))
        }


        homeActivityViewModel.mLanguagesList.observe(this, Observer { mlist ->
            val mLanguageList = mlist
            progressBar.gone()
            openLanguageDialog(mLanguageList) {
                val map = HashMap<String, String>()
                map["languages_ids"] = it.toString()
                lifecycleScope.launch {
                    val updateLanguagePreferenceResponse = withContext(Dispatchers.IO) {
                        userProfileViewModel.updateLanguagePreference(map)
                    }
                    toast(updateLanguagePreferenceResponse.message)
                    homeActivityViewModel.languageClick()
                }
            }
        })



        lytBill.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.RIGHT);

            if (!Utils.isNetworkAvailable(this@HomeActivity)) {
                DialogUtils.showNetworkErrorToast()
                return@setOnClickListener
            }
            val token = PreferencesUtils.getLoggedStatus()
            val url = FeatureConfigClass().baseUrl + "billinghistory?token=" + token
            Util.openWebview(this@HomeActivity, url)
        }

        homeActivityViewModel.getAppInfoMutableLiveData().observe(this, appInfoModelObserver)
        homeActivityViewModel.callForGenres()
        homeActivityViewModel.callForFilters()
        homeActivityViewModel.callLanguage()
        val msg = intent.getStringExtra("msg")
        msg?.let {
            DialogUtils.showDialog(this, "Error!", it, "Close", null, object : AppDialogListener {
                override fun onNegativeButtonPressed() {

                }

                override fun onPositiveButtonPressed() {
                    PreferencesUtils.setLoggedIn("")
                    PreferencesUtils.deleteAllPreferences()
                    downloadMetadataDao?.deleteAll()
                    VideoSdkUtil.deleteAllDownloadedVideo(this@HomeActivity.application)
                }

            })
        }
        // checkForDynamicLink()
        //ad.loadAds(BannerType.SCREEN_HOME)
    }


//    override fun onBackPressed() {
//        if (!navController!!.popBackStack()) {
//            showAppCloseDialog()
//        }
//    }

//    fun showAppCloseDialog() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed()
//            return
//        }
//
//        this.doubleBackToExitPressedOnce = true
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
//
//        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
//

//    }

    private fun openWebview(url: String?) {
        Util.openWebview(this, url)
    }

    val menuOnClickListener = View.OnClickListener {
        drawer_layout.closeDrawer(Gravity.RIGHT);
        when (it.id) {
            refundPolicy.id -> openWebview(Constants.URL_REFUND)
            help.id -> openWebview(Constants.URL_HELP)
            tc.id -> openWebview(Constants.URL_TC)
            privacyPolicy.id -> openWebview(Constants.URL_PRIVACY_POLICY)
            aboutus.id -> openWebview(Constants.URL_ABOUT_US)
            tvRate.id -> rateApplication()
            tvShare.id -> shareApplication()
        }

    }


    private fun rateApplication() {
        try {
            val rateIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${this?.packageName}"))
            startActivity(rateIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.showErrorToast(this,
                "Play Store app is not installed in your device.",
                Toast.LENGTH_SHORT)
        }

    }

    private fun shareApplication() {
        try {
            val shareBody = "https://play.google.com/store/apps/details?id=${this?.packageName}"
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share using..."))
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.showErrorToast(this,
                "Sharing app is not installed in your device.",
                Toast.LENGTH_SHORT)
        }

    }


    private fun showSearchScreen() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivityForResult(intent, BaseConstants.REQUEST_CODE_SEARCH)
        Utils.animateActivity(this, "zero")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == BaseConstants.REQUEST_CODE_SEARCH) {
            val query = data?.extras?.getString(Key.SEARCH_QUERY)
            MyLog.e("Query: " + query)
            val intent = Intent(this, SearchResultActivity::class.java)
            intent.putExtra(Key.SEARCH_QUERY, query)
            startActivityForResult(intent, BaseConstants.REQUEST_CODE_SEARCH_RESULT)
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

        val mLanguageList = userProfileViewModel.languageList as ArrayList
        if (mLanguageList.isNotEmpty()) {
            this.openLanguageDialog(mLanguageList) {
                val map = HashMap<String, String>()
                map["languages_ids"] = it.toString()
                progressBar.visible()
                lifecycleScope.launch {
                    val updateLanguagePreferenceResponse = withContext(Dispatchers.IO) {
                        userProfileViewModel.updateLanguagePreference(map)
                    }
                    progressBar.gone()
                    toast(updateLanguagePreferenceResponse.message)
                    val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }


    fun openProfile() {
        val token = PreferencesUtils.getLoggedStatus()
        if (token.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)
        } else {

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private var categoryTabListList: ArrayList<CategoryTab> = ArrayList<CategoryTab>()

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
                        setCatListAdapter()

                    } else {

                    }

                }

            }

        }

    }
    private fun onGetAppInfoSuccess(appInfo: AppInfo) {
        val storeVersion = appInfo.currentVersion
        val forceUpdate = appInfo.isForceUpdate

        var currentVersion = 0
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            currentVersion = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        MyLog.e("Store app version: $storeVersion")
        MyLog.e("Current app version: $currentVersion")

        if (currentVersion < storeVersion) {
            // Need to update application
            val dialog = AlertDialog.Builder(this, R.style.MaterialDialogSheet)
            dialog.setTitle("Update Available")
            dialog.setMessage("A new version of " + getString(R.string.app_name) + " is available on Play Store. Do you want to update?")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Yes, update") { dialog, which ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")))
                } catch (ex: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
                this@HomeActivity.finish()
            }
            if (!forceUpdate) {
                dialog.setNegativeButton("No, leave it!") { dialog, which -> }
            }
            dialog.setIcon(android.R.drawable.ic_dialog_alert)
            dialog.show()
        }
    }

    override fun onStart() {
        super.onStart()
        // mCustomTabActivityHelper?.bindCustomTabsService(this)
    }

    override fun onStop() {
        super.onStop()
        //  mCustomTabActivityHelper?.unbindCustomTabsService(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show()
        return true
    }

    fun checkForDynamicLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) {
                it?.let {
                    it.link?.let {
                        if (Utils.isNetworkAvailable(this@HomeActivity)) {
                            val sanitizer = UrlQuerySanitizer()
                            sanitizer.allowUnregisteredParamaters = true
                            sanitizer.parseUrl(it.toString())
                            val id = sanitizer.getValue("id")
                            val type = sanitizer.getValue("type")
                            val image = sanitizer.getValue("image")
                            id?.let {
                                val video = Video()
                                video.id = it.toInt()
                                video.type = type
                                video.thumbnail = image
                                startActivity(MediaDetailActivity.getDetailIntent(this@HomeActivity,
                                    video))

                            }

                        }
                    }

                }
            }


    }

    private val profileObserver = Observer<DataLoadingStatus<UserProfileData>> {

        when (it?.status) {
            LoadingStatus.ERROR -> {
                lytBill.gone()
                txtLanguage.gone()
           //     lytMyDownloads.gone()
                txtName.text = "Log in"
                txtPhoneNumber.text = "For better experience"
                it.message?.let {
                    // Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
                }
            }
            LoadingStatus.SUCCESS -> {
                Utils.displayCircularProfileImage(this, it.data?.user?.image,
                    R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, imgHeaderProfile1)

                it.data?.user?.name.let { name ->
                    txtName.text = name
                } ?: {
                    txtName.text = ""
                }
                it.data?.user?.email.let { email ->
                    txtPhoneNumber.text = email
                } ?: {
                    txtPhoneNumber.text = ""
                }
              //  txtBilling.visible()


                if (it.data?.is_subscribed == 1) {

                  //  PreferencesUtils.setIsSubscribe(true)
                    crdPlan.gone()

                } else {
                   // PreferencesUtils.setIsSubscribe(false)
                    crdPlan.visible()
                }

                if (it.data?.is_review == 1) {

                   // PreferencesUtils.setIsReview(1)
                    crdPlan.gone()
                    refundPolicy.gone()

                } else {
                  //  PreferencesUtils.setIsReview(0)
                    crdPlan.visible()
                    refundPolicy.visible()
                }
            }

        }

    }

    private fun observeUserProfile() {
        userProfileViewModel.getUserProfile().observe(this, profileObserver)
        userProfileViewModel.refreshUserProfile()
    }

    override fun onResume() {
        super.onResume()
        userProfileViewModel.refreshUserProfile()
        userProfileViewModel.refreshWallet()
        homeActivityViewModel.callForAppInfo()
        homeActivityViewModel.callLanguage()

        val strProfile = PreferencesUtils.getStringPreferences("profile")
        val userProfileData = Gson().fromJson(strProfile, UserProfileData::class.java)
        /* userProfileData?.let {
             Utils.displayCircularProfileImage(this, it.user?.image,
                 R.drawable.ic_user_profile, R.drawable.ic_user_profile, imgHeaderProfile)
         } ?: Utils.displayCircularProfileImage(this, "",
             R.drawable.ic_user_profile, R.drawable.ic_user_profile, imgHeaderProfile)
             */
        observeUserProfile()

        val allNotifications = notificationDao?.getAllNotifications()
        val unreadNotifications = notificationDao?.getUnreadNotifications()
        llNotification.gone()
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

    private val dataFilters = DataFilters()


    override fun onItemClick(data: CategoryTab) {
        drawer_layout.closeDrawer(Gravity.RIGHT)
//        homeActivityViewModel.categoryClick(data)

        dataFilters.apply {
            type = data.type
            genre_id = ""
            filter = ""
            filter_type = ""
        }
        val intent = Intent(this@HomeActivity, DashboardListActivity::class.java)
        intent.putExtra("FILTER", Gson().toJson(dataFilters))
        intent.putExtra("TITLE", data.displayName)
        startActivity(intent)
    }

}
