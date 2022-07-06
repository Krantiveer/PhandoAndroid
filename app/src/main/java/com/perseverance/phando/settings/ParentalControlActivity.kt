package com.perseverance.phando.settings

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.openParentalPinDialog
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.Data
import com.perseverance.phando.data.MaturityRating
import com.perseverance.phando.data.ParentSettingPost
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.profile.UserProfileViewModel
import kotlinx.android.synthetic.main.activity_parental_control.*
import kotlinx.android.synthetic.main.layout_header_new.*

class ParentalControlActivity : BaseScreenTrackingActivity(),
    ParentalControlAdapter.AdapterClick {

    override var screenName: String = BaseConstants.PARENTAL_ACTIVITY
    var mMaturityRatingsList = arrayListOf<MaturityRating>()
    var mSelectedList = arrayListOf<MaturityRating>()
    var mUserListAdapter: ParentalControlAdapter? = null
    lateinit var mData: Data
    private val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parental_control)
        txtTitle.text = "Parental Control"

        imgBack.setOnClickListener {
            finish()
        }
        txtUpdatePin.setOnClickListener{
            openParentalPinDialog(mData.isPinSet,true) { pin, confirmPin,currentPin ->
                if (mData.isPinSet == 1) {
                    val map = mutableMapOf<String, String>()
                    map["current_pin"] = currentPin
                    map["user_pin"] = pin
                    map["confirm_user_pin"] = confirmPin
                    map["change_pin_item_id"] = "1"
                    userProfileViewModel.setupdateUserPin(map)
                }
            }
        }
        btnSave.setOnClickListener {
            openParentalPinDialog(mData.isPinSet) { pin, confirmPin, _ ->
                if (mData.isPinSet == 0) {
                    val map = mutableMapOf<String, String>()
                    map["user_pin"] = pin
                    map["confirm_user_pin"] = confirmPin
                    map["change_pin_item_id"] = "1"
                    userProfileViewModel.setupdateUserPin(map)
                } else {
                    val map = mutableMapOf<String, String>()
                    map["userpin"] = pin
                    map["setting_section_item_id"] = "1"
                    userProfileViewModel.setValidateUserPin(map)
                }
            }
        }

        swKids.setOnCheckedChangeListener { buttonView, isChecked ->
            mUserListAdapter?.apply {
//                for (i in 0 until mMaturityRatingsList.size) {
//                    mMaturityRatingsList[i].setting_value = 0
//                }
                this.setIsViewDisabled(isChecked)
            }
            if (isChecked) {
                mData.all_age.setting_value = 1
            } else {
                mData.all_age.setting_value = 0
            }

        }

        userProfileViewModel.refreshparentalControl()

        userProfileViewModel.getparentalControl().observe(this, Observer {
            when (it?.status) {
                LoadingStatus.LOADING -> {
                }
                LoadingStatus.ERROR -> {
                    toast(it.message.toString())
                }
                LoadingStatus.SUCCESS -> {
//                    toast(it.data?.message.toString())
                    if (it.data!!.status == "success") {
                        mData = it.data?.data!!
                        swKids.isChecked = it.data.data.all_age.setting_value == 1
                        txtSwName.text = it.data.data.all_age.setting_name
                        mMaturityRatingsList = it.data.data.maturity_rating
                        if (mData.isPinSet == 1) {
                            txtUpdatePin.visible()
                        }else{
                            txtUpdatePin.gone()

                        }
                        setListAdapter()
                    }
                }
            }
        })
        userProfileViewModel.parentalSetting.observe(this, Observer {
            when (it?.status) {
                LoadingStatus.LOADING -> {
                }
                LoadingStatus.ERROR -> {
                    toast(it.message.toString())
                }
                LoadingStatus.SUCCESS -> {
                    toast(it.data?.message.toString())
                }
            }
        })

        userProfileViewModel.updateUserPin.observe(this, Observer {
            when (it?.status) {
                LoadingStatus.LOADING -> {

                }
                LoadingStatus.ERROR -> {
                    toast(it.message.toString())
                }
                LoadingStatus.SUCCESS -> {
                    toast(it.data?.message.toString())
                    if (it.data!!.status == "success") {
                        val array = arrayListOf<String>()
                        if (mData.all_age.setting_value == 1) {
                            array.add(mData.all_age.setting_default_value)
                        }
                        if (!mUserListAdapter?.isViewDisabled!!) {
                            for (data in mMaturityRatingsList) {
                                if (data.setting_value == 1) {
                                    array.add(data.setting_default_value)
                                }
                            }
                        }
                        val postData = ParentSettingPost(ratings = array)
                        userProfileViewModel.setParentalSetting(postData)
                    }
                }
            }
        })

        userProfileViewModel.validateUserPin.observe(this, Observer {
            when (it?.status) {
                LoadingStatus.LOADING -> {

                }
                LoadingStatus.ERROR -> {
                    toast(it.message.toString())
                }
                LoadingStatus.SUCCESS -> {
                    toast(it.data?.message.toString())
                    if (it.data!!.status == "success") {
                        val array = arrayListOf<String>()
                        if (mData.all_age.setting_value == 1) {
                            array.add(mData.all_age.setting_default_value)
                        }
                        if (!mUserListAdapter?.isViewDisabled!!) {
                            for (data in mMaturityRatingsList) {
                                if (data.setting_value == 1) {
                                    array.add(data.setting_default_value)
                                }
                            }
                        }
                        val postData = ParentSettingPost(ratings = array)
                        userProfileViewModel.setParentalSetting(postData)
                    }
                }
            }
        })


    }

    private fun setListAdapter() {
        mUserListAdapter = ParentalControlAdapter(this, mMaturityRatingsList, this)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = mUserListAdapter
        mUserListAdapter?.apply {
            this.setIsViewDisabled(swKids.isChecked)
        }
    }

    override fun onItemClick(data: MaturityRating) {

    }

}