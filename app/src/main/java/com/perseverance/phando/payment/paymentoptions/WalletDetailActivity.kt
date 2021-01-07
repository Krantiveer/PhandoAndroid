package com.perseverance.phando.payment.paymentoptions

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.Toast
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.payment.subscription.CreateOrderResponse
import com.perseverance.phando.payment.subscription.SubscriptionsViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_base_list.*
import kotlinx.android.synthetic.main.activity_payment_options.*
import kotlinx.android.synthetic.main.activity_payment_options.toolbar
import org.json.JSONObject

class WalletDetailActivity : BaseScreenTrackingActivity(), PaymentResultListener {

    override var screenName="WalletDetail"

    private var razorpayOrdertId: String? = null
    private val subscriptionsViewModel by lazy {
        ViewModelProvider(this).get(SubscriptionsViewModel::class.java)
    }
    private val walletDetailViewModel by lazy {
        ViewModelProvider(this).get(WalletDetailViewModel::class.java)
    }
    var MAX_RECHARGE= 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        hint.text="Add points to ${getString(R.string.app_name)}"
        chipGroup.isSingleSelection = true
        progressBar.visible()
        walletDetailViewModel.walletDetailLiveData.observe(this, Observer {
            it ?: return@Observer
            progressBar.gone()
            wallet.text = "Balance Points : ${it.balance}"
            MAX_RECHARGE= it.max_recharge_point
            when (it.is_active) {
                0 -> {
                    deactivate.text = "Activate"
                    startActivityForResult(Intent(this@WalletDetailActivity,WalletTCActivity::class.java),101)
                    addPoints.isEnabled=false
                    chipGroup.isEnabled=false
                }
                1 -> {
                    deactivate.text = "Deactivate"
                    addPoints.isEnabled=true
                    chipGroup.isEnabled=true
                }
            }
            chipGroup.removeAllViews()
            it.getWalletRechargePoints.forEachIndexed { index, amount ->
                val chip = Chip(this@WalletDetailActivity)
                val paddingDp = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 10f,
                        resources.displayMetrics
                ).toInt()
                chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
                chip.setText(amount)
               // chip.isCheckable = true
                chip.setOnClickListener {
                    createOrder(chip.text.toString())
                }
                chipGroup.addView(chip)

            }

        })
        walletDetailViewModel.refreshWallet()

        deactivate.setOnClickListener {
            walletDetailViewModel.walletDetailLiveData.value?.let {
                when(it.is_active){
                    0, 2 -> {
                        progressBar.visible()
                        walletDetailViewModel.activateWallet()
                    }
                    else ->{
                        progressBar.visible()
                        walletDetailViewModel.deActivateWallet()
                    }
                }
            }
        }
        addPoints.setOnClickListener {
            amount.text.toString().let {
                if (it.isNotBlank()){
                    createOrder(it)
                }else if(it.toInt() > MAX_RECHARGE) {
                    toast("Max allowed recharge points is $MAX_RECHARGE")
                }else
                {
                    toast("Please enter amount")
                }
            }

        }
        history.setOnClickListener {
            startActivity(Intent(this@WalletDetailActivity,WalletHistoryActivity::class.java))
        }
    }


    //Payment
    private fun createOrder(points: String) {
        val map: MutableMap<String, String> = HashMap()
        map["payment_type"] = "wallet_recharge"
        map["points"] = points
        map["payment_mode"] = "razorpay"

        subscriptionsViewModel.createOrder(map).observe(this, androidx.lifecycle.Observer {

            progressBar.gone()

            when (it?.status) {
                LoadingStatus.LOADING -> {
                    progressBar.visible()
                }
                LoadingStatus.ERROR -> {
                    progressBar.gone()
                    it.message?.let {
                        Toast.makeText(this@WalletDetailActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
                LoadingStatus.SUCCESS -> {
                    progressBar.gone()
                    it.data?.let {
                        if(it.status=="error"){
                            it.message?.let { it1 -> toast(it1) }
                        }else{
                            startPayment(it)
                        }
                    }



                }

            }

        })
    }

    private fun startPayment(orderResponse: CreateOrderResponse?) {

        orderResponse?.let { createOrderResponse ->
            val activity: Activity = this
            val co = Checkout()
            co.setKeyID(createOrderResponse.key)
            try {
                razorpayOrdertId = createOrderResponse.gateway_order_id
                val options = JSONObject()
                options.put("name", createOrderResponse.app_name)
                options.put("description", createOrderResponse.description)
                options.put("amount", (createOrderResponse.order_details!!.amount * 100).toString())
                options.put("order_id", createOrderResponse.gateway_order_id)

                val prefill = JSONObject()
                prefill.put("email", createOrderResponse.user_email)
                prefill.put("contact", createOrderResponse.user_mobile)

                options.put("prefill", prefill)

                co.open(activity, options)

            } catch (e: Exception) {
                razorpayOrdertId = null
                toast("Error in payment: " + e.message)
                e.printStackTrace()
            }
        } ?: toast("Error in payment. Unable to get order ")

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        // MyLog.e("razorpay", "$p0 : $p1")
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        if (razorpayPaymentId == null) {
            toast("Payment failed")
            return
        }
        //Log.e("razorpay", "$razorpayPaymentId")
        val map: MutableMap<String, String> = HashMap()
        map["razorpay_order_id"] = razorpayOrdertId!!
        map["razorpay_payment_id"] = razorpayPaymentId

        subscriptionsViewModel.updateOrderOnServer(map).observe(this@WalletDetailActivity, androidx.lifecycle.Observer {

            progressBar.gone()

            when (it?.status) {
                LoadingStatus.LOADING -> {
                    progressBar.visible()
                }
                LoadingStatus.ERROR -> {
                    progressBar.gone()
                    it.message?.let {
                        toast(it)
                    }
                }
                LoadingStatus.SUCCESS -> {
                    progressBar.gone()
                    it.data?.message?.let { it1 -> toast(it1) }
                    if (it.data?.status.equals("success", true)) {
                        walletDetailViewModel.refreshWallet()
                        if (intent.getBooleanExtra("isBuy",false)){
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK){
            walletDetailViewModel.refreshWallet()
        }else{
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}