package com.perseverance.phando.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.utils.AppSignatureHelper
import kotlinx.android.synthetic.main.activity_splash.*


open class BaseSplashActivity : BaseScreenTrackingActivity() {
    override var screenName = BaseConstants.SPLASH_SCREEN
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("start_home", false)) {

        }
        setContentView(R.layout.activity_splash)
        //version?.text = "${BuildConfig.VERSION_CODE}-${BuildConfig.VERSION_NAME}(${BuildConfig.BUILD_TYPE})"
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 2000
        animation.startOffset = 10
        animation.fillAfter = true
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                imageView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                Handler().postDelayed({
                    if (!isFinishing) {
                        launch()
                    }
                }, 500)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        imageView.startAnimation(animation)


    }


    private fun launch() {
        intent = Intent(this@BaseSplashActivity, HomeActivity::class.java)
        try {
            val extras = getIntent().extras
            if (extras != null) {
                val isNotification = extras.getString("is_notification")
                if (!TextUtils.isEmpty(isNotification)) {
                    intent.putExtra("is_notification", isNotification)
                }
            }
        } catch (e: Exception) {
        }

        startActivity(intent)
        finish()
    }

}
