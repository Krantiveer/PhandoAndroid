package com.perseverance.phando.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window

abstract class BaseDialog : Dialog {
    var mContext: Context? = null
    private var mWidth = 0
    private var mHeight = 0

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}
    constructor(context: Context) : super(context) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowManager = this.window!!.attributes
        setContentView(contentView)
        mContext = context
        defaults
        onCreateStuff()
        initListeners()
        windowManager.width = WindowManager.LayoutParams.MATCH_PARENT
        windowManager.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = windowManager
    }

    protected abstract fun onCreateStuff()
    protected abstract fun initListeners()
    abstract val contentView: Int
    val defaults: Unit
        get() {
            val displayMetrics = DisplayMetrics()
            val windowmanager =
                mContext!!.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowmanager.defaultDisplay.getMetrics(displayMetrics)
            mWidth = displayMetrics.widthPixels
            mHeight = displayMetrics.heightPixels
            Log.e("Height = ", "$mHeight width $mWidth")
        }

    fun showDialog() {
        if (this.isShowing) {
            return
        } else {
            this.show()
        }
    }

    fun dismissDialog() {
        if (this.isShowing) {
            this.dismiss()
        } else {
            return
        }
    }


}