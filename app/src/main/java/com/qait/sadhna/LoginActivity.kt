package com.qait.sadhna

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.TrackingUtils


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findNavController(R.id.nav_host_fragment)
        TrackingUtils.sendScreenTracker(BaseConstants.LOGIN)
        val msg = intent.getStringExtra("msg")
        msg?.let {
            DialogUtils.showDialog(this,"Error!",it,"Close",null,object : AppDialogListener {
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
