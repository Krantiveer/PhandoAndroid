package com.perseverance.phando.home.dashboard.mylist

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class MyListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<Video, AdapterClickListener, BaseViewHolder<Video, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Video, AdapterClickListener> {
        return MyListViewHolder(inflate(R.layout.item_my_list, parent), listener)
    }


}
