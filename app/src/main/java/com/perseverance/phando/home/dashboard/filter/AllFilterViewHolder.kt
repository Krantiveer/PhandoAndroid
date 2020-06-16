package com.perseverance.phando.home.dashboard.filter

import android.view.View
import com.perseverance.phando.db.Filter
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.item_filter.view.*

class AllFilterViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Filter, AdapterClickListener>(itemView, listener) {

        init {
            itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

        }
        override fun onBind(filter: Filter) {
            itemView.tag = filter
            itemView.txt_item.text = filter.title


        }
    }