package com.perseverance.phando.home.series

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_episode.view.*

/*
created by @alokipandey for QAIT on 02/12/2020
*/

class TrailerListAdapter(context: Context, listener: AdapterClickListener) :
        GenericRecyclerViewAdapter<TrailerX, AdapterClickListener,
                BaseViewHolder<TrailerX, AdapterClickListener>>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TrailerX, AdapterClickListener> {
        return TrailerListViewHolder(inflate(R.layout.item_episode, parent), listener)
    }
}

class TrailerListViewHolder(itemView: View, listener: AdapterClickListener) :
        BaseViewHolder<TrailerX, AdapterClickListener>(itemView, listener) {
    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }
    }

    override fun onBind(item: TrailerX) {
        itemView.tag = item
        itemView.free.gone()
//        if (item.is_free == 1) { // if paid video then show premium icon
//            itemView.free.gone()
//        } else {
//            itemView.free.visible()
//        }
        itemView.img_thumbnail.resizeView(ListItemThumbnail(), true)
        Utils.displayImage(itemView.context, item.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.video_placeholder, itemView.img_thumbnail)

        itemView.title.text = item.title
        itemView.title.visible()
        itemView.rating.gone()
        item.duration_str?.let {
            itemView.duration.text="($it)"
        }?: itemView.duration.gone()
    }
}