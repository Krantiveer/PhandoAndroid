package com.perseverance.phando.settings

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.NotificationsData
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.profile.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_notifcation_settings.*
import kotlinx.android.synthetic.main.layout_header_new.*

class NotificationsSettingsActivity : BaseScreenTrackingActivity(),
    NotificationsSettingsAdapter.AdapterClick {
    override var screenName: String = BaseConstants.NOTIFICATIONS_ACTIVITY


    var mNotificationList = arrayListOf<NotificationsData>()
    var mUserListAdapter: NotificationsSettingsAdapter? = null
    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifcation_settings)
        txtTitle.text = "Notifications Settings"

        imgBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            userProfileViewModel.setNotificationsSetting(map)
        }

        userProfileViewModel.refreshNotificationsSettings()
        userProfileViewModel.getNotificationsSettings().observe(this, Observer {
            when (it?.status) {
                LoadingStatus.LOADING -> {
                }
                LoadingStatus.ERROR -> {
                    toast(it.message.toString())
                }
                LoadingStatus.SUCCESS -> {
                    mNotificationList = it.data?.data!!
                    for (i in 0 until mNotificationList.size) {
                        map[mNotificationList[i].id] = mNotificationList[i].value
                    }
                    setListAdapter()
                }
            }
        })
        userProfileViewModel.setNotificationsSettings.observe(this, Observer {
            when (it?.status) {
                LoadingStatus.LOADING -> {
                }
                LoadingStatus.ERROR -> {
                    toast(it.message.toString())
                }
                LoadingStatus.SUCCESS -> {
                    toast(it.data?.message.toString())
//                    finish()
                }
            }
        })
    }

    private fun setListAdapter() {
        mUserListAdapter = NotificationsSettingsAdapter(this, mNotificationList, this)
        rvList.layoutManager =
            LinearLayoutManager(this)
        rvList.adapter = mUserListAdapter
    }

    val map = mutableMapOf<String, Boolean>()

    override fun onItemClick(data: NotificationsData) {
        map[data.id] = data.value

    }

}