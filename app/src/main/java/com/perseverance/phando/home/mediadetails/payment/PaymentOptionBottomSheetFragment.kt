package com.perseverance.phando.home.mediadetails.payment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.perseverance.phando.R
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import kotlinx.android.synthetic.main.payment_option_bottom_sheet.*
import kotlinx.android.synthetic.main.purchase_option_bottom_sheet.*

class PaymentOptionBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var paymentOptionSelection: PaymentOptionSelection

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MediaDetailActivity) {
            paymentOptionSelection = context as PaymentOptionSelection

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_option_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallet.setOnClickListener {
            paymentOptionSelection.onPaymentOptionSelected(true)
            dismiss()
        }
        razorpay.setOnClickListener {
            paymentOptionSelection.onPaymentOptionSelected(false)
            dismiss()
        }

    }

}