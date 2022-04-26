package com.perseverance.phando.home.profile.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.LoginResponse
import com.perseverance.phando.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_p2_otp_verification.*
import kotlinx.android.synthetic.main.login_link_container.*


class OTPVerificationFragment : BaseOTPVerificationFragment() {
    override var screenName= BaseConstants.OTP_VERIFICATION_SCREEN

    val verifyOtpObserver = Observer<DataLoadingStatus<LoginResponse>> {
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
                var loginResponse = it.data
                loginResponse?.accessToken?.let {
                    onVerifyOtpSuccess(loginResponse)
                } ?: loginResponse?.message?.let {
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

            userProfileViewModel.verifyOTP(map)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileViewModel.verifyOTPTForLoginData.observe(viewLifecycleOwner, verifyOtpObserver)
        val fromSignup = arguments?.getBoolean("FRON_SIGNUP", false) ?: false

        if (!fromSignup) {
            val map: MutableMap<String, String> = HashMap()
            map["country_code"] = countryCode
            map["mobile"] = mobileNo
            userProfileViewModel.getOTP(map)
        } else {
            startTimer()
        }
        linkMobile.gone()
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        userProfileViewModel.verifyOTPTForLoginData.observe(this, verifyOtpObserver)
//        //val fromSignup = intent.getBooleanExtra("FRON_SIGNUP", false)
//
//        if (!fromSignup) {
//            val map: MutableMap<String, String> = HashMap()
//            map["country_code"] = countryCode
//            map["mobile"] = mobileNo
//            userProfileViewModel.getOTP(map)
//        } else {
//            startTimer()
//        }
//        linkMobile.gone()
//    }
//
    fun onVerifyOtpSuccess(loginResponse: LoginResponse) {
        countDownTimer?.cancel()
        PreferencesUtils.setLoggedIn(loginResponse.accessToken)

        if (requireActivity().intent.hasExtra("login_error")) {
            startActivity(Intent(requireContext(), HomeActivity::class.java))
        }

        appCompatActivity.setResult(Activity.RESULT_OK)
        appCompatActivity.finish()
    }

}
