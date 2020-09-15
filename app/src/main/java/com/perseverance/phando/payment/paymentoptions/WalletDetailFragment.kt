package com.perseverance.phando.payment.paymentoptions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.isSuccess
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.payment.subscription.CreateOrderResponse
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_payment_options.*
import kotlinx.android.synthetic.main.activity_payment_options.progressBar
import kotlinx.android.synthetic.main.activity_payment_options.wallet
import kotlinx.android.synthetic.main.activity_wallet_t_c.*
import kotlinx.android.synthetic.main.fragment_payment_option.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalletDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalletDetailFragment : BaseFragment() {

    var MAX_RECHARGE= 1000
    private val paymentActivityViewModel : PaymentActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hint.text="Add points to ${getString(R.string.app_name)}"
        chipGroup.isSingleSelection = true

        paymentActivityViewModel.loaderLiveData.observe(this, Observer {
            if(it) progressBar.visible() else progressBar.gone()

        })
        paymentActivityViewModel.walletDetailLiveData.observe(this, Observer {
            it ?: return@Observer
            wallet.text = "Balance Points : ${it.balance}"
            MAX_RECHARGE= it.max_recharge_point
            when (it.is_active) {
                0 -> {
                    deactivate.text = "Activate"
                    addPoints.isEnabled=false
                    chipGroup.isEnabled=false
                    findNavController().navigate(R.id.action_walletDetailFragment_to_walletTCFragment)

                }
                1 -> {
                    deactivate.text = "Deactivate"
                    addPoints.isEnabled=true
                    chipGroup.isEnabled=true
                }
            }
            chipGroup.removeAllViews()
            it.getWalletRechargePoints.forEachIndexed { index, amount ->
                val chip = Chip(appCompatActivity)
                val paddingDp = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10f,
                        resources.displayMetrics
                ).toInt()
                chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
                chip.setText(amount)
                // chip.isCheckable = true
                chip.setOnClickListener {
                   val action = WalletDetailFragmentDirections.actionWalletDetailFragmentToWalletRechargeFragment(chip.text.toString())
                    findNavController().navigate(action)
                }
                chipGroup.addView(chip)

            }

        })

        paymentActivityViewModel.updateOrderOnServerLiveData.observe(this, Observer {
            it?:return@Observer
            paymentActivityViewModel.refreshWallet()
            paymentActivityViewModel.updateOrderOnServerLiveData.value=null
            findNavController().popBackStack()



        })

        paymentActivityViewModel.activateWalletLiveData.observe(this, Observer {
            it?:return@Observer
            progressBar.gone()
            if (it.status == "success"){
                if (it.message=="deactivated"){
                    appCompatActivity.finish()
                }

            }
        })

        deactivate.setOnClickListener {
            paymentActivityViewModel.walletDetailLiveData.value?.let {
                when(it.is_active){
                    0, 2 -> {
                        progressBar.visible()
                        paymentActivityViewModel.activateWallet()
                    }
                    else ->{
                        val alertDialog = MaterialAlertDialogBuilder(appCompatActivity, R.style.AlertDialogTheme).create()
                        alertDialog.setTitle("Deactivate your Wallet?")
                        alertDialog.setMessage("You will not be able to use your remaining points if you choose to deactivate your wallet. Are you sure you wish to continue?")
                        alertDialog.setCancelable(false)

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.confirm)
                        ) { dialog, which ->
                            progressBar.visible()
                            paymentActivityViewModel.deActivateWallet()
                        }
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.cancel)
                        ) { dialog, which ->
                        }
                        alertDialog.show()



                    }
                }
            }
        }
        addPoints.setOnClickListener {
            amount.text.toString().let {
                if (it.isNotBlank()){
                    val action = WalletDetailFragmentDirections.actionWalletDetailFragmentToWalletRechargeFragment(it)
                    findNavController().navigate(action)
                }else if(it.toInt() > MAX_RECHARGE) {
                    toast("Max allowed recharge points is $MAX_RECHARGE")
                }else
                {
                    toast("Please enter amount")
                }
            }

        }
        history.setOnClickListener {
            startActivity(Intent(appCompatActivity,WalletHistoryActivity::class.java))
        }
    }



}