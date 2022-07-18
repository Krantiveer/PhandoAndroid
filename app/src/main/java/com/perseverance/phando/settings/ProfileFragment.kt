package com.perseverance.phando.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.Constants
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.dashboard.mylist.UserListActivity
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.home.profile.UserProfileViewModel
import com.perseverance.phando.home.profile.login.LoginActivity
import com.perseverance.phando.utils.*
import com.perseverance.phando.utils.Util.Companion.openWebview
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {
    override var screenName = BaseConstants.PROFILE_SCREEN

    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(requireContext())?.downloadMetadataDao()
    }

    private val homeActivityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    val menuOnClickListener = View.OnClickListener {
        when (it.id) {
            help.id -> openWebview(requireActivity(), Constants.URL_HELP)
            tc.id -> openWebview(requireActivity(), Constants.URL_TC)
            privacyPolicy.id -> openWebview(requireActivity(), Constants.URL_PRIVACY_POLICY)
            aboutus.id -> openWebview(requireActivity(), Constants.URL_ABOUT_US)
            rate.id -> rateApplication()
            share.id -> shareApplication()
        }
    }


    private fun rateApplication() {
        try {
            val rateIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${activity?.packageName}"))
            startActivity(rateIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.showErrorToast(activity,
                "Play Store app is not installed in your device.",
                Toast.LENGTH_SHORT)
        }

    }

    private fun shareApplication() {
        try {
            val shareBody = "https://play.google.com/store/apps/details?id=${activity?.packageName}"
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share using..."))
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.showErrorToast(activity,
                "Sharing app is not installed in your device.",
                Toast.LENGTH_SHORT)
        }

    }

    fun openProfile() {
        val token = PreferencesUtils.getLoggedStatus()
        if (token.isEmpty()) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)
        } else {
            if (!Utils.isNetworkAvailable(requireContext())) {
                DialogUtils.showNetworkErrorToast()
                return
            }
            val url = FeatureConfigClass().baseUrl + "updateUserProfile?token=" + token
            MyLog.e("updateUserProfile", url)
            Util.openWebview(requireActivity(), url)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivityViewModel.title.value="Search"

        help?.setOnClickListener(menuOnClickListener)
        tc?.setOnClickListener(menuOnClickListener)
        privacyPolicy?.setOnClickListener(menuOnClickListener)
        aboutus?.setOnClickListener(menuOnClickListener)
        rate?.setOnClickListener(menuOnClickListener)
        share?.setOnClickListener(menuOnClickListener)

        txtSettings.setOnClickListener {
            startActivity(Intent(requireActivity(),
                SettingsActivity::class.java))
        }

        txtWishlist.setOnClickListener {
            startActivity(Intent(requireActivity(), UserListActivity::class.java))
        }

        llProfile.setOnClickListener {
            openProfile()
        }

        txtBilling.setOnClickListener {
            if (!Utils.isNetworkAvailable(requireActivity())) {
                DialogUtils.showNetworkErrorToast()
                return@setOnClickListener
            }
            val token = PreferencesUtils.getLoggedStatus()
            val url = FeatureConfigClass().baseUrl + "billinghistory?token=" + token
            Util.openWebview(requireActivity(), url)
        }

        userProfileViewModel.getSavesUserProfile()?.let {
            userName.visible()

            txtBilling.visible()
            txtWishlist.visible()
            btnLogout.visible()
            userName.visible()
            updateProfile.visible()
            userName.text = it.user.name
            userEmail.text = it.user.email
            it.user.mobile.let {
                if (it.length > 4) {
                    userMobile.visible()
                    userMobile.text = it
                }
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

            Utils.displayCircularProfileImage(requireActivity(), it.user.image,
                R.drawable.ic_user_avatar, R.drawable.ic_user_avatar, avatar)

            it.is_subscribed?.let {
                if (it == 0) {
                    btnSubscribe.text = "Subscribe"
                } else {
                    btnSubscribe.text = "View Subscriptions"
                }
            }
        }

        updateProfile.setOnClickListener {
            if (!Utils.isNetworkAvailable(requireContext())) {
                DialogUtils.showNetworkErrorToast()
                return@setOnClickListener
            }
            val token = PreferencesUtils.getLoggedStatus()
            val url = FeatureConfigClass().baseUrl + "updateUserProfile?token=" + token
            MyLog.e("updateUserProfile", url)
            Util.openWebview(requireActivity(), url)
        }


        btnLogout.setOnClickListener {
            val alertDialog =
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).create()
            alertDialog.setIcon(R.mipmap.ic_launcher)
            alertDialog.setTitle("Logout")
            alertDialog.setMessage(resources.getString(R.string.logout_message))
            alertDialog.setCancelable(false)

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.yes)
            ) { dialog, which ->
                PreferencesUtils.setLoggedIn("")
                PreferencesUtils.deleteAllPreferences()
                downloadMetadataDao.deleteAll()
                VideoSdkUtil.deleteAllDownloadedVideo(requireActivity().application)

                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.no)
            ) { dialog, which ->
            }
            alertDialog.show()
        }
        observeUserProfile()

    }

    private fun observeUserProfile() {
        userProfileViewModel.getUserProfile().observe(viewLifecycleOwner, Observer {

            when (it?.status) {
                LoadingStatus.LOADING -> {
                }
                LoadingStatus.ERROR -> {
                    it.message?.let {
                        txtBilling.gone()
                        txtWishlist.gone()
                    }
                }
                LoadingStatus.SUCCESS -> {
                    txtBilling.visible()
                    txtWishlist.visible()
                    btnLogout.visible()
                    userName.visible()
                    updateProfile.visible()
                    userName.text = it.data?.user?.name
                    userEmail.text = it.data?.user?.email
                    it.data?.user?.mobile?.let {
                        userMobile.visible()
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

                    Utils.displayCircularProfileImage(requireContext(), it.data?.user?.image,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                userProfileViewModel.refreshUserProfile()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userProfileViewModel.refreshUserProfile()
    }

}