package com.perseverance.phando.banner

import android.os.Bundle
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.Video

class BannerFragment : BaseBannerFragment() {
    override var screenName =""
    companion object {
        fun newInstance(banner: Video): BannerFragment {
            val fragment = BannerFragment()
            val args = Bundle()
            args.putParcelable(ARG_BANNER, banner)
            fragment.arguments = args
            return fragment
        }
    }
}
