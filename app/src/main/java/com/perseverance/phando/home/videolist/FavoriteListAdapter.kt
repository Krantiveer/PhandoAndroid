
package com.perseverance.phando.home.videolist

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.FavoriteVideo
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class FavoriteListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<FavoriteVideo, AdapterClickListener, BaseViewHolder<FavoriteVideo, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<FavoriteVideo, AdapterClickListener> {
        return FavoriteListViewHolder(inflate(R.layout.tuple_video, parent), listener)
    }


}
