package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.data.BaseResponse

class P2PasswordChangedActivity : BaseUserLoginActivity() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_password_changed, container, false)
    }

    override fun onGetOtpSuccess(baseResponse: BaseResponse) {
    }




}
