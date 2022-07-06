package com.perseverance.phando.ui

import android.content.Context
import android.view.Gravity
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.dialogs.BaseDialog
import kotlinx.android.synthetic.main.dialog_parental_pin.*

class ParentalControlPinDialog(
    context: Context,
    val isPinSet: Int,
    val isUpdate: Boolean,
    themeResId: Int,
    val LayoutId: Int,
    var mClick: ItemClick,
) : BaseDialog(context, themeResId) {

    init {
        val wmlp = this.window!!.attributes
        wmlp.gravity = Gravity.BOTTOM
        window!!.attributes = wmlp
    }

    override fun onCreateStuff() {
        setCanceledOnTouchOutside(true)
        if (isUpdate) {
            btnSave.text="Update"
            txtSetPin.text = "New Pin"
            txtConfirmPin.text = "Confirm New Pin"
            txtConfirmPin.visible()
            inputConfirm.visible()
            txtCurrentPin.visible()
            inputCurrent.visible()
        }else {
            txtCurrentPin.gone()
            inputCurrent.gone()
            if (isPinSet == 1) {
                txtSetPin.text = "Enter Pin"
                txtConfirmPin.gone()
                inputConfirm.gone()
            } else {
                txtSetPin.text = "Set Pin"
                txtConfirmPin.visible()
                inputConfirm.visible()
            }
        }
    }

    override fun initListeners() {
        btnCancel.setOnClickListener {
            dismissDialog()
        }

        btnSave.setOnClickListener {
            mClick.onItemClick(inputSet.otp.toString(),
                inputConfirm.otp.toString(),
                inputCurrent.otp.toString())
            dismissDialog()
        }
    }

    interface ItemClick {
        fun onItemClick(pin: String, confirm: String, current: String)
    }

    override val contentView: Int
        get() = LayoutId


}