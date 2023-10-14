package com.perseverance.phando.splash

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.notification.NotificationData
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


open class BaseSplashActivity : BaseScreenTrackingActivity() {
    override var screenName = BaseConstants.SPLASH_SCREEN
    var isFromNotification = true



   /* override fun onStart() {
        super.onStart()
        val bundle = intent.extras
        if (bundle != null && isFromNotification == true) {
            val id = bundle.getString("id")

            if (id != null) {

                val title = bundle.getString("title")
                Log.e("@@id", id.toString())
                val type = bundle.getString("type")
                val is_free = bundle.getString("is_free")
                val thumbnail = bundle.getString("thumbnail")
                val rating = bundle.getString("rating")
                val details = bundle.getString("detail")

                if (!("T".equals(type) || "M".equals(type))) {
                    return
                }

                val baseVideo = Video()
                baseVideo.id = id.toInt()
                baseVideo.thumbnail = thumbnail.toString()
                baseVideo.title = title
                baseVideo.type = type
                baseVideo.description = details.toString()
                baseVideo.rating = rating!!.toInt()
                baseVideo.is_free = is_free!!.toInt()

                val notificationModel = NotificationData()
                val dbId = System.currentTimeMillis()
                notificationModel.dbID = dbId
                notificationModel.id = id.toInt()
                notificationModel.thumbnail = thumbnail
                notificationModel.title = title
                notificationModel.type = type
                notificationModel.description = details
                notificationModel.rating = rating.toInt()
                notificationModel.free = is_free.toInt()

                AppDatabase.getInstance(this).notificationDao().insert(notificationModel)

                isFromNotification = false
                var intent: Intent? = null
                if ("T".equals(baseVideo.type)) {
                    intent = Intent(this, SeriesActivity::class.java)
                    intent.putExtra(Key.CATEGORY, baseVideo)
                } else {
                    intent = MediaDetailActivity.getDetailIntent(this, baseVideo)
                }
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            } else {


                checkForDynamicLink()

            }
        } else {
            checkForDynamicLink()

        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        printHashKey(this)


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
                    checkForDynamicLink()
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

    private fun checkForDynamicLink() {
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
                                        val intent = Intent(this@BaseSplashActivity,
                                            SeriesActivity::class.java)
                                        intent.putExtra(Key.CATEGORY, video)
                                        intent.putExtra("fromDyLink", true)
                                        startActivity(intent)
                                        finish()
                                    }
                                    "player" -> {
                                        startActivity(MediaDetailActivity.getDetailIntent(this@BaseSplashActivity,
                                            video,
                                            trailerId,
                                            true))
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
            .addOnFailureListener { e ->
                openHome()
            }
    }

    open fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.getPackageManager()
                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }

}
