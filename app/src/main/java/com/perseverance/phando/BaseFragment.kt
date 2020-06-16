package com.perseverance.phando

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.perseverance.phando.ui.WaitingDialog

/**
 * A simple [Fragment] subclass.
 */
open class BaseFragment : Fragment() {
    protected var waitingDialog: WaitingDialog? = null


    protected var appCompatActivity: AppCompatActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appCompatActivity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitingDialog = WaitingDialog(appCompatActivity)

    }

    fun showProgress(message: String?=null) {
        appCompatActivity?.let {
            if (!it.isFinishing) {
                waitingDialog?.let { waitingDialog ->
                    message?.let {
                        waitingDialog.setMessage(it)
                    }
                    waitingDialog.show()
                }
            }
        }
    }

    fun dismissProgress() {
        waitingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }

        }

    }

}
