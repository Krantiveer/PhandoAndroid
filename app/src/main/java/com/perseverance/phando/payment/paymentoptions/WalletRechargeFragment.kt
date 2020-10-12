package com.perseverance.phando.payment.paymentoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.isSuccess
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import kotlinx.android.synthetic.main.fragment_wallet_recharge.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WalletRechargeFragment : BaseFragment() {
    override var screenName= BaseConstants.WALLWET_RECHARGE_SCREEN
    private val paymentActivityViewModel: PaymentActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_recharge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val amount = arguments?.getString("amount")
        priceInfo.text = "₹ $amount"

        razorpay.setOnClickListener {
            createOrder(amount)

        }
        paymentActivityViewModel.loaderLiveData.observe(viewLifecycleOwner, Observer {
            if (it) progressBar.visible() else progressBar.gone()

        })

        paymentActivityViewModel.updateOrderOnServerLiveData.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            paymentActivityViewModel.refreshWallet()
            paymentActivityViewModel.updateOrderOnServerLiveData.value = null
            if (paymentActivityViewModel.purchaseOption == null) {
                findNavController().popBackStack()
            } else {
                val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.paymentOptionFragment, true)
                        .build()
                findNavController().navigate(R.id.action_walletRechargeFragment_to_paymentOptionFragment, null, navOptions)

            }


        })

        paymentActivityViewModel.walletDetailLiveData.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            points.text = "Balance Points : ${it.balance}"

        })

    }

    private fun createOrder(amount: String?) {
        progressBar.visible()
        val map: MutableMap<String, String?> = HashMap()
        lifecycleScope.launch {
            map["payment_type"] = "wallet_recharge"
            map["points"] =amount
            map["payment_mode"] = "razorpay"
            val createOrderResponse = withContext(Dispatchers.IO) { paymentActivityViewModel.createOrder(map) }
            progressBar.gone()
            if (createOrderResponse.status.isSuccess()) {
                paymentActivityViewModel.createOrderResponseLiveData.value=createOrderResponse
            } else {
                createOrderResponse.message?.let { it1 -> toast(it1) }
            }
        }

    }

}