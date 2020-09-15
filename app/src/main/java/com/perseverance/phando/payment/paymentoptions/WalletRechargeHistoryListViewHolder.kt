package com.perseverance.phando.payment.paymentoptions

import android.view.View
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.item_wallet_recharge.view.*


class WalletRechargeHistoryListViewHolder(itemView: View) : BaseViewHolder<WalletRechargeHistory, AdapterClickListener>(itemView) {

    override fun onBind(walletRechargeHistory: WalletRechargeHistory) {
        itemView.tag = walletRechargeHistory
        itemView.date.text = walletRechargeHistory.datetime
        itemView.walletPoint.text = walletRechargeHistory.wallet_points.toString()
        itemView.summery.text=walletRechargeHistory.payment_summary
        itemView.transactionID.text=walletRechargeHistory.transaction_id
        itemView.status.text=walletRechargeHistory.status

    }
}