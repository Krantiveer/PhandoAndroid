package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.data.BaseResponse

class LoginFragment : BaseUserLoginFragment() {
    override var screenName= BaseConstants.LOGIN_SCREEN
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_login, container, false)
    }

    override fun onGetOtpSuccess(baseResponse: BaseResponse) {
    }

    override fun onGetOtpSuccessSocial(baseResponse: BaseResponse) {

    }
}
