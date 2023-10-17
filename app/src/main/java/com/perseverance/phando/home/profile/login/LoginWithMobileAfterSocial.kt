package com.perseverance.phando.home.profile.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.LoginResponse
import kotlinx.android.synthetic.main.activity_login_after_social.*
import kotlinx.android.synthetic.main.activity_p2_login_with_mobileno.*
import kotlinx.android.synthetic.main.activity_p2_login_with_mobileno.ccp
import kotlinx.android.synthetic.main.activity_p2_login_with_mobileno.inputMobile
import kotlinx.android.synthetic.main.activity_wallet_history.*
import kotlinx.android.synthetic.main.login_link_container.*
import java.util.*
import kotlin.collections.HashMap

class LoginWithMobileAfterSocial : BaseUserLoginFragment() {
    override var screenName= BaseConstants.LOGIN_WITH_MOBILE_SCREEN_SOCIAL

    lateinit var mobileNo: String
    lateinit var token: String

    val sendOtpObserver = Observer<DataLoadingStatus<BaseResponse>> {
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
                loginResponse?.message?.let {
                    toast(it, Toast.LENGTH_LONG)
                    val countryCode = ccp.selectedCountryCode
                    val bundle = bundleOf(
                        "MOBILE" to inputMobile.text.toString().trim(),
                        "COUNTRY_CODE" to countryCode,
                        "FROM_SIGNUP" to "Social"
                    )

                    val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginWithMobileAfterSocial, false).build()
                    navigator.navigate(R.id.P2OTPVerificationActivity, bundle, navOption)


                }
            }

        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_login_after_social, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileViewModel.getOTPTDataSocial.observe(viewLifecycleOwner, sendOtpObserver)

        var deviceId = UUID.randomUUID().toString()
        val  name:  String  = Build.MANUFACTURER + " - " + Build.MODEL
        token = arguments?.getString("token").toString()
        Log.e("@@tokenFromSocial",token)

        ccp.registerCarrierNumberEditText(inputMobile)

        tvSkip.setOnClickListener{
            val intent = Intent(requireActivity(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()

        }
        nextSocial.setOnClickListener {
            val countryCode = ccp.selectedCountryCode
            mobileNo = inputMobile.text.toString()
            if (mobileNo.isNullOrBlank()) {
                toast("Please enter Mobile Number", Toast.LENGTH_LONG)
                return@setOnClickListener
            }
            if (countryCode == "91" && mobileNo.length != 10) {
                toast("Please enter 10 digit Mobile Number", Toast.LENGTH_LONG)
            }
            if (ccp.isValidFullNumber) {


                val map: MutableMap<String, String> = HashMap()
                map["country_code"] = countryCode
                map["mobile"] = mobileNo
                map["device_type"] = "Android"
                map["device_id"] = deviceId
                map["device_name"] = name
                userProfileViewModel.getOTPSocial(map)

              /*  val bundle = bundleOf(
                        "MOBILE" to inputMobile.text.toString().trim(),
                        "COUNTRY_CODE" to countryCode,
                    "FROM_SIGNUP" to "Social"
                )

                val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginWithMobileAfterSocial, false).build()
                navigator.navigate(R.id.P2OTPVerificationActivity, bundle, navOption)*/

            } else {
                Toast.makeText(appCompatActivity, "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG).show()
            }
        }

    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_p2_login_with_mobileno)
//        TrackingUtils.sendScreenTracker(BaseConstants.LOGIN_WITH_OTP)
//        ccp.registerCarrierNumberEditText(inputMobile)
////        val countryCode = ccp.selectedCountryCode
////        if (countryCode=="91"){
////            val maxLength = 10
////            val fArray = arrayOfNulls<InputFilter>(1)
////            fArray[0] = LengthFilter(maxLength)
////            inputMobile.filters = fArray
////        }
//        next.setOnClickListener {
//            val countryCode = ccp.selectedCountryCode
//            mobileNo = inputMobile.text.toString()
//            if (mobileNo.isNullOrBlank()) {
//                toast("Please enter Mobile Number", Toast.LENGTH_LONG)
//                return@setOnClickListener
//            }
//            if (countryCode == "91" && mobileNo.length != 10) {
//                toast("Please enter 10 digit Mobile Number", Toast.LENGTH_LONG)
//            }
//            if (ccp.isValidFullNumber) {
//                val intent = Intent(this@P2LoginWithMobileNoActivity, P2OTPVerificationActivity::class.java)
//                intent.putExtra("MOBILE", mobileNo)
//                intent.putExtra("COUNTRY_CODE", countryCode)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this@P2LoginWithMobileNoActivity, "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG).show()
//            }
//        }
//        linkMobile.gone()
//
//    }


    override fun onGetOtpSuccess(baseResponse: BaseResponse) {

    }

    override fun onGetOtpSuccessSocial(baseResponse: BaseResponse) {

    }

}
