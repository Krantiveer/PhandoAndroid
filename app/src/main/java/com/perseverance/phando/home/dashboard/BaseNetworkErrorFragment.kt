package com.perseverance.phando.home.dashboard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseNetworkErrorFragment : Fragment() {

    protected var activity: AppCompatActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    fun showProgress(message: String) {

    }

    fun dismissProgress() {

    }
}
