package com.perseverance.phando.home.videolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.BaseNetworkErrorFragment
import kotlinx.android.synthetic.main.fragment_base.*

class NetworkErrorFragment : BaseNetworkErrorFragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_base, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lbl_no_video_base.text = String.format(BaseConstants.RETRY_LABEL, BaseConstants.CONNECTION_ERROR)
        lbl_no_video_base.visibility = View.VISIBLE
        recycler_view_base.visibility = View.GONE
        lbl_no_video_base.setOnClickListener {

        }
    }

    companion object {

        fun newInstance(): NetworkErrorFragment {
            val fragment = NetworkErrorFragment()

            return fragment
        }
    }

}
