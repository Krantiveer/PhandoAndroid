package com.perseverance.phando.payment.paymentoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import kotlinx.android.synthetic.main.activity_wallet_t_c.*

class WalletTCFragment : BaseFragment() {
    override var screenName= BaseConstants.WALLWET_TC_SCREEN
    private val paymentActivityViewModel: PaymentActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                appCompatActivity.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_wallet_t_c, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tcWebview.settings.javaScriptEnabled = true
        // tcWebview.setBackgroundColor(R.color.bg_app)
        //tcWebview.loadUrl("javascript:document.body.style.color=\"white\";")
        paymentActivityViewModel.tcResponseDataLiveData.observe(viewLifecycleOwner, Observer {
            progressBar.gone()
            it.data?.let { it1 -> tcWebview.loadData(it1, "text/html", "UTF-8") }
        })

        activate.setOnClickListener {
            if (!cbAgree.isChecked) {
                toast("Please accept terms and conditions")
                return@setOnClickListener
            }
            progressBar.visible()
            paymentActivityViewModel.activateWallet()
        }

        paymentActivityViewModel.activateWalletLiveData.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            progressBar.gone()
            if (it.status == "success") {
                findNavController().popBackStack()

            }
        })
        progressBar.visible()
        paymentActivityViewModel.getTC()
    }


}