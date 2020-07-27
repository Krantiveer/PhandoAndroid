package com.perseverance.phando.home.dashboard

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.phando.AdsUtil.BannerType
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.search.SearchActivity
import com.perseverance.phando.search.SearchResultActivity
import com.perseverance.phando.splash.AppInfo
import com.perseverance.phando.splash.AppInfoModel
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.TrackingUtils
import com.perseverance.phando.utils.Utils

class HomeActivity : AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener{

   // private var mCustomTabActivityHelper: CustomTabActivityHelper? = null
    private var selectedTab = 0
    private var doubleBackToExitPressedOnce = false

    private val homeActivityViewModel by lazy {
        ViewModelProviders.of(this@HomeActivity).get(DashboardViewModel::class.java)
    }


    private val appInfoModelObserver = Observer<AppInfoModel> { appInfoModel ->
        if (appInfoModel!!.throwable == null) {
            onGetAppInfoSuccess(appInfoModel.appInfo)
        }
    }
    var navController: NavController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          setContentView(R.layout.activity_home)

      //  mCustomTabActivityHelper = CustomTabActivityHelper()

         val navView: BottomNavigationView = findViewById(R.id.bottomNavigation)
         navController = findNavController(R.id.nav_host_fragment)
         navView.setupWithNavController(navController!!)
//         navView.setOnNavigationItemSelectedListener {item ->
//
//            onNavDestinationSelected(item, Navigation.findNavController(this, R.id.nav_host_fragment))
//
//        }
       // navView.setOnNavigationItemSelectedListener(this)

        homeActivityViewModel.getAppInfoMutableLiveData().observe(this, appInfoModelObserver)
        homeActivityViewModel.callForGenres()
        homeActivityViewModel.callForFilters()
        homeActivityViewModel.callLanguage()
        homeActivityViewModel.callForAppInfo()
        homeActivityViewModel.callCountryCode()
        TrackingUtils.sendScreenTracker( BaseConstants.HOME)
        val msg = intent.getStringExtra("msg")
        msg?.let {
            DialogUtils.showDialog(this,"Error!",it,"Close",null,object : AppDialogListener {
                override fun onNegativeButtonPressed() {

                }

                override fun onPositiveButtonPressed() {

                }

            })
        }
 ad.loadAds(BannerType.SCREEN_HOME)
    }


    override fun onBackPressed() {
        if (!navController!!.popBackStack()) {
            showAppCloseDialog()
        }
    }

    fun showAppCloseDialog() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)


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

    fun onGetAppInfoSuccess(appInfo: AppInfo) {
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
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                } catch (ex: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
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
        Toast.makeText(this,item.toString(),Toast.LENGTH_LONG).show()
        return true
    }
}
