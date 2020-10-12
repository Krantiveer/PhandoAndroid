package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.BaseLoginFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_p2_set_paasword.*

class SetPasswordFragment : BaseLoginFragment() {

    override var screenName= BaseConstants.SET_PASSWORD_SCREEN
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_set_paasword, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mobileNo = arguments?.getString("MOBILE")!!
        val countryCode = arguments?.getString("COUNTRY_CODE")!!
        updatePassword.setOnClickListener {
            val password = password1.text.toString()
            val confirmPassword = password2.text.toString()
            Utils.hideKeyboard(appCompatActivity)
            if (password.isNullOrBlank()) {
                toast("Please enter password")
                return@setOnClickListener
            }
            if (confirmPassword.isNullOrBlank()) {
                toast("Please enter confirm password")
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                toast("Password does not match")
                return@setOnClickListener
            }
            if (password.length < 6) {
                toast("Password length must be 6 or above")
                return@setOnClickListener
            }
            val bundle = bundleOf(
                    "MOBILE" to mobileNo,
                    "COUNTRY_CODE" to countryCode,
                    "PASSWORD" to password

            )
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2SetPasswordActivity, false).build()
            navigator.navigate(R.id.P2ChangePasswordOTPVerificationActivity, bundle, navOption)

//            val intent = Intent(this@P2SetPasswordActivity, P2ChangePasswordOTPVerificationActivity::class.java)
//            intent.putExtra("MOBILE", mobileNo)
//            intent.putExtra("COUNTRY_CODE", countryCode)
//            intent.putExtra("PASSWORD", password)
//            startActivity(intent)
        }
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_p2_set_paasword)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//
//    }

//    fun updatePassword(view: View) {
//        val password = password1.text.toString()
//        val confirmPassword = password2.text.toString()
//        Utils.hideKeyboard(appCompatActivity)
//        if (password.isNullOrBlank()) {
//            toast("Please enter password")
//            return
//        }
//        if (confirmPassword.isNullOrBlank()) {
//            toast("Please enter confirm password")
//            return
//        }
//        if (password != confirmPassword) {
//            toast("Password does not match")
//            return
//        }
//        if (password.length<6) {
//            toast("Password length must be 6 or above")
//            return
//        }
//        val countryCode = intent.getStringExtra("COUNTRY_CODE")
//        val mobileNo = intent.getStringExtra("MOBILE")
//        val intent = Intent(this@P2SetPasswordActivity, P2ChangePasswordOTPVerificationActivity::class.java)
//        intent.putExtra("MOBILE", mobileNo)
//        intent.putExtra("COUNTRY_CODE", countryCode)
//        intent.putExtra("PASSWORD", password)
//        startActivity(intent)
//    }


}
