package com.perseverance.phando.payment.paymentoptions

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.isSuccess
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.R
import com.perseverance.phando.Session
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.utils.FirebaseEventUtil
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class PaymentActivity : BaseScreenTrackingActivity(),PaymentResultListener {

    override var screenName=""
    private var razorpayOrdertId :String?=null

    private val paymentActivityViewModel by lazy {
        ViewModelProvider(this).get(PaymentActivityViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val navController = findNavController(R.id.dashboard_container)// where nav_host_fragment is the id for your Main NavHost fragment
        setupActionBarWithNavController(this@PaymentActivity, navController)

        paymentActivityViewModel.purchaseOption=intent.getParcelableExtra(BaseConstants.PURCHASE_OPTION)
        paymentActivityViewModel.refreshWallet()
        paymentActivityViewModel.createOrderResponseLiveData.observe(this, Observer { orderResponse ->
            orderResponse?.let { createOrderResponse ->
                val activity: Activity = this@PaymentActivity
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
        })
        if (paymentActivityViewModel.purchaseOption==null){
            val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.paymentOptionFragment, true)
                    .build()
            findNavController(R.id.dashboard_container).navigate(R.id.action_paymentOptionFragment_to_walletDetailFragment,null,navOptions)
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

    override fun onPaymentError(p0: Int, p1: String?) {
        // MyLog.e("razorpay", "$p0 : $p1")
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        if (razorpayPaymentId == null) {
            toast("Payment failed")
            return
        }
        paymentActivityViewModel.loaderLiveData.value=true
        val map: MutableMap<String, String> = HashMap()
        map["razorpay_order_id"] = razorpayOrdertId!!
        map["razorpay_payment_id"] = razorpayPaymentId
        lifecycleScope.launch {
            val updateOrderOnServer = withContext(Dispatchers.IO) {
                paymentActivityViewModel.updateOrderOnServer(map)
            }
            paymentActivityViewModel.loaderLiveData.value=false
            if (updateOrderOnServer.status.isSuccess()){
                paymentActivityViewModel.updateOrderOnServerLiveData.value=updateOrderOnServer

            }else{
                toast("Payment failed")
            }

        }

    }

}