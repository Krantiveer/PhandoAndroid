package com.perseverance.phando.home.dashboard.filter

import android.view.View
import com.perseverance.phando.db.Category
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.item_filter.view.*

class GenresFilterViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Category, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(category: Category) {
        itemView.tag = category
        itemView.txt_item.text = category.name


    }
}