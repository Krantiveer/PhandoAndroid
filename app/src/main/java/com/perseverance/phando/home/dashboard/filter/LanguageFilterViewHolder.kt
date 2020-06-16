package com.perseverance.phando.home.dashboard.filter

import android.view.View
import com.perseverance.phando.db.Language
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.item_filter.view.*

class LanguageFilterViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Language, AdapterClickListener>(itemView, listener) {

        init {
            itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

        }
        override fun onBind(category: Language) {
            itemView.tag = category
            itemView.txt_item.text = category.language


        }
    }