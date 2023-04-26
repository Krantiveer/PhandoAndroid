package com.perseverance.phando

import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.perseverance.phando.utils.MyLog
import com.videoplayer.VideoPlayerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by TrilokiNath on 14-03-2016.
 */
class Session : VideoPlayerApplication() {
    lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private suspend fun getRemoteIp() {
        remoteIp = ""

        /* try {
                ""
    //            getPublicIpAddress()
            } catch (e: Exception) {
                ""
            }*/
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CoroutineScope(Dispatchers.IO).launch { getRemoteIp() }
        firebaseAnalytics = Firebase.analytics
        sAnalytics = GoogleAnalytics.getInstance(this).apply {
//            if(BuildConfig.DEBUG) {
//                setDryRun(true)
//            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.TOPIC).addOnCompleteListener {
            if (it.isSuccessful) {
                if (BuildConfig.DEBUG) MyLog.e("@@topic ${BuildConfig.TOPIC} subscribed")
            } else {
                if (BuildConfig.DEBUG) MyLog.e("topic ${BuildConfig.TOPIC} subscribtion error")
            }
        }

    }// To enable debug logging use: adb shell setprop MyLog.tag.GAv4 DEBUG


    /**
     * Gets the default [Tracker] for this [Application].
     * @return tracker
     */
    @get:Synchronized
    val defaultTracker: Tracker?
        get() {
            // To enable debug logging use: adb shell setprop MyLog.tag.GAv4 DEBUG
            if (sTracker == null) {
                if (BuildConfig.DEBUG) {
                    sTracker = sAnalytics?.newTracker(R.xml.test_global_tracker)
                    sTracker?.enableAutoActivityTracking(false);
                } else {
                    sTracker = sAnalytics?.newTracker(R.xml.global_tracker)
                    sTracker?.enableAutoActivityTracking(false);
                }

            }
            return sTracker
        }

    companion object {
        lateinit var instance: Session
            private set

        private var sAnalytics: GoogleAnalytics? = null
        private var sTracker: Tracker? = null
        var launchHomeAfterLogin = true
        var remoteIp: String = ""
    }
}