package com.perseverance.phando

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.utils.TrackingUtils

open abstract class BaseScreenTrackingActivity : AppCompatActivity() {

    abstract var screenName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (screenName.isNotBlank()) TrackingUtils.sendScreenTracker(screenName)
    }
}