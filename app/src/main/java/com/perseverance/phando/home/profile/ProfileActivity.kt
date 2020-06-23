package com.perseverance.phando.home.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.OfflineMediaListActivity
import com.perseverance.phando.payment.subscription.SubscriptionPackageActivity
import com.perseverance.phando.splash.SplashActivity
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.PreferencesUtils
import com.perseverance.phando.utils.Util
import com.perseverance.phando.utils.Utils
import com.qait.sadhna.LoginActivity
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.activity_user_profile.*

class ProfileActivity : AppCompatActivity() {

    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "Profile"
        observeUserProfile()
        btnSubscribe.setOnClickListener {
            if (!Utils.isNetworkAvailable(this@ProfileActivity)){
                toast(BaseConstants.NETWORK_ERROR)
                return@setOnClickListener
            }
            if (btnSubscribe.text.equals("Subscribe")) {
                val intent = Intent(this@ProfileActivity, SubscriptionPackageActivity::class.java)
                startActivityForResult(intent, 101)
            }else{
                val token = PreferencesUtils.getLoggedStatus()
                val url = FeatureConfigClass().baseUrl + "viewsubscription?token=" + token
                Util.openWebview(this@ProfileActivity, url)
            }
        }
        btnBilling.setOnClickListener {
            if (!Utils.isNetworkAvailable(this@ProfileActivity)){
                toast(BaseConstants.NETWORK_ERROR)
                return@setOnClickListener
            }
                val token = PreferencesUtils.getLoggedStatus()
                val url = FeatureConfigClass().baseUrl + "billinghistory?token=" + token
                Util.openWebview(this@ProfileActivity, url)
        }

        updateProfile.setOnClickListener {
            if (!Utils.isNetworkAvailable(this@ProfileActivity)){
                toast(BaseConstants.NETWORK_ERROR)
                return@setOnClickListener
            }
            val token = PreferencesUtils.getLoggedStatus()
            val url = FeatureConfigClass().baseUrl + "updateUserProfile?token=" + token
            MyLog.e("updateUserProfile", url)
            Util.openWebview(this@ProfileActivity, url)
        }
        btnLogout.setOnClickListener {
            val alertDialog = MaterialAlertDialogBuilder(this@ProfileActivity, R.style.AlertDialogTheme).create()
            alertDialog.setIcon(R.mipmap.ic_launcher)
            alertDialog.setTitle("Logout")
            alertDialog.setMessage(resources.getString(R.string.logout_message))
            alertDialog.setCancelable(false)

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.yes)
            ) { dialog, which ->
                PreferencesUtils.setLoggedIn("")
                finish()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no)
            ) { dialog, which ->
            }
            alertDialog.show()
        }

        btnDownloads.setOnClickListener {
            val downloadedVideoList = VideoSdkUtil.getDownloadedVideo(this.application)
            if (downloadedVideoList.isEmpty()) {
                toast("Download is empty")
                return@setOnClickListener
            }
            val intent = Intent(this@ProfileActivity, OfflineMediaListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun observeUserProfile() {

        userProfileViewModel.getUserProfile().observe(this, Observer {
            progressBar.gone()

            when (it?.status) {
                LoadingStatus.LOADING -> {
                    progressBar.visible()
                }
                LoadingStatus.ERROR -> {
                    it.message?.let {
                        Toast.makeText(this@ProfileActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
                LoadingStatus.SUCCESS -> {
                    progressBar.gone()
                    userName.visible()
                    userName.text = it.data?.user?.name
                    userEmail.text = it.data?.user?.email
                   it.data?.user?.mobile?.let {
                       userMobile.text =it
                   }?: userMobile.gone()

                    Utils.displayCircularProfileImage(this@ProfileActivity, it.data?.user?.image,
                            R.drawable.ic_user_avatar,R.drawable.ic_user_avatar,avatar)

                    it.data?.is_subscribed?.let {
                        if (it==0){
                            btnSubscribe.text="Subscribe"
                        }else{
                            btnSubscribe.text="View Subscriptions"
                        }
                    }


                }

            }



        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        userProfileViewModel.refreshUserProfile()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            Activity.RESULT_OK ->{
                userProfileViewModel.refreshUserProfile()
            }

        }
    }

}
