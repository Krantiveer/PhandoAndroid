package com.perseverance.phando.search

import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.tuple_video.view.*

class SearchResultListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<SearchResult, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: SearchResult) {
        itemView.tag = item
        if (item.is_free == 0) { // if paid video then show premium icon
            itemView.free.visible()
        } else {
            itemView.free.gone()
        }

        Utils.displayImage(itemView.context, item.thumbnail,
                R.drawable.new_video_placeholder,
                R.drawable.new_error_placeholder, itemView.img_thumbnail)
        itemView.img_thumbnail.resizeView(ListItemThumbnail())
        itemView.txtTitle.text = item.title
        //itemView.duration.text = item.formatedDuration


    }
}