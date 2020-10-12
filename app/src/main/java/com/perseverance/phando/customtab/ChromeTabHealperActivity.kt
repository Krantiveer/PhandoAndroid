package com.perseverance.phando.customtab

import android.os.Bundle
import com.perseverance.phando.BaseScreenTrackingActivity

abstract class ChromeTabHelperActivity : BaseScreenTrackingActivity() {


    private var mCustomTabActivityHelper: CustomTabActivityHelper? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCustomTabActivityHelper = CustomTabActivityHelper()
    }

    override fun onStart() {
        super.onStart()
        mCustomTabActivityHelper?.bindCustomTabsService(this)
    }

    override fun onStop() {
        super.onStop()
        mCustomTabActivityHelper?.unbindCustomTabsService(this)
    }

}
