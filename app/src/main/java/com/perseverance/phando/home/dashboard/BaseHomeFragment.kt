package com.perseverance.phando.home.dashboard

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
open class BaseHomeFragment : Fragment() {
    protected lateinit var appCompatActivity: AppCompatActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appCompatActivity = context as AppCompatActivity

    }


}
