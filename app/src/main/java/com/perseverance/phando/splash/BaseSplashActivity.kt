package com.perseverance.phando.splash

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


open class BaseSplashActivity : BaseScreenTrackingActivity() {
    override var screenName = BaseConstants.SPLASH_SCREEN
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //version?.text = "${BuildConfig.VERSION_CODE}-${BuildConfig.VERSION_NAME}(${BuildConfig.BUILD_TYPE})"
        printHashKey(this)
//        checkForDynamicLink()
//        val path = "android.resource://" + packageName + "/" + R.raw.mitwa
//        imageView.setVideoURI(Uri.parse(path))
//        imageView.start()
//
//
//        imageView.setOnCompletionListener { checkForDynamicLink() }

//        Glide.with(this).asGif().load(AppCompatResources.getDrawable(this, R.drawable.logo_gif)!!)
        Glide.with(this).asGif()
            .load("https://firebasestorage.googleapis.com/v0/b/candorott-677c7.appspot.com/o/logo-new.gif?alt=media&token=1ce6db94-4bfb-460d-bed3-ca25d1692af4")
            .listener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {

                    return false;
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    resource!!.setLoopCount(1)
                    resource.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable) {
                            checkForDynamicLink()
                        }
                    })
                    return false
                }

            }).into(imageView)

//        intent.data?.let {
//            imageView.visibility = View.VISIBLE
//            checkForDynamicLink()
//        } ?: run {
//            val animation = AlphaAnimation(0.0f, 1.0f)
//            animation.duration = 2000
//            animation.startOffset = 10
//            animation.fillAfter = true
//            animation.setAnimationListener(object : Animation.AnimationListener {
//                override fun onAnimationStart(animation: Animation) {
//                    imageView.visibility = View.VISIBLE
//                }
//
//                override fun onAnimationEnd(animation: Animation) {
//                    checkForDynamicLink()
//                }
//
//                override fun onAnimationRepeat(animation: Animation) {
//
//                }
//            })
//            imageView.startAnimation(animation)
//        }
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
