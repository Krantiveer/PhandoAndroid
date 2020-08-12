package com.perseverance.patrikanews.utils

import android.content.Context
import android.graphics.PorterDuff
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.perseverance.phando.resize.ThumbnailResizer
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.Utils

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)
fun Fragment.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(requireContext(), colorRes)

/**
 * Easy toast function for Activity.
 */

fun AppCompatActivity.replaceFragment(id: Int, fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    if (!isFinishing) {
        try {
            transaction.replace(id, fragment)
            transaction.commit()
        } catch (e: Exception) {

        }

    }
}

fun AppCompatActivity.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.activity, text, duration).show()
}

fun AppCompatActivity.log(message: String, tag: String = "*AppLog*") {
    MyLog.d(tag, message)
}

fun Fragment.log(message: String, tag: String = "*AppLog*") {
    MyLog.d(tag, message)
}

fun log(message: String, tag: String = "*AppLog*") {
    MyLog.d(tag, message)
}


fun View.onClick(f: () -> Unit) {
    this.setOnClickListener {
        f()
    }

}

/**
 * Inflate the layout specified by [layoutRes].
 */
fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Context.getDrawableCompat(@DrawableRes resId: Int, @ColorRes tintColorRes: Int = 0) = when {
    tintColorRes != 0 -> AppCompatResources.getDrawable(this, resId)?.apply {
        setColorFilter(getColorCompat(tintColorRes), PorterDuff.Mode.SRC_ATOP)
    }
    else -> AppCompatResources.getDrawable(this, resId)
}!!

fun ImageView.loadImage(url: String?) {
    Glide.with(context)
            .load(url)
            .apply(RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(this)
}

fun ImageView.loadRoundedImage(url: String?) {

    /*  Glide.with(context)
              .load(url)
              .apply(RequestOptions()
                      .diskCacheStrategy(DiskCacheStrategy.ALL))
              .into(BitmapImageViewTarget(this))*/


}

/*Glide.with(context).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
    @Override
    protected void setResource(Bitmap resource) {
        RoundedBitmapDrawable circularBitmapDrawable =
        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
        circularBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(circularBitmapDrawable);
    },
});*/

fun View.resizeView(thumbnailResizer: ThumbnailResizer, isConstraintLayout: Boolean = false) {
    val width = thumbnailResizer.getWidth()
    val height = thumbnailResizer.getHeight()

    if (isConstraintLayout) {
        val layoutParams = ConstraintLayout.LayoutParams(width, height)
        this.layoutParams = layoutParams
    } else {
        val layoutParams = RelativeLayout.LayoutParams(width, height)
        this.layoutParams = layoutParams
    }
    this.requestLayout()
}


fun ImageView.resizeHomeBanner() {

    val width = Utils.getScreenWidth(this.context)
    var height = Utils.getScreenHeight(this.context)
    height = (height * .85).toInt()
    val layoutParams = ConstraintLayout.LayoutParams(width, height)
    this.layoutParams = layoutParams
    this.requestLayout()
}

fun LinearLayout.resizeItem() {

    val width = Utils.getWidthForGridItem(this.context)
    val height = Utils.getHeightForGridItem(this.context)
    this.requestLayout()
    this.layoutParams.height = height
    this.layoutParams.width = width
}

fun LinearLayout.resizeCategoryItem() {

    val width = Utils.getWidthForGridItem(this.context)
    val height = Utils.getHeightForGridItem(this.context)
    this.requestLayout()
    this.layoutParams.height = height * 2
    this.layoutParams.width = width
}

fun RecyclerView.addDividerDecoration(orientation: Int) {
    val dividerItemDecoration = DividerItemDecoration(this.context,
            orientation)
    this.addItemDecoration(dividerItemDecoration)
}


fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        var timer: CountDownTimer? = null

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            timer?.cancel()
            timer = object : CountDownTimer(1000, 1500) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    afterTextChanged.invoke(editable.toString())
                }
            }.start()
        }
    })

}
