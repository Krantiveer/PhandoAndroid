package com.perseverance.phando.search

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class SearchResultAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<SearchResult, AdapterClickListener, BaseViewHolder<SearchResult, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SearchResult, AdapterClickListener> {
        return SearchResultListViewHolder(inflate(R.layout.tuple_video, parent), listener)
    }


}
