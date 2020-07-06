
package com.perseverance.phando.home.videolist

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class BaseCategoryListAdapter(context: Context, listener: AdapterClickListener,val imageOrientation: Int?=0) : GenericRecyclerViewAdapter<Video, AdapterClickListener, BaseViewHolder<Video, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Video, AdapterClickListener> {
        return GridItemViewHolder(inflate(R.layout.tuple_grid_video_item, parent), listener,imageOrientation)
    }


}
