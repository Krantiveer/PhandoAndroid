package com.perseverance.phando.home.mediadetails

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class RelatedEpisodeListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<RelatedEpisode, AdapterClickListener, BaseViewHolder<RelatedEpisode, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RelatedEpisode, AdapterClickListener> {
        return RelatedEpisodeListViewHolder(inflate(R.layout.item_trailer, parent), listener)
    }


}
