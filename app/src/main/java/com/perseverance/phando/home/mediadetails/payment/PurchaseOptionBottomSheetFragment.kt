package com.perseverance.phando.home.mediadetails.payment

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.perseverance.phando.R
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.payment_option_bottom_sheet.*

class PurchaseOptionBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var purchaseOptionSelection: PurchaseOptionSelection

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MediaDetailActivity) {
            purchaseOptionSelection = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.payment_option_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val purchaseOption = arguments?.getParcelable<PurchaseOption>("payment_option")!!
        var price= number2digits(purchaseOption?.value)
        if (purchaseOption.discount_percentage > 0) {
            val discount = (purchaseOption.value * purchaseOption.discount_percentage) / 100
            price = number2digits(purchaseOption?.value - discount)
        }
        rentPriceInfo.text = "INR ${price}"
        when (purchaseOption.key) {
            "rent_price" -> {
                rentPriceType.text="Renting a video"
                rentDescription.text =purchaseOption.note
            }
            "purchase_price" -> {
                rentPriceType.text="Buying a video"
                rentDescription.text =purchaseOption.note

            }
        }
        offlineDescription.text = "Purchased media will be available for offline viewing (download and watch inside app) until expired"

        rentContainer.setOnClickListener {
            purchaseOptionSelection.onPurchaseOptionSelected(purchaseOption)
            dismiss()
        }
    }
    fun number2digits(number: Float): String {
        return String.format("%.2f", number)
    }
}