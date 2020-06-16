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
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*

class EpisodeListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Episode, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: Episode) {
        itemView.tag = item
        if (item.is_free == 0) { // if paid video then show premium icon
            itemView.free.visible()
        }else{
            itemView.free.gone()
        }
        itemView.img_thumbnail.resizeView(ListItemThumbnail())
        Utils.displayImage(itemView.context, item.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.video_placeholder, itemView.img_thumbnail)


        itemView.txtTitle.text = item.title
        itemView.txtTitle.visible()
    }
}