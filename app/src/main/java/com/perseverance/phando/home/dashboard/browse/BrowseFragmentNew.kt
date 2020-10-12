package com.perseverance.phando.home.dashboard.browse

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.utils.Util
import kotlinx.android.synthetic.main.fragment_browse_new.*

/**
 * A simple [Fragment] subclass.
 */
class BrowseFragmentNew : BaseBrowseFragmentNew() {

    override var screenName = BaseConstants.HOME_BROWSE_SCREEN
    override fun setBannerSlider(browseDataList: List<BrowseData>, browseData: BrowseData) {
        var width = 0
        if (browseData.displayType == "TOP_BANNER") {
            homeHeaderView?.setData(browseData.list, tabLayout, childFragmentManager)
            (browseDataList as ArrayList).removeAt(0)
            homeHeaderView.visible()
            width = Util.getScreenHeightForHomeBanner(this.requireContext())
            width = (width * .75).toInt()
            tabLayout.visible()

        } else {
            homeHeaderView.gone()
            tabLayout.gone()
        }
        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, width, 0, 0)
        topBannerMetaDataContainer.setLayoutParams(params)
    }

    override fun setTopBannserHeight() {


    }
}
