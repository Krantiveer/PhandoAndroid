package com.perseverance.phando.home.dashboard

import android.view.View
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.item_grid_text.view.*


class SettingFragmentViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<SettingsFragment.SettingItems, AdapterClickListener>(itemView, listener) {


    init {
        itemView.categoryName.setOnClickListener { v -> listener.onItemClick(v.categoryName.tag) }

    }

    override fun onBind(item: SettingsFragment.SettingItems) {
        itemView.categoryName.tag = item
        itemView.categoryName.text = item.diaplayName
    }
}