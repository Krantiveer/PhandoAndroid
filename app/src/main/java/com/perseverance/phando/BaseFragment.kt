package com.perseverance.phando

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.perseverance.phando.ui.WaitingDialog

/**
 * A simple [Fragment] subclass.
 */
open class BaseFragment : Fragment() {

    protected lateinit var appCompatActivity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appCompatActivity = context as AppCompatActivity
    }

}
