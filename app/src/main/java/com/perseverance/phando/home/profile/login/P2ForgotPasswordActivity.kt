package com.perseverance.phando.home.profile.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.BaseActivity
import com.perseverance.phando.R
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_p2_forgot_paasword.*

class P2ForgotPasswordActivity : BaseActivity() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_forgot_paasword, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ccp.registerCarrierNumberEditText(inputPhone)
        resetPassword.setOnClickListener {
            Utils.hideKeyboard(appCompatActivity)
            if (inputPhone.text.toString().isNullOrBlank()) {
                toast("Please enter mobile number")
                return@setOnClickListener
            }
            if (!inputPhone.text.toString().isValidPhoneNumber()) {
                toast("Please enter valid mobile number")
                return@setOnClickListener
            }

            if (ccp.isValidFullNumber) {
                val countryCode = ccp.selectedCountryCode
                val bundle = bundleOf(
                        "MOBILE" to inputPhone.text.toString(),
                        "COUNTRY_CODE" to countryCode

                )
                val navOption = NavOptions.Builder().setPopUpTo(R.id.P2ForgotPasswordActivity, false).build()
                navigator.navigate(R.id.P2SetPasswordActivity, bundle, navOption)

//
//                val intent = Intent(appCompatActivity, P2SetPasswordActivity::class.java)
//                intent.putExtra("MOBILE", inputPhone.text.toString())
//                intent.putExtra("COUNTRY_CODE", countryCode)
//                startActivity(intent)
                // startActivity(intent)
            } else {
                toast( "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG)
            }
        }

    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_p2_forgot_paasword)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        TrackingUtils.sendScreenTracker(BaseConstants.FORGOT_PASSWORD)
//        ccp.registerCarrierNumberEditText(inputPhone)
//    }



    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    fun CharSequence?.isValidPhoneNumber() = !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()
    fun resetPassword(view:View){
        Utils.hideKeyboard(appCompatActivity)
        if (inputPhone.text.toString().isNullOrBlank()) {
            toast("Please enter mobile number")
            return
        }
        if (!inputPhone.text.toString().isValidPhoneNumber()) {
            toast("Please enter valid mobile number")
            return
        }

        if (ccp.isValidFullNumber) {
            val countryCode = ccp.selectedCountryCode
            val intent = Intent(appCompatActivity, P2SetPasswordActivity::class.java)
            intent.putExtra("MOBILE", inputPhone.text.toString())
            intent.putExtra("COUNTRY_CODE", countryCode)
            startActivity(intent)
            // startActivity(intent)
        } else {
            toast( "number " + ccp.fullNumber.toString() + " not valid!", Toast.LENGTH_LONG)
        }


    }
}
