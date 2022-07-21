package com.perseverance.patrikanews.utils

import android.content.Context
import android.graphics.PorterDuff
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.phando.R
import com.perseverance.phando.db.Language
import com.perseverance.phando.dialogs.ListDialog
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.resize.ThumbnailResizer
import com.perseverance.phando.ui.LanguagesDialog
import com.perseverance.phando.ui.ParentalControlPinDialog
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.Utils
import java.text.DecimalFormat
import java.util.*

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun decimalFormat(value:Int):String? {
    val form = DecimalFormat("0.0")
   return form.format(value)
}

fun getDurationString(seconds: Int): String? {
    var seconds = seconds
    val hours = seconds / 3600
    val minutes = seconds % 3600 / 60
    seconds = seconds % 60

    if (hours <= 0) {
        return twoDigitString(minutes) + " min"
    }

    if (hours <= 0 && minutes <= 0) {
        return twoDigitString(seconds) + " sec"
    }

    return twoDigitString(hours) + "hrs : " + twoDigitString(minutes) + "min"
}

fun twoDigitString(number: Int): String {
    if (number == 0) {
        return "0"
    }
    return if (number / 10 == 0) {
        "$number"
    } else number.toString()
}

fun getRandomColor(): Int {
    val rnd = Random()
//    val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

    val color = (Math.random() * 16777215).toInt() or (0xFF shl 23)

    return color


}

fun View.gone() {
    visibility = View.GONE
}


fun EditText.spaceFilter() {
    this.filters = this.filters.let {
        it + InputFilter { source, _, _, _, _, _ ->
            source.filterNot { char -> char.isWhitespace() }
        }
    }
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

fun String?.isSuccess(): Boolean = this == "success"

fun String.isError(): Boolean = this == "error"

fun String.isLoading(): Boolean = this == "loading"

fun Context.openListDialog(
    mUserList: ArrayList<CategoryTab>,
    returns: (CategoryTab) -> Unit,
) {
    ListDialog(
        this,
        R.style.pullBottomfromTop,
        R.layout.dialog_list_category,
        mUserList,
        object : ListDialog.ItemClick {
            override fun onItemClick(data: CategoryTab) {
                returns(data)
            }
        }
    ).showDialog()
}


fun Context.openLanguageDialog(
    mUserList: ArrayList<Language>,
    returns: (StringBuilder) -> Unit,
) {
    if (!LanguagesDialog.isOpen) {
        LanguagesDialog(
            this,
            R.style.pullBottomfromTop,
            R.layout.dialog_language,
            mUserList,
            object : LanguagesDialog.ItemClick {
                override fun onItemClick(data: StringBuilder) {
                    returns(data)
                }
            }
        ).showDialog()
    }
}

fun Context.openParentalPinDialog(
    isPinSet: Int,
    isUpdate: Boolean = false,
    returns: (String, String, String) -> Unit,
) {
    ParentalControlPinDialog(
        this,
        isPinSet,
        isUpdate,
        R.style.pullBottomfromTop,
        R.layout.dialog_parental_pin,
        object : ParentalControlPinDialog.ItemClick {
            override fun onItemClick(pin: String, confirm: String, current: String) {
                returns(pin, confirm, current)
            }
        }
    ).showDialog()
}


fun Context.showDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    onPositiveClick: (() -> Unit)? = null,
    neutralButtonText: String = "",
    onNeutralClick: (() -> Unit)? = null,
) {
    val alertD = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme).apply {
        setIcon(R.mipmap.ic_launcher)
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { dialog, whichButton -> }
        if (neutralButtonText.isNotEmpty()) setNeutralButton(neutralButtonText) { dialog, whichButton -> }
        //val value: String = input.text.toString()
        val dialog = this.create()
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            this.setOnClickListener {
                if (dialog.isShowing) dialog.dismiss()
                onPositiveClick?.invoke() ?: return@setOnClickListener
            }
        }
        if (neutralButtonText.isNotEmpty()) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).apply {
            this.setOnClickListener {
                if (dialog.isShowing) dialog.dismiss()
                onNeutralClick?.invoke() ?: return@setOnClickListener
            }
        }
    }


//    val alert = AlertDialog.Builder(this).apply {
//        setTitle(title)
//        setMessage(message)
//        setPositiveButton(positiveButtonText) { dialog, whichButton -> }
//        if (neutralButtonText.isNotEmpty()) setNeutralButton(neutralButtonText) { dialog, whichButton -> }
//        //val value: String = input.text.toString()
//        val dialog = this.create()
//        dialog.show()
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
//            this.setOnClickListener {
//                if (dialog.isShowing) dialog.dismiss()
//                onPositiveClick?.invoke() ?: return@setOnClickListener
//            }
//        }
//        if (neutralButtonText.isNotEmpty()) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).apply {
//            this.setOnClickListener {
//                if (dialog.isShowing) dialog.dismiss()
//                onNeutralClick?.invoke() ?: return@setOnClickListener
//            }
//        }
//    }
}