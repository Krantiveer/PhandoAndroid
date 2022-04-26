package com.perseverance.phando.home.profile.login

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.R
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.profile.UserProfileViewModel
import com.perseverance.phando.retrofit.Cred
import com.perseverance.phando.retrofit.LoginResponse
import com.perseverance.phando.utils.PreferencesUtils


open abstract class BaseUserLoginFragment : BaseSocialLoginFragment() {

    abstract fun onGetOtpSuccess(baseResponse: BaseResponse)
    private var linkMobile: ImageView? = null
    private var linkEmail: ImageView? = null
    private var linkGmail: ImageView? = null
    private var linkFacebook: ImageView? = null
    private var mobileLogin: Button? = null
    private var signup: Button? = null
    private var forgotPassword: TextView? = null

    protected val userProfileViewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }
    private var doubleBackToExitPressedOnce = false
    val loginObserver = Observer<DataLoadingStatus<LoginResponse>> {
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
                loginResponse?.let {
                    onLoginSuccess(it)
                }


            }

        }

    }
    val getOtpObserver = Observer<DataLoadingStatus<BaseResponse>> {
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
                loginResponse?.let {
                    toast(it.message, Toast.LENGTH_LONG)
                    onGetOtpSuccess(loginResponse)
                }


            }

        }

    }

    val socialLoginRegisterObserver = Observer<DataLoadingStatus<LoginResponse>> {

        dismissProgress()

        when (it?.status) {
            LoadingStatus.LOADING -> {
                showProgress()
            }
            LoadingStatus.ERROR -> {
                it.message?.let {
                    Toast.makeText(appCompatActivity, it, Toast.LENGTH_LONG).show()
                }
            }
            LoadingStatus.SUCCESS -> {
                var loginResponse = it.data
                loginResponse?.accessToken?.let {
                    onSocialUserRegisterSuccess(loginResponse)

                    loginResponse?.message?.let {
                        toast(it, Toast.LENGTH_LONG)
                    }

                } ?: loginResponse?.message?.let {
                    toast(it, Toast.LENGTH_LONG)
                }


            }

        }

    }

    //abstract fun onLoginSuccess(loginResponse: LoginResponse)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileViewModel.loginUserData.observe(this, loginObserver)
        userProfileViewModel.getOTPTData.observe(this, getOtpObserver)
        userProfileViewModel.socialLoginUserData.observe(this, socialLoginRegisterObserver)
        initFacebook()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linkMobile = view.findViewById<ImageView>(R.id.linkMobile)
        linkEmail = view.findViewById<ImageView>(R.id.linkEmail)
        linkGmail = view.findViewById<ImageView>(R.id.linkGmail)
        linkFacebook = view.findViewById<ImageView>(R.id.linkFacebook)
        mobileLogin = view.findViewById<Button>(R.id.mobileLogin)
        signup = view.findViewById<Button>(R.id.signup)
        forgotPassword = view.findViewById<TextView>(R.id.forgotPassword)

        linkMobile?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2LoginWithMobileNoActivity, null, navOption)
        }
        linkEmail?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2LoginWithEmailActivity, null, navOption)
        }
        linkGmail?.setOnClickListener {
            gmailSignIn()
        }
        linkFacebook?.setOnClickListener {
            facebookSignIn()
        }
        mobileLogin?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2LoginWithMobileNoActivity, null, navOption)
        }
        signup?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2SignupActivity, null, navOption)
        }
        forgotPassword?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2ForgotPasswordActivity, null, navOption)
        }
    }

    fun doLogin(email: String, password: String) {
        val cred = Cred(email, password)
        userProfileViewModel.loginUser(cred)
    }


    fun getOtp(map: Map<String, String>) {

        userProfileViewModel.getOTP(map)
    }

    fun verifyOtp(map: Map<String, String>) {
        userProfileViewModel.verifyOTP(map)

    }

    fun doSocialLogin(socialLoggedInUser: SocialLoggedInUser) {
        userProfileViewModel.socialLoginUser(socialLoggedInUser)
    }

    fun onSocialUserRegisterSuccess(loginResponse: LoginResponse) {
        onLoginSuccess(loginResponse)
    }

    fun onLoginSuccess(loginResponse: LoginResponse) {
        PreferencesUtils.setLoggedIn(loginResponse.accessToken)
        if (requireActivity().intent.hasExtra("login_error")) {
            startActivity(Intent(requireContext(), HomeActivity::class.java))
        }
        appCompatActivity.setResult(RESULT_OK)
        appCompatActivity.finish()
    }

    override fun onSocialUserLoginSuccess(loggedInUser: SocialLoggedInUser) {
        doSocialLogin(loggedInUser)
    }

}