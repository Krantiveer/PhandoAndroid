package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import kotlinx.android.synthetic.main.activity_p2_otp_verification.*
import kotlinx.android.synthetic.main.login_link_container.*


class ChangePasswordOTPVerificationFragment : BaseOTPVerificationFragment() {
    override var screenName= BaseConstants.OTP_VERIFICATION_SCREEN
    private lateinit var password: String

    val verifyOtpForforgotPsswordObserver = Observer<DataLoadingStatus<BaseResponse>> {
        dismissProgress()

        when (it?.status) {
            LoadingStatus.LOADING -> {
                showProgress()
            }
            LoadingStatus.ERROR -> {
                it.message?.let {
                    toast(it, Toast.LENGTH_LONG)
                }
            }
            LoadingStatus.SUCCESS -> {

                var baseResponse = it.data
                baseResponse?.let {
                    onVerifyOtpSuccess(it)

                } ?: baseResponse?.message?.let {
                    toast(it, Toast.LENGTH_LONG)
                }


            }

        }

    }

    override fun verifyOTP() {

        if (inputOTP.otp.isNullOrBlank()) {
            toast("Please enter OTP", Toast.LENGTH_LONG)
        } else {
            val map: MutableMap<String, String> = HashMap()
            map["country_code"] = countryCode
            map["mobile"] = mobileNo
            map["otp"] = inputOTP.otp!!
            map["password"] = password
            map["password_confirmation"] = password
            userProfileViewModel.verifyOTPForForgotPassword(map)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileViewModel.verifyOTPForForgotPasswordData.observe(viewLifecycleOwner, verifyOtpForforgotPsswordObserver)
        password = arguments?.getString("PASSWORD")!!

        val map: MutableMap<String, String> = HashMap()
        map["country_code"] = countryCode
        map["mobile"] = mobileNo
        userProfileViewModel.getOTP(map)
        linkMobile.gone()
    }

    override fun onGetOtpSuccessSocial(baseResponse: BaseResponse) {

    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        userProfileViewModel.verifyOTPForForgotPasswordData.observe(this, verifyOtpForforgotPsswordObserver)
//        password = intent.getStringExtra("PASSWORD")
//
//        val map: MutableMap<String, String> = HashMap()
//        map["country_code"] = countryCode
//        map["mobile"] = mobileNo
//        userProfileViewModel.getOTP(map)
//        linkMobile.gone()
//    }

    fun onVerifyOtpSuccess(loginResponse: BaseResponse) {
        countDownTimer?.cancel()
        navigator.navigate(R.id.P2PasswordChangedActivity)

    }
}
