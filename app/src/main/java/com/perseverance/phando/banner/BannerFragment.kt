package com.perseverance.phando.banner

import android.os.Bundle
import com.perseverance.phando.db.Video

class BannerFragment : BaseBannerFragment() {
    companion object {
        fun newInstance(banner: Video): BannerFragment {
            val fragment = BannerFragment()
            val args = Bundle()
            args.putSerializable(ARG_BANNER, banner)
            fragment.arguments = args
            return fragment
        }
    }
}
