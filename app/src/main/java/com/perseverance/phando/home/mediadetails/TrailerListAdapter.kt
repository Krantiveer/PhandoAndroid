
package com.perseverance.phando.home.mediadetails

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class TrailerListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<Trailer, AdapterClickListener, BaseViewHolder<Trailer, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Trailer, AdapterClickListener> {
        return TrailerListViewHolder(inflate(R.layout.item_trailer, parent), listener)
    }


}
