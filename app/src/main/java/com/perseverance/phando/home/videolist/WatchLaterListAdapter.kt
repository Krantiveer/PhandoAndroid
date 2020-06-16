
package com.perseverance.phando.home.videolist

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.WatchLaterVideo
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class WatchLaterListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<WatchLaterVideo, AdapterClickListener, BaseViewHolder<WatchLaterVideo, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<WatchLaterVideo, AdapterClickListener> {
        return WatchLaterListViewHolder(inflate(R.layout.tuple_video, parent), listener)
    }


}
