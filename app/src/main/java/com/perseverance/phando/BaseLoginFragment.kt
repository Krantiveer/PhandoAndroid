package com.perseverance.phando

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.perseverance.phando.ui.WaitingDialog

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseLoginFragment : BaseFragment() {
    protected var waitingDialog: WaitingDialog? = null
    protected lateinit var navigator: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitingDialog = WaitingDialog(appCompatActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = view.findNavController()
    }

    fun showProgress(message: String? = null) {

        if (!appCompatActivity.isFinishing) {
            waitingDialog?.let { waitingDialog ->
                message?.let {
                    waitingDialog.setMessage(it)
                }
                waitingDialog.show()
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
