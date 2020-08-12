package com.perseverance.phando.home.dashboard.filter

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter
import com.perseverance.phando.home.dashboard.models.Filter


class FilterAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<Filter, AdapterClickListener, BaseViewHolder<Filter, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Filter, AdapterClickListener> {
        return FilterViewHolder(inflate(R.layout.item_filter, parent), listener)
    }


}
