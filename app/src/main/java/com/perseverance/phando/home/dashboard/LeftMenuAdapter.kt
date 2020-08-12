package com.perseverance.phando.home.dashboard

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.Category
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class LeftMenuAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<Category, AdapterClickListener, BaseViewHolder<Category, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Category, AdapterClickListener> {
        return LeftMenuViewHolder(inflate(R.layout.tuple_left_menu, parent), listener)
    }


}
