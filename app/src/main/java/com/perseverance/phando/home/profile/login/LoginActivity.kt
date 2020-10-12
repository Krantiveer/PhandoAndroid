package com.perseverance.phando.home.profile.login

import android.os.Bundle
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.navigation.findNavController
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.TrackingUtils


class LoginActivity : BaseScreenTrackingActivity() {

    override var screenName=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findNavController(R.id.nav_host_fragment)
        val msg = intent.getStringExtra("msg")
        msg?.let {
            DialogUtils.showDialog(this, "Error!", it, "Close", null, object : AppDialogListener {
                override fun onNegativeButtonPressed() {

                }

                override fun onPositiveButtonPressed() {

                }

            })
        }
    }

    companion object {
        const val REQUEST_CODE_LOGIN = 101
    }

}
