package com.perseverance.phando.home.list

import android.text.TextUtils
import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemCircularThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.tuple_home_circular_video_item.view.*

class HomeFragmentCategoryListCircularViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }
    override fun onBind(item: Video) {
        itemView.tag = item
        if(item.isFree == 0) { // if paid video then show premium icon
            itemView.free.visible()
        }else{
            itemView.free.gone()
        }

        if (TextUtils.isEmpty(item.thumbnail)) {
            // itemView.img_thumbnail.resizeView(ListItemThumbnail())
            Utils.displayCircularImage(itemView.context, Utils.createHeaderThumbnailUrl(item),
                    R.drawable.ic_circular_play_placeholder,
                    R.drawable.ic_circular_play_placeholder, itemView.img_thumbnail)
        } else {
            // itemView.img_thumbnail.resizeView(ListItemThumbnail())
            Utils.displayCircularImage(itemView.context, item.thumbnail,
                    R.drawable.ic_circular_play_placeholder,
                    R.drawable.ic_circular_play_placeholder, itemView.img_thumbnail)
        }
        itemView.img_thumbnail.resizeView(ListItemCircularThumbnail())

        itemView.txtTitle.text = item.title
        itemView.duration.text = item.formatedDuration

        // itemView.invalidate()
    }
    }