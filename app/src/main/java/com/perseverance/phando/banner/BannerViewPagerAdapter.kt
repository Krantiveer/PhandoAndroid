package com.perseverance.phando.banner

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.perseverance.phando.db.Video

/**
 * Created by QAIT\TrilokiNathon 14/3/18.
 */

class BannerViewPagerAdapter(private var banners: List<Video>, fm: FragmentManager) : FragmentStatePagerAdapter(fm,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return BannerFragment.newInstance(banners[position])
    }

    override fun getCount(): Int {
        return banners.size
    }
}
