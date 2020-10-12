package com.perseverance.phando.banner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.fragment.app.Fragment
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.fragment_banner_list.*
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*

abstract class BaseBannerFragment : BaseFragment() {

    private var banner: Video? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            banner = requireArguments().getParcelable(ARG_BANNER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_banner_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        banner?.let {
            if (it.is_free == 1) { // if paid video then show premium icon
                free.gone()
            } else {
                free.visible()
            }
        }
        Utils.displayImage(activity, banner?.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.video_placeholder, imgThumbnail)

        imgThumbnail.setOnClickListener {

            banner?.id?.let {
                if (Utils.isNetworkAvailable(activity)) {
                    if ("T".equals(banner!!.type)) {
                        val intent = Intent(activity, SeriesActivity::class.java)
                        intent.putExtra(Key.CATEGORY, banner)
                        startActivity(intent)
                    } else {
                        startActivity(MediaDetailActivity.getDetailIntent(activity as Context, banner!!))
                        Utils.animateActivity(activity, "next")
                    }
                    /*startActivity(MediaDetailActivity.getDetailIntent(activity as Context, baseVideo))
                    Utils.animateActivity(activity, "next")*/
                } else {
                    DialogUtils.showMessage(activity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                }
            }


        }


    }

    companion object {

        val ARG_BANNER = "param_banner"

    }


}// Required empty public constructor
