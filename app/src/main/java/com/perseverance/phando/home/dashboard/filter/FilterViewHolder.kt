package com.perseverance.phando.home.dashboard.filter

import android.view.View
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.home.dashboard.models.FilterForAdopter
import kotlinx.android.synthetic.main.item_filter.view.*

class FilterViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<FilterForAdopter, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(filter: FilterForAdopter) {
        itemView.tag = filter
        itemView.txt_item.text = filter.name
        if (filter.isSelected) {
            itemView.txt_item.setTextColor(itemView.context.resources.getColor(R.color.red_normal))
        } else {
            itemView.txt_item.setTextColor(itemView.context.resources.getColor(R.color.white))
        }


    }
}