package com.perseverance.phando.payment.paymentoptions

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.isSuccess
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseFragment
import com.perseverance.phando.R
import com.perseverance.phando.Session
import com.perseverance.phando.constants.BaseConstants
import kotlinx.android.synthetic.main.fragment_payment_option.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class PaymentOptionFragment : BaseFragment() {
    override var screenName = BaseConstants.PAYMENT_OPTIONS_SCREEN
    private val paymentActivityViewModel: PaymentActivityViewModel by activityViewModels()
    private val logger by lazy {
        AppEventsLogger.newLogger(appCompatActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentActivityViewModel.purchaseOption?.let {
            logAddToCartEvent(it.payment_info.media_id.toString(), it.payment_info.type, it.final_price.toDouble(), it.currency)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val priceInfoTxt = "${paymentActivityViewModel.purchaseOption?.currency_symbol} ${paymentActivityViewModel.purchaseOption?.final_price} Or " +
                "${paymentActivityViewModel.purchaseOption?.final_points} points"
        priceInfo.text = priceInfoTxt
        when (paymentActivityViewModel.purchaseOption?.key) {
            "rent_price" -> {
                itemName.text = "${paymentActivityViewModel.purchaseOption?.mediaTitle} (Rent)"
            }
            "purchase_price" -> {
                itemName.text = "${paymentActivityViewModel.purchaseOption?.mediaTitle} (Buy)"
            }
        }

        wallet.setOnClickListener {
            if (paymentActivityViewModel.getWallet()?.balance!! >= paymentActivityViewModel.purchaseOption?.final_points!!) {
                val map: MutableMap<String, String?> = HashMap()
                map["payment_type"] = paymentActivityViewModel.purchaseOption?.payment_info?.payment_type
                map["media_id"] = paymentActivityViewModel.purchaseOption?.payment_info?.media_id.toString()
                map["type"] = paymentActivityViewModel.purchaseOption?.payment_info?.type
                map["payment_mode"] = "wallet"
                val alertDialog = MaterialAlertDialogBuilder(appCompatActivity, R.style.AlertDialogTheme).create()
                alertDialog.setTitle("Payment")
                alertDialog.setMessage("Are you sure you want to purchase ${paymentActivityViewModel.purchaseOption?.mediaTitle} for ${paymentActivityViewModel.purchaseOption?.final_points} points?")
                alertDialog.setCancelable(false)
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getString(R.string.confirm)
                ) { dialog, which ->
                    createOrder(map)
                }
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getString(R.string.cancel)
                ) { dialog, which ->
                }
                alertDialog.show()
            } else {
                findNavController().navigate(R.id.action_paymentOptionFragment_to_walletDetailFragment)
            }
        }
        razorpay.setOnClickListener {
            val map: MutableMap<String, String?> = HashMap()
            map["payment_type"] = paymentActivityViewModel.purchaseOption?.payment_info?.payment_type
            map["media_id"] = paymentActivityViewModel.purchaseOption?.payment_info?.media_id.toString()
            map["type"] = paymentActivityViewModel.purchaseOption?.payment_info?.type
            map["payment_mode"] = "razorpay"
            createOrder(map)
        }
        paymentActivityViewModel.loaderLiveData.observe(viewLifecycleOwner, Observer {
            if (it) progressBar.visible() else progressBar.gone()
        })
        paymentActivityViewModel.walletDetailLiveData.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            progressBar.gone()
            points.text = "Balance Points : ${it.balance}"
            when (it.is_active) {
                0 -> {
                    walletPay.text = "Activate"
                }
                1 -> {
                    if (it.balance >= paymentActivityViewModel.purchaseOption?.final_points!!) {
                        walletPay.text = "Pay"
                        walletHint.text = " (Balance:${it.balance} points)"
                    } else {
                        walletPay.text = "Recharge"
                        walletHint.text = " (Balance:${it.balance} points) Low balance! required points: ${paymentActivityViewModel.purchaseOption?.final_points!!}"
                    }
                }
            }
        })

        paymentActivityViewModel.updateOrderOnServerLiveData.observe(viewLifecycleOwner, Observer { it ->
            it ?: return@Observer
            paymentActivityViewModel.refreshWallet()
            paymentActivityViewModel.updateOrderOnServerLiveData.value = null
            paymentActivityViewModel.purchaseOption?.let {
                val item1 = Bundle()
                item1.putString(FirebaseAnalytics.Param.ITEM_ID, it.payment_info.media_id.toString())
                item1.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, it.payment_info.type)
                item1.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, it.payment_info.payment_type)
                item1.putString(FirebaseAnalytics.Param.ITEM_NAME, it.mediaTitle)
                Session.instance.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                    param(FirebaseAnalytics.Param.PRICE, it.final_price.toLong())
                    param(FirebaseAnalytics.Param.TRANSACTION_ID, "razorpay")
                    param(FirebaseAnalytics.Param.ITEMS, arrayOf(item1))
                }
                //Facebook tracking
                logAddPurchaseEvent(it.payment_info.media_id.toString(), it.payment_info.type, it.final_price, it.currency)
            }
            appCompatActivity.setResult(Activity.RESULT_OK)
            appCompatActivity.finish()
        })
    }

    private fun createOrder(map: Map<String, String?>) {
        progressBar.visible()
        lifecycleScope.launch {
            val createOrderResponse = withContext(Dispatchers.IO) { paymentActivityViewModel.createOrder(map) }
            progressBar.gone()
            if (createOrderResponse.status.isSuccess()) {
                if (createOrderResponse?.is_subscribed == 1) {
                    paymentActivityViewModel.refreshWallet()
                    paymentActivityViewModel.purchaseOption?.let {
                        val item1 = Bundle()
                        item1.putString(FirebaseAnalytics.Param.ITEM_ID, it.payment_info.media_id.toString())
                        item1.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, it.payment_info.type)
                        item1.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, it.payment_info.payment_type)
                        item1.putString(FirebaseAnalytics.Param.ITEM_NAME, it.mediaTitle)
                        Session.instance.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                            param(FirebaseAnalytics.Param.PRICE, it.final_price.toLong())
                            param(FirebaseAnalytics.Param.TRANSACTION_ID, "wallet")
                            param(FirebaseAnalytics.Param.ITEMS, item1)
                        }
                        //Facebook tracking
                        logAddPurchaseEvent(it.payment_info.media_id.toString(), it.payment_info.type, it.final_price, it.currency)
                    }
                    appCompatActivity.setResult(Activity.RESULT_OK)
                    appCompatActivity.finish()
                } else {
                    paymentActivityViewModel.createOrderResponseLiveData.value = createOrderResponse
                }
            } else {
                createOrderResponse.message?.let { it1 -> toast(it1) }
            }
        }
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    private fun logAddToCartEvent(contentId: String?, contentType: String?, price: Double, currency: String?) {
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType)
        params.putString(AppEventsConstants.EVENT_PARAM_VALUE_TO_SUM, currency)
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, price, params)
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    private fun logAddPurchaseEvent(mediaId: String, type: String, finalPrice: Float, currency: String) {
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, mediaId)
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, type)
        logger.logPurchase(
                finalPrice.toBigDecimal(),
                Currency.getInstance(currency),
                params)
    }
}