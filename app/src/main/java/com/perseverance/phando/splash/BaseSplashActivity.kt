package com.perseverance.phando.splash

import android.content.Intent
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*


open class BaseSplashActivity : BaseScreenTrackingActivity() {
    override var screenName = BaseConstants.SPLASH_SCREEN
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //version?.text = "${BuildConfig.VERSION_CODE}-${BuildConfig.VERSION_NAME}(${BuildConfig.BUILD_TYPE})"
        intent.data?.let {
            imageView.visibility = View.VISIBLE
            checkForDynamicLink()
        } ?: run {
            val animation = AlphaAnimation(0.0f, 1.0f)
            animation.duration = 2000
            animation.startOffset = 10
            animation.fillAfter = true
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    imageView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animation) {
                    openHome()
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            imageView.startAnimation(animation)
        }
    }


    private fun openHome() {
        val intent = Intent(this@BaseSplashActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }

    fun checkForDynamicLink() {
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(this) {
        }
        Firebase.dynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(this) {
                    it?.let {
                        it.link?.let {
                            if (Utils.isNetworkAvailable(this@BaseSplashActivity)) {
                                val sanitizer = UrlQuerySanitizer()
                                sanitizer.allowUnregisteredParamaters = true
                                sanitizer.parseUrl(it.toString())
                                val screen = sanitizer.getValue("screen")
                                val id = sanitizer.getValue("id")
                                val type = sanitizer.getValue("type")
                                val image = sanitizer.getValue("thumbnail")
                                val trailerId = sanitizer.getValue("trailerid")

                                id?.let {
                                    val video = Video()
                                    video.id = it.toInt()
                                    video.type = type
                                    video.thumbnail = image
                                    when (screen) {
                                        "tvseries" -> {
                                            val intent = Intent(this@BaseSplashActivity, SeriesActivity::class.java)
                                            intent.putExtra(Key.CATEGORY, video)
                                            intent.putExtra("fromDyLink",true)
                                            startActivity(intent)
                                            finish()
                                        }
                                        "player" -> {
                                            startActivity(MediaDetailActivity.getDetailIntent(this@BaseSplashActivity, video, trailerId,true))
                                            finish()
                                        }
                                    }
                                } ?: openHome()
                            } else {
                                openHome()
                            }
                        } ?: openHome()
                    } ?: openHome()
                }
                .addOnFailureListener {
                    openHome()
                }
    }
}
