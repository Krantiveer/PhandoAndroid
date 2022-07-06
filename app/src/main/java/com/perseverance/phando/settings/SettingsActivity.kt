package com.perseverance.phando.settings

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.Constants
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.home.dashboard.viewmodel.DashboardViewModel
import com.perseverance.phando.utils.PreferencesUtils
import com.perseverance.phando.utils.Util
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.layout_header_new.*

class SettingsActivity : BaseScreenTrackingActivity() {
    override var screenName = BaseConstants.SETTINGS_SCREEN
    private var bitmap: Bitmap? = null
    private val homeActivityViewModel by lazy {
        ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    val menuOnClickListener = View.OnClickListener {
        when (it.id) {
            help.id -> openWebview(Constants.URL_HELP)
            tc.id -> openWebview(Constants.URL_TC)
            privacyPolicy.id -> openWebview(Constants.URL_PRIVACY_POLICY)
            aboutus.id -> openWebview(Constants.URL_ABOUT_US)
            btnRate.id -> rateApplication()
            btnShare.id -> shareApplication()
            cvNotificationSettings.id -> {
                startActivity(Intent(this,
                    NotificationsSettingsActivity::class.java))
            }
            cvParentalControl.id -> {
                startActivity(Intent(this,
                    ParentalControlActivity::class.java))
            }

        }
    }

    private fun initClick() {
        help?.setOnClickListener(menuOnClickListener)
        tc?.setOnClickListener(menuOnClickListener)
        privacyPolicy?.setOnClickListener(menuOnClickListener)
        aboutus?.setOnClickListener(menuOnClickListener)
        btnRate?.setOnClickListener(menuOnClickListener)
        btnShare?.setOnClickListener(menuOnClickListener)
        cvNotificationSettings?.setOnClickListener(menuOnClickListener)
        cvParentalControl?.setOnClickListener(menuOnClickListener)
    }

    override fun onResume() {
        super.onResume()
        val token = PreferencesUtils.getLoggedStatus()
        if (token.isEmpty()) {
            cvNotificationSettings.gone()
            cvParentalControl.gone()
        } else {
            cvNotificationSettings.visible()
            cvParentalControl.visible()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        txtTitle.text = "Settings"

        imgBack.setOnClickListener {
            finish()
        }
        initClick()
        Util.hideKeyBoard(this)

    }

    private fun rateApplication() {
        try {
            val rateIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${this?.packageName}"))
            startActivity(rateIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.showErrorToast(this,
                "Play Store app is not installed in your device.",
                Toast.LENGTH_SHORT)
        }

    }

    private fun shareApplication() {
        try {
            val shareBody = "https://play.google.com/store/apps/details?id=${packageName}"
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share using..."))
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.showErrorToast(this,
                "Sharing app is not installed in your device.",
                Toast.LENGTH_SHORT)
        }

    }


    data class SettingItems(val id: String, val diaplayName: String)

    private fun openWebview(url: String?) {
        Util.openWebview(this, url)


//        val intentBuilder = CustomTabsIntent.Builder()
//        val color = ContextCompat.getColor(activity as Context, R.color.black)
//        val secondaryColor = ContextCompat.getColor(activity as Context, R.color.white)
//        intentBuilder.setToolbarColor(color)
//        intentBuilder.setSecondaryToolbarColor(secondaryColor)
//        intentBuilder.setShowTitle(true)
//        intentBuilder.enableUrlBarHiding()
//        bitmap?.let { intentBuilder.setCloseButtonIcon(it) }
//        CustomTabActivityHelper.openCustomTab(
//                activity, intentBuilder.build(), Uri.parse(url?.trim()), WebviewFallback())
    }


    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

}