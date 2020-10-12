package com.perseverance.phando

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.fragment.app.Fragment
import com.perseverance.phando.ui.WaitingDialog
import com.perseverance.phando.utils.TrackingUtils

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    abstract var screenName: String
    protected lateinit var appCompatActivity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appCompatActivity = context as AppCompatActivity
        if (screenName.isNotBlank()) TrackingUtils.sendScreenTracker(screenName)
    }

}
