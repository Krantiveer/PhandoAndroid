package com.perseverance.phando.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.R
import com.perseverance.phando.customtab.CustomTabActivityHelper
import com.perseverance.phando.customtab.WebviewActivity
import com.perseverance.phando.customtab.WebviewFallback
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class Util {
    private val SHOW_LOG = true
    private val TAG = "Util"

    companion object {
        private var mProgressDialog: ProgressDialog? = null

        fun showDialog(context: Context, message: String) {
            val alertDialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme).create()
            alertDialog.setIcon(R.mipmap.ic_launcher)
            alertDialog.setTitle(R.string.app_name)
            alertDialog.setMessage(message)
            /*alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });*/

            alertDialog.show()

        }


        fun showToast(activity: AppCompatActivity, message: String) {
            val ssb = SpannableStringBuilder()
                    .append(message)
            ssb.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0,
                    message.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            Snackbar.make(activity.findViewById(android.R.id.content), ssb, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

        }


        fun showLoader(argContext: Context?,
                       argMessage: String) {

            dismissLoader()
            try {
                if (argContext != null) {
                    mProgressDialog = ProgressDialog.show(argContext, null, "" + argMessage, true, false)
                    mProgressDialog!!.show()
                }
            } catch (ex: Exception) {
                // Crashlytics.logException(ex);
                MyLog.e("Util", "showLoader: Exception Occured : " + ex.message)
            }

        }

        /**
         * Dismisses open (if any) progress dialog box.
         */
        fun dismissLoader() {
            try {
                if (mProgressDialog != null && mProgressDialog!!.isShowing) {
                    mProgressDialog!!.dismiss()
                }
            } catch (ex: Exception) {
                // Crashlytics.logException(ex);
                MyLog.e("Util", "dismissLoader: Exception Occured : " + ex.message)
            }

        }


        fun hideKeyBoard(activity: Activity) {
            if (activity.currentFocus != null) {
                val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }


        fun getScreenWidthForVideo(context: Context): Int {
            val width = context.resources.displayMetrics.widthPixels
            return width
        }

        fun getScreenHeightForVideo(context: Context): Int {
            val height = getScreenWidthForVideo(context)
            return (height * 0.58).toInt()
        }

        fun getScreenWidthForHomeBanner(context: Context): Int {
            val width = context.resources.displayMetrics.widthPixels
            return width
        }

        fun getScreenHeightForHomeBanner(context: Context): Int {
            val height = getScreenWidthForVideo(context)
            return (height * 0.56).toInt()
        }

        fun getScreenHeight(context: Context): Int {
            val height = context.resources.displayMetrics.heightPixels
            return height
        }

        fun getScreenWidth(context: Context): Int {
            val width = context.resources.displayMetrics.widthPixels
            return width
        }


        fun isNetworkAvailable(activity: AppCompatActivity?): Boolean {
            val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun isNetworkAvailable(activity: Context?): Boolean {
            val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }


        fun setTintDrawable(drawable: Drawable?, @ColorInt color: Int) {
            if (drawable != null) {
                drawable.clearColorFilter()
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawable.invalidateSelf()
                val wrapDrawable = DrawableCompat.wrap(drawable).mutate()
                DrawableCompat.setTint(wrapDrawable, color)
            }
        }

        fun getISO8601StringForDate(): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return dateFormat.format(Date())
        }

        fun addClickablePartTextViewResizable(strSpanned: Spanned, tv: TextView,
                                              maxLine: Int, spanableText: String, viewMore: Boolean): SpannableStringBuilder {
            val str = strSpanned.toString()
            val ssb = SpannableStringBuilder(strSpanned)

            if (str.contains(spanableText)) {
                ssb.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
//                    tv.layoutParams = tv.getLayoutParams();
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        if (viewMore) {
                            makeTextViewResizable(tv, 1, "View Less", false)
                        } else {
                            makeTextViewResizable(tv, 2, "View More", true)
                        }
                    }

                }, str.indexOf(spanableText), (str.indexOf(spanableText) + spanableText.length), 0)

            }
            return ssb

        }

        private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
        private fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
            if (tv.tag == null) {
                tv.tag = tv.text
            }
            val vto = tv.viewTreeObserver
            var lineEndIndex: Int = 0
            var text: String = ""
            globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                tv.viewTreeObserver.removeGlobalOnLayoutListener(globalLayoutListener)
                when {
                    maxLine == 0 -> {
                        lineEndIndex = tv.layout.getLineEnd(0)
                        text = "${tv.text.subSequence(0, (lineEndIndex - expandText.length + 1))}  $expandText"
                    }
                    maxLine > 0 && tv.lineCount >= maxLine -> {
                        lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                        text = "${tv.text.subSequence(0, (lineEndIndex - expandText.length + 1))}  $expandText"
                    }
                    else -> {
                        lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                        text = "${tv.text.subSequence(0, lineEndIndex)}  $expandText"
                    }
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                        addClickablePartTextViewResizable(Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText,
                                viewMore), TextView.BufferType.SPANNABLE)
            }
            vto.addOnGlobalLayoutListener(globalLayoutListener)
        }


        fun openWebview(activity: Activity?, url: String?) {
            if (BuildConfig.APPLICATION_ID == "com.perseverance.anvitonmovies") {
                val intent = Intent(activity, WebviewActivity::class.java)
                intent.putExtra("url", url)
                activity?.startActivity(intent)
            } else {
                val intentBuilder = CustomTabsIntent.Builder()
                val color = ContextCompat.getColor(activity as Context, R.color.black)
                val secondaryColor = ContextCompat.getColor(activity as Context, R.color.white)
                intentBuilder.setToolbarColor(color)
                intentBuilder.setSecondaryToolbarColor(secondaryColor)
                intentBuilder.setShowTitle(true)
                intentBuilder.enableUrlBarHiding()
                val bitmap = getBitmapFromVectorDrawable(activity)
                bitmap.let { intentBuilder.setCloseButtonIcon(it) }
                CustomTabActivityHelper.openCustomTab(
                        activity, intentBuilder.build(), Uri.parse(url?.trim()), WebviewFallback())
            }


        }

        private fun getBitmapFromVectorDrawable(context: Context): Bitmap {
            var drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_black_24dp)
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

        fun hideSystemUI(@NonNull activity: Activity, isLandscape: Boolean = true) {
            val decorView = activity.window.decorView
            var uiState = (View.SYSTEM_UI_FLAG_VISIBLE)
            if (isLandscape) {
                uiState = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_IMMERSIVE
                        )
            }
            decorView.systemUiVisibility = uiState
        }
    }


}
