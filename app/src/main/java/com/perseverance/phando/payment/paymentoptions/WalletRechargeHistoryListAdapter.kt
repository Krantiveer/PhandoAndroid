package com.perseverance.phando.payment.paymentoptions

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class WalletRechargeHistoryListAdapter(context: Context) : GenericRecyclerViewAdapter<WalletRechargeHistory, AdapterClickListener, BaseViewHolder<WalletRechargeHistory, AdapterClickListener>>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<WalletRechargeHistory, AdapterClickListener> {
        return WalletRechargeHistoryListViewHolder(inflate(R.layout.item_wallet_history, parent))
    }
}
