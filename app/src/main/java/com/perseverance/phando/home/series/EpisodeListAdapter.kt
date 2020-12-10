package com.perseverance.phando.home.series

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class EpisodeListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<Episode, AdapterClickListener, BaseViewHolder<Episode, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Episode, AdapterClickListener> {
        return EpisodeListViewHolder(inflate(R.layout.item_episode, parent), listener)
    }


}
