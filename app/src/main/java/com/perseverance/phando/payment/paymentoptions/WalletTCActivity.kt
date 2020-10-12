package com.perseverance.phando.payment.paymentoptions

import android.content.DialogInterface
import com.perseverance.phando.BaseScreenTrackingActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import kotlinx.android.synthetic.main.activity_wallet_t_c.*
import kotlinx.android.synthetic.main.activity_wallet_t_c.progressBar

class WalletTCActivity : BaseFragment() {
    override var screenName= BaseConstants.WALLWET_TC_SCREEN

    private val walletDetailViewModel : WalletDetailViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_wallet_t_c,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tcWebview.settings.javaScriptEnabled=true
        // tcWebview.setBackgroundColor(R.color.bg_app)
        //tcWebview.loadUrl("javascript:document.body.style.color=\"white\";")
        walletDetailViewModel.tcResponseDataLiveData.observe(this, Observer {
            progressBar.gone()
            tcWebview.loadData(it.data, "text/html", "UTF-8")
        })

        activate.setOnClickListener {
            if (!cbAgree.isChecked){
                toast("Please accept terms and conditions")
                return@setOnClickListener
            }
            progressBar.visible()
            walletDetailViewModel.activateWallet()
        }

        walletDetailViewModel.activateWalletLiveData.observe(this@WalletTCActivity, Observer {
            it?:return@Observer
            progressBar.gone()
            if (it.status == "success"){
                findNavController().popBackStack()
            }
        })
        progressBar.visible()
        walletDetailViewModel.getTC()
    }


}