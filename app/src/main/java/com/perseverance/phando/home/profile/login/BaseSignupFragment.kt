package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.data.BaseResponse
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.LoginResponse


open abstract class BaseSignupFragment : BaseUserLoginFragment() {
    abstract fun onRegisterSuccess(loginResponse: LoginResponse)

    val registerUserObserver = Observer<DataLoadingStatus<LoginResponse>> {

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
                    onRegisterSuccess(loginResponse)

                    loginResponse?.message?.let {
                        toast(it, Toast.LENGTH_LONG)
                    }

                } ?: loginResponse?.message?.let {
                    toast(it, Toast.LENGTH_LONG)
                }


            }

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileViewModel.registerUserData.observe(this, registerUserObserver)

    }


    override fun onGetOtpSuccess(baseResponse: BaseResponse) {
    }


    override fun onSocialUserLoginSuccess(loggedInUser: SocialLoggedInUser) {
        doSocialLogin(loggedInUser)
    }

    fun checkPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }


    fun doRegister(map: Map<String, String>) {
        userProfileViewModel.registerUser(map)

    }
}