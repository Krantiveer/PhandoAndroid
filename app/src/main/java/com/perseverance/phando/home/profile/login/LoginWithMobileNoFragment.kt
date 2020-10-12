package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import kotlinx.android.synthetic.main.activity_p2_login_with_mobileno.*
import kotlinx.android.synthetic.main.login_link_container.*


class LoginWithMobileNoFragment : BaseUserLoginFragment() {
    override var screenName= BaseConstants.LOGIN_WITH_MOBILE_SCREEN

    lateinit var mobileNo: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_login_with_mobileno, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ccp.registerCarrierNumberEditText(inputMobile)
        next.setOnClickListener {
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
//                val intent = Intent(appCompatActivity, P2OTPVerificationActivity::class.java)
//                intent.putExtra("MOBILE", mobileNo)
//                intent.putExtra("COUNTRY_CODE", countryCode)
//                startActivity(intent)

                val bundle = bundleOf(
                        "MOBILE" to inputMobile.text.toString().trim(),
                        "COUNTRY_CODE" to countryCode

                )
                val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginWithMobileNoActivity, false).build()
                navigator.navigate(R.id.P2OTPVerificationActivity, bundle, navOption)

            } else {
                Toast.makeText(appCompatActivity, "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG).show()
            }
        }
        linkMobile.gone()
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

}
