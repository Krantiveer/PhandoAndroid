package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.perseverance.patrikanews.utils.gone
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.retrofit.Cred
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_p2_login_with_email.*
import kotlinx.android.synthetic.main.login_link_container.*

class LoginWithEmailFragment : BaseUserLoginFragment() {
    override var screenName= BaseConstants.LOGIN_WITH_EMAIL_SCREEN

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_login_with_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linkEmail.gone()
        login.setOnClickListener {
            Utils.hideKeyboard(appCompatActivity)
            if (inputEmail.text.toString().isNullOrBlank()) {
                Toast.makeText(appCompatActivity, "Please enter email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (inputPassword.text.toString().isNullOrBlank()) {
                Toast.makeText(appCompatActivity, "Please enter password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val cred: Cred = Cred(inputEmail.text.toString(), inputPassword.text.toString())
                userProfileViewModel.loginUser(cred)
            }

        }
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_p2_login_with_email)
//        TrackingUtils.sendScreenTracker(BaseConstants.LOGIN_WITH_EMAIL)
//        linkEmail.gone()
//        login.setOnClickListener {
//            Utils.hideKeyboard(this@P2LoginWithEmailActivity)
//            if (inputEmail.text.toString().isNullOrBlank()) {
//                Toast.makeText(this@P2LoginWithEmailActivity, "Please enter email", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            } else if (inputPassword.text.toString().isNullOrBlank()) {
//                Toast.makeText(this@P2LoginWithEmailActivity, "Please enter password", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            } else {
//                val cred: Cred = Cred(inputEmail.text.toString(), inputPassword.text.toString())
//                userProfileViewModel.loginUser(cred)
//            }
//
//        }
//    }

    override fun onGetOtpSuccess(baseResponse: BaseResponse) {
    }

}
