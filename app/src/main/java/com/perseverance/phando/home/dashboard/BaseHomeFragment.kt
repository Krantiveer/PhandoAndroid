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
    private lateinit var activity: AppCompatActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        imgHeaderImage.setOnClickListener {
//            activity.onBackPressed()
//        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity

    }


}
