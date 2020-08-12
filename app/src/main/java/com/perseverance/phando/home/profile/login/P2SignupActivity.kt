package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import com.perseverance.phando.R
import com.perseverance.phando.retrofit.LoginResponse
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_p2_signup.*

class P2SignupActivity : BaseSignupActivity() {
    var countryCode = ""

    override fun onRegisterSuccess(loginResponse: LoginResponse) {
        val bundle = bundleOf(
                "MOBILE" to inputMobile.text.toString().trim(),
                "COUNTRY_CODE" to countryCode,
                "FRON_SIGNUP" to true

        )
        val navOption = NavOptions.Builder().setPopUpTo(R.id.P2OTPVerificationActivity, false).build()
        navigator.navigate(R.id.P2OTPVerificationActivity, bundle, navOption)
//        val intent = Intent(appCompatActivity, P2OTPVerificationActivity::class.java)
//        intent.putExtra("ONLY_VERIFY_OTP", true)
//        intent.putExtra("MOBILE", inputMobile.text.toString().trim())
//        intent.putExtra("COUNTRY_CODE", countryCode)
//        intent.putExtra("FRON_SIGNUP", true)
//        startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input_password.isLongClickable = false
        input_confirm_password.isLongClickable = false
        ccp.registerCarrierNumberEditText(inputMobile)
        doSignup.setOnClickListener {
            Utils.hideKeyboard(appCompatActivity)
            if (input_first_name.text.toString().isNullOrBlank()) {
                Toast.makeText(appCompatActivity, "Please enter name", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
//                else if(input_email.text.toString().isNullOrBlank()) {
//                    Toast.makeText(this@RegisterActivity, "Please enter email", Toast.LENGTH_LONG).show()
//                    return@setOnClickListener
//                }
            else if (inputMobile.text.toString().isNullOrBlank()) {
                Toast.makeText(appCompatActivity, "Please enter Mobile Number", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (input_password.text.toString().isNullOrBlank()) {
                Toast.makeText(appCompatActivity, "Please enter password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (!ccp.isValidFullNumber) {
                Toast.makeText(appCompatActivity, "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (checkPassword(input_password.text.toString(), input_confirm_password.text.toString())) {
                countryCode = ccp.selectedCountryCode
                val map: MutableMap<String, String> = HashMap()
                map["email"] = input_email.text.toString()
                map["password"] = input_password.text.toString()
                map["name"] = input_first_name.text.toString()
                map["country_code"] = countryCode
                map["mobile"] = inputMobile.text.toString()
                doRegister(map)
            } else {
                Toast.makeText(appCompatActivity, "Password and Confirm Password don't match", Toast.LENGTH_LONG).show()
            }

        }

    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_p2_signup)
//        TrackingUtils.sendScreenTracker(BaseConstants.SIGNUP)
//        supportActionBar?.title = ""
//        input_password.isLongClickable = false
//        input_confirm_password.isLongClickable = false
//        ccp.registerCarrierNumberEditText(inputMobile)
//        signup.setOnClickListener {
//            Utils.hideKeyboard(appCompatActivity)
//            if (input_first_name.text.toString().isNullOrBlank()) {
//                Toast.makeText(appCompatActivity, "Please enter name", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
////                else if(input_email.text.toString().isNullOrBlank()) {
////                    Toast.makeText(this@RegisterActivity, "Please enter email", Toast.LENGTH_LONG).show()
////                    return@setOnClickListener
////                }
//            else if (inputMobile.text.toString().isNullOrBlank()) {
//                Toast.makeText(appCompatActivity, "Please enter Mobile Number", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            } else if (input_password.text.toString().isNullOrBlank()) {
//                Toast.makeText(appCompatActivity, "Please enter password", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            } else if (!ccp.isValidFullNumber) {
//                Toast.makeText(appCompatActivity, "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }else if (checkPassword(input_password.text.toString(), input_confirm_password.text.toString())) {
//                 countryCode = ccp.selectedCountryCode
//                val map: MutableMap<String, String> = HashMap()
//                map["email"] = input_email.text.toString()
//                map["password"] = input_password.text.toString()
//                map["name"] = input_first_name.text.toString()
//                map["country_code"] = countryCode
//                map["mobile"] = inputMobile.text.toString()
//                doRegister(map)
//            } else {
//                Toast.makeText(appCompatActivity, "Password and Confirm Password don't match", Toast.LENGTH_LONG).show()
//            }
//
//        }
//
//    }

}
