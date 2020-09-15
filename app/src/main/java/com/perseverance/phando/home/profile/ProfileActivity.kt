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
import com.google.gson.Gson
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.mediadetails.OfflineMediaListActivity
import com.perseverance.phando.payment.paymentoptions.PaymentActivity
import com.perseverance.phando.payment.paymentoptions.WalletDetailActivity
import com.perseverance.phando.payment.subscription.SubscriptionPackageActivity
import com.perseverance.phando.utils.*
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.activity_user_profile.*

class ProfileActivity : AppCompatActivity() {

    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }
    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(this@ProfileActivity)?.downloadMetadataDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "Profile"

        btnSubscribe.setOnClickListener {
            if (!Utils.isNetworkAvailable(this@ProfileActivity)) {
                DialogUtils.showNetworkErrorToast()
                return@setOnClickListener
            }
            if (btnSubscribe.text.equals("Subscribe")) {
                val intent = Intent(this@ProfileActivity, SubscriptionPackageActivity::class.java)
                startActivityForResult(intent, 101)
            } else {
                val token = PreferencesUtils.getLoggedStatus()
                val url = FeatureConfigClass().baseUrl + "viewsubscription?token=" + token
                Util.openWebview(this@ProfileActivity, url)
            }
        }
        btnBilling.setOnClickListener {
            if (!Utils.isNetworkAvailable(this@ProfileActivity)) {
                DialogUtils.showNetworkErrorToast()
                return@setOnClickListener
            }
            val token = PreferencesUtils.getLoggedStatus()
            val url = FeatureConfigClass().baseUrl + "billinghistory?token=" + token
            Util.openWebview(this@ProfileActivity, url)
        }
        btnWallet.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, PaymentActivity::class.java))
        }

        updateProfile.setOnClickListener {
            if (!Utils.isNetworkAvailable(this@ProfileActivity)) {
                DialogUtils.showNetworkErrorToast()
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
                PreferencesUtils.deleteAllPreferences()
                downloadMetadataDao?.deleteAll()
                VideoSdkUtil.deleteAllDownloadedVideo(this@ProfileActivity.application)
                finish()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no)
            ) { dialog, which ->
            }
            alertDialog.show()
        }

        btnDownloads.setOnClickListener {
            val allData = downloadMetadataDao?.getAllDownloadData()
            if (allData == null || allData.isEmpty()) {
                toast("Download is empty")
                return@setOnClickListener
            }
            val intent = Intent(this@ProfileActivity, OfflineMediaListActivity::class.java)
            startActivity(intent)
        }
        val strProfile = PreferencesUtils.getStringPreferences("profile")
        val userProfileData = Gson().fromJson(strProfile, UserProfileData::class.java)
        userProfileData?.let {
            progressBar.gone()
            userName.visible()
            userName.text = it.user?.name
            userEmail.text = it.user?.email
            it.user?.mobile?.let {
                userMobile.text = it
            } ?: userMobile.gone()

            Utils.displayCircularProfileImage(this@ProfileActivity, it.user?.image,
                    R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, avatar)

            it.is_subscribed?.let {
                if (it == 0) {
                    btnSubscribe.text = "Subscribe"
                } else {
                    btnSubscribe.text = "View Subscriptions"
                }
            }
        }
        observeUserProfile()
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
                        userMobile.text = it
                    } ?: userMobile.gone()

                    Utils.displayCircularProfileImage(this@ProfileActivity, it.data?.user?.image,
                            R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, avatar)

                    it.data?.is_subscribed?.let {
                        if (it == 0) {
                            btnSubscribe.text = "Subscribe"
                        } else {
                            btnSubscribe.text = "View Subscriptions"
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
        when (resultCode) {
            Activity.RESULT_OK -> {
                userProfileViewModel.refreshUserProfile()
            }

        }
    }

}
