package com.perseverance.phando.payment.subscription

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.retrofit.ApiClient
import com.perseverance.phando.retrofit.ApiService
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_package_new.*
import kotlinx.android.synthetic.main.layout_header_new.imgBack
import kotlinx.android.synthetic.main.layout_header_new.txtTitle
import org.json.JSONObject
import java.util.*

class SubscriptionPackageActivity : BaseScreenTrackingActivity(), DataAdapter.OnClickListener, PaymentResultListener {

    override var screenName: String="Package Subscription"
    private var apiServiceLogin: ApiService? = null
    private var recyclerView: RecyclerView? = null
    private var data: ArrayList<PackageDetails>? = null
    private var adapter: DataAdapter? = null
    private var razorpayOrdertId: String? = null
    private val subscriptionsViewModel by lazy {
        ViewModelProvider(this).get(SubscriptionsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiServiceLogin = ApiClient.getLoginClient().create(ApiService::class.java)
        setContentView(R.layout.activity_package_new)
        initViews()
        Checkout.preload(applicationContext);
    }

    private fun initViews() {
        txtTitle.text = "Subscribe Now"

        imgBack.setOnClickListener {
            finish()
        }
        recyclerView = findViewById<View>(R.id.card_recycler_view) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = layoutManager
        callForPackages()
    }

    override fun onClick(packageDetails: PackageDetails) {
        createOrder(packageDetails.packageId)
    }


    private fun callForPackages() {
        subscriptionsViewModel.data.observe(this, androidx.lifecycle.Observer {

            progressBar.gone()

            when (it?.status) {
                LoadingStatus.LOADING -> {
                    progressBar.visible()
                }
                LoadingStatus.ERROR -> {
                    it.message?.let {
                        Toast.makeText(this@SubscriptionPackageActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
                LoadingStatus.SUCCESS -> {
                    progressBar.gone()
                    adapter = DataAdapter(this@SubscriptionPackageActivity, it.data?.packageDetails)
                    recyclerView!!.adapter = adapter

                }

            }

        })
    }

    private fun createOrder(package_id: String) {
        val map: MutableMap<String, String> = HashMap()
        map["package_id"] = package_id

        subscriptionsViewModel.createOrder(map).observe(this, androidx.lifecycle.Observer {

            progressBar.gone()

            when (it?.status) {
                LoadingStatus.LOADING -> {
                    progressBar.visible()
                }
                LoadingStatus.ERROR -> {
                    progressBar.gone()
                    it.message?.let {
                        Toast.makeText(this@SubscriptionPackageActivity, it, Toast.LENGTH_LONG).show()
                    }
                }
                LoadingStatus.SUCCESS -> {
                    progressBar.gone()
                    startPayment(it.data)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
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
        //Log.e("razorpay", "$razorpayPaymentId")
        val map: MutableMap<String, String> = HashMap()
        map["razorpay_order_id"] = razorpayOrdertId!!
        map["razorpay_payment_id"] = razorpayPaymentId

        subscriptionsViewModel.updateOrderOnServer(map).observe(this@SubscriptionPackageActivity, androidx.lifecycle.Observer {

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
                        setResult(Activity.RESULT_OK)
                        finish()
                    }


                }

            }

        })
    }
}