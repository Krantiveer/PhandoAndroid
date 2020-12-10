package com.perseverance.phando.home.series

import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_episode.view.*

class EpisodeListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Episode, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }
    }

    override fun onBind(item: Episode) {
        itemView.tag = item
        if (item.is_free == 1) { // if paid video then show premium icon
            itemView.free.gone()
        } else {
            itemView.free.visible()
        }
        itemView.img_thumbnail.resizeView(ListItemThumbnail(), true)
        Utils.displayImage(itemView.context, item.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.video_placeholder, itemView.img_thumbnail)

        itemView.title.text = item.title
        itemView.title.visible()
        itemView.rating.gone()
        itemView.details.text = item.detail
        item.duration_str?.let {
            itemView.duration.text="($it)"
        }?: itemView.duration.gone()

    }
}