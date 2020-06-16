package com.perseverance.phando.home.series

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class SeriesListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<SeriesData, AdapterClickListener, BaseViewHolder<SeriesData, AdapterClickListener>>(context, listener) {

    private val VIEW_TYPE_LIST = 1
    private val VIEW_TYPE_HEADER = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SeriesData, AdapterClickListener> {

        return SeriesListViewHolder(inflate(R.layout.home_widget, parent), listener)
    }

}
