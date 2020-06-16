package com.perseverance.phando.home.mediadetails

import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_trailer.view.*

class TrailerListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Trailer, AdapterClickListener>(itemView, listener) {

        init {
            itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

        }
        override fun onBind(item: Trailer) {
            itemView.tag = item
            itemView.free.gone()
            itemView.img_thumbnail.resizeView(ListItemThumbnail())
            Utils.displayImage(itemView.context, item.thumbnail,
                    R.drawable.video_placeholder,
                    R.drawable.error_placeholder, itemView.img_thumbnail)

            if (item.isSelected) {
                itemView.containerTrailer.setBackgroundColor(itemView.context.resources.getColor(R.color.white))
            }else{
                itemView.containerTrailer.setBackgroundColor(itemView.context.resources.getColor(R.color.bg_app))
            }
        }
    }