package com.perseverance.phando.payment.paymentoptions

import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.item_wallet_history.view.*


class WalletRechargeHistoryListViewHolder(itemView: View) : BaseViewHolder<WalletRechargeHistory, AdapterClickListener>(itemView) {

    override fun onBind(walletRechargeHistory: WalletRechargeHistory) {
        itemView.tag = walletRechargeHistory
        itemView.date.text = walletRechargeHistory.datetime
        itemView.walletPoint.text = walletRechargeHistory.wallet_points.toString()
        itemView.summary.text=walletRechargeHistory.payment_summary
        itemView.tv_transaction_id.text=walletRechargeHistory.transaction_id
        itemView.tv_transaction_status.text=walletRechargeHistory.status
        itemView.tv_transaction_amount.text = walletRechargeHistory.currency_symbol + walletRechargeHistory.amount.toString()
        itemView.iv_drop_down.setOnClickListener {
            if (itemView.vw_detail_container.isVisible){
                itemView.vw_detail_container.gone()
                itemView.iv_drop_down.setImageResource(R.drawable.ic_detail_arrow_down)
            }else{
                itemView.vw_detail_container.apply {
                    setBackgroundColor(Color.parseColor("#141B26"))
                    visible()
                }
                itemView.iv_drop_down.setImageResource(R.drawable.ic_detail_arrow_up)
            }
        }
        if (walletRechargeHistory.transaction_type=="Cr"){
            itemView.walletPoint.setTextColor(Color.GREEN)
        }else{
            itemView.walletPoint.setTextColor(Color.RED)
        }

    }
}