package com.perseverance.phando.home.profile.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import com.perseverance.phando.BaseLoginFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import kotlinx.android.synthetic.main.activity_p2_password_changed.*

class PasswordChangedFragment : BaseLoginFragment() {
    override var screenName= BaseConstants.CHANGE_PASSWORD_SCREEN
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_p2_password_changed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mobileLogin?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2LoginWithMobileNoActivity, null, navOption)
        }
        linkEmail?.setOnClickListener {
            val navOption = NavOptions.Builder().setPopUpTo(R.id.P2LoginActivity, false).build()
            navigator.navigate(R.id.P2LoginWithEmailActivity, null, navOption)
        }
    }

}
