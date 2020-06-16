package com.perseverance.phando

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.perseverance.phando.ui.WaitingDialog

/**
 * A simple [Fragment] subclass.
 */
open class BaseActivity : Fragment() {
    protected var waitingDialog: WaitingDialog? = null
    lateinit var appCompatActivity: AppCompatActivity
    protected lateinit var navigator :NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitingDialog = WaitingDialog(appCompatActivity)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity){
            appCompatActivity=context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigator = view.findNavController()
    }
    fun showProgress(message: String?=null) {

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
