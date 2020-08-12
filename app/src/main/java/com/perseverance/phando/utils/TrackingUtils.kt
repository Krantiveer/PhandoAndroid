package com.perseverance.phando.utils

import android.util.Log
import com.google.android.gms.analytics.HitBuilders
import com.perseverance.phando.Session


/**
 * Created by TrilokiNath on 08-11-2018.
 */
object TrackingUtils {
    private val gaTracker by lazy {
        Session.instance?.defaultTracker
    }

    fun sendScreenTracker(screenName: String, title: String? = null) {
        gaTracker?.setScreenName(screenName)
        val screenViewBuilder = HitBuilders.ScreenViewBuilder()
        title?.let {
            screenViewBuilder.set("&dt", it)
        }
        gaTracker?.send(screenViewBuilder.build())
        MyLog.e("GA tracker sent: $screenName")
    }

    fun sendVideoEvent(label: String?, category: String?, action: String?) {
        MyLog.e("VideoEvent", label + " " + action)
        gaTracker?.send(HitBuilders.EventBuilder()
                .setCategory(if (category.isNullOrEmpty()) "Phando Video" else category)
                .setLabel(label)
                .setAction(action)
                .build())
    }
}