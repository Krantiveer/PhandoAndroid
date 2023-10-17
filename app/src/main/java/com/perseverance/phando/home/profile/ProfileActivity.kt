package com.perseverance.phando.home.profile

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.isSuccess
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.dashboard.mylist.UserListActivity
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.OfflineMediaListActivity
import com.perseverance.phando.home.profile.login.LoginActivity
import com.perseverance.phando.payment.paymentoptions.PaymentActivity
import com.perseverance.phando.payment.subscription.SubscriptionPackageActivity
import com.perseverance.phando.utils.*
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.layout_header_new.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileActivity : BaseScreenTrackingActivity() {
    override var screenName = BaseConstants.PROFILE_SCREEN

    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(this@ProfileActivity)?.downloadMetadataDao()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
        txtTitle.text = "Profile"

        imgBack.setOnClickListener {
            finish()
        }
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
        btnMyList.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, UserListActivity::class.java))
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
            val alertDialog =
                MaterialAlertDialogBuilder(this@ProfileActivity, R.style.AlertDialogTheme).create()
            alertDialog.setIcon(R.drawable.app_logo)
            alertDialog.setTitle("Logout")
            alertDialog.setMessage(resources.getString(R.string.logout_message))
            alertDialog.setCancelable(false)

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.yes)
            ) { dialog, which ->
                PreferencesUtils.setLoggedIn("")
                PreferencesUtils.deleteAllPreferences()
                downloadMetadataDao?.deleteAll()
                VideoSdkUtil.deleteAllDownloadedVideo(this@ProfileActivity.application)

                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no)
            ) { dialog, which ->
            }
            alertDialog.show()
        }



        val allData = downloadMetadataDao.getAllDownloadData()
        if (allData == null || allData.isNullOrEmpty()) {
            cvDownload.gone()
        } else {
            cvDownload.gone()
        }

        cvDownload.setOnClickListener {
            if (allData == null || allData.isEmpty()) {
                toast("Download is empty")
                return@setOnClickListener
            }
            val intent = Intent(this@ProfileActivity, OfflineMediaListActivity::class.java)
            startActivity(intent)
        }
        userProfileViewModel.getSavesUserProfile()?.let {
            progressBar.gone()
            userName.visible()


            userName.text = it.user.name
            userEmail.text = it.user.email
            it.user.mobile.let {
                userMobile.text = it
            }

            if (it.user.name.isNullOrEmpty()) {
                userName.gone()
            }
            if (it.user.email.isNullOrEmpty()) {
                userEmail.gone()
            }
            if (it.user.mobile.isNullOrEmpty() && it.user.mobile.length > 4) {
                userMobile.gone()
            }

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
        btnChangeLanguage.setOnClickListener {

            openLanguagePreferenceDialog()

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


                    if (it.data?.user?.name!!.isNullOrEmpty()) {
                        userName.gone()
                    }
                    if (it.data.user.email.isNullOrEmpty()) {
                        userEmail.gone()
                    }
                    if (it.data.user.mobile.length < 4) {
                        userMobile.gone()
                    }

                    Utils.displayCircularProfileImage(this@ProfileActivity, it.data?.user?.image,
                        R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, avatar)

                    if(it.data.subscription_start_date != null){
                        myPackageDateTime.text = "Expires on " + it.data.subscription_end_date
                    }

                    if(it.data.current_subscription != null){
                        userSubId.text = "Subscriber ID : " + it.data.current_subscription.id
                    }
                    if(it.data.package_name != null){
                        myPackageName.text = it.data.package_name
                    }
                    if (it.data.current_subscription != null){

                        if(it.data.price != null){
                            myPackagePrice.text = it.data.current_subscription.plan.currency+" " +it.data.price.toString()
                        }
                    }

                    it.data.is_subscribed.let {
                        if (it == 0) {
                            cardSubPlan.gone()
                            //btnCancelSubs.gone()
                            lytSubs.gone()
                        } else {
                            cardSubPlan.visible()
                           // btnCancelSubs.visible()
                            lytSubs.visible()

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

        MaterialAlertDialogBuilder(this@ProfileActivity, R.style.AlertDialogTheme)
            .apply {
                setTitle("Select Language")
                setMultiChoiceItems(
                    array,
                    boolLanguageArray,
                    object : DialogInterface.OnMultiChoiceClickListener {

                        override fun onClick(
                            dialog: DialogInterface?,
                            which: Int,
                            isChecked: Boolean,
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
                                if (languageId.isNullOrEmpty()) {
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
