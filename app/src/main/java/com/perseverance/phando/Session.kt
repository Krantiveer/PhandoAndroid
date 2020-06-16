package com.perseverance.phando

import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.videoplayer.VideoPlayerApplication

//import com.google.android.gms.analytics.GoogleAnalytics
//import com.google.android.gms.analytics.Tracker

/**
 * Created by TrilokiNath on 14-03-2016.
 */
class Session : VideoPlayerApplication() {


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sAnalytics = GoogleAnalytics.getInstance(this).apply {
//            if(BuildConfig.DEBUG) {
//                setDryRun(true)
//            }

        }

    }// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG


    /**
     * Gets the default [Tracker] for this [Application].
     * @return tracker
     */
    @get:Synchronized
    val defaultTracker: Tracker?
        get() {
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            if (sTracker == null) {
                if(BuildConfig.DEBUG) {
                    sTracker = sAnalytics?.newTracker(R.xml.test_global_tracker)
                    sTracker?.enableAutoActivityTracking(true);
                }else{
                    sTracker = sAnalytics?.newTracker(R.xml.global_tracker)
                    sTracker?.enableAutoActivityTracking(true);
                }

            }
            return sTracker
        }

    companion object {
        lateinit var instance: Session
            private set

        private var sAnalytics: GoogleAnalytics? = null
        private var sTracker: Tracker? = null
        var launchHomeAfterLogin=true
    }
}