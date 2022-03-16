package com.perseverance.phando.home.dashboard.browse

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.invisible
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.banner.BannerViewPagerAdapter
import com.perseverance.phando.banner.FixedSpeedScroller
import com.perseverance.phando.db.Video
import com.perseverance.phando.utils.Util
import kotlinx.android.synthetic.main.item_top_banner.view.*
import java.lang.reflect.Field


/**
 * TODO: document your custom view class.
 */
open class BaseHomeHeaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle) {

    var counter = 0
    var list = emptyList<Video>()


    val runnable: Runnable = object : Runnable {
        override fun run() {
            if (list.isNotEmpty() && list.size > 1) {
                if (list.size === counter) {
                    counter = 0
                } else {
                    counter++
                }
                bannerViewPager.setCurrentItem(counter, if (counter == 0) true else true)

            }
            handler?.postDelayed(this, 3000)
        }

    }

    init {

        View.inflate(context, R.layout.item_top_banner, this)
        val width = Util.getScreenWidthForHomeBanner(context)
        val height = Util.getScreenHeightForHomeBanner(context)
        mainContainer.requestLayout()
        mainContainer.layoutParams.height = height
        mainContainer.layoutParams.width = width
    }

    fun setData(list: List<Video>, tabLayout: TabLayout, childFragmentManager: FragmentManager) {
        this.list = list
        if (list.isNotEmpty()) {
            bannerViewPager.adapter = BannerViewPagerAdapter(list, childFragmentManager)
            bannerViewPager.setClipToPadding(false)
            bannerViewPager.setPadding(80, 50, 80, 50)
            bannerViewPager.pageMargin = 20
            tabLayout.setupWithViewPager(bannerViewPager, true)
            if (list.size < 2) {
                tabLayout.invisible()
            } else {
                tabLayout.visible()
            }

        } else {
            gone()
        }
        bannerViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {


            private var mCurrentPosition = 0
            private var mScrollState = 0


            override fun onPageSelected(position: Int) {
                mCurrentPosition = position
                counter = mCurrentPosition
            }

            override fun onPageScrollStateChanged(state: Int) {
                handleScrollState(state)
                mScrollState = state
            }

            private fun handleScrollState(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    setNextItemIfNeeded()
                }
            }

            private fun setNextItemIfNeeded() {
                if (!isScrollStateSettling()) {
                    handleSetNextItem()
                }
            }

            private fun isScrollStateSettling(): Boolean {
                return mScrollState == ViewPager.SCROLL_STATE_SETTLING
            }

            private fun handleSetNextItem() {
                val lastPosition = bannerViewPager!!.adapter!!.count - 1
                if (mCurrentPosition == 0) {
                    bannerViewPager!!.setCurrentItem(lastPosition, false)
                } else if (mCurrentPosition == lastPosition) {
                    bannerViewPager!!.setCurrentItem(0, false)
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        })
        try {
            val mScroller: Field
            mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.setAccessible(true)
            val scroller = FixedSpeedScroller(bannerViewPager.getContext(), null)
            // scroller.setFixedDuration(5000);
            mScroller.set(bannerViewPager, scroller)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.postDelayed(runnable, 3000)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(runnable)
    }

}
