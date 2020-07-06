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
import kotlinx.android.synthetic.main.tuple_home_circular_video_item.view.duration
import kotlinx.android.synthetic.main.tuple_home_circular_video_item.view.free
import kotlinx.android.synthetic.main.tuple_home_circular_video_item.view.img_thumbnail
import kotlinx.android.synthetic.main.tuple_home_circular_video_item.view.txtTitle
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*

class HomeFragmentCategoryListCircularViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }
    override fun onBind(item: Video) {
        itemView.tag = item
        if(item.is_free == 1) { // if paid video then show premium icon
            itemView.free.gone()
        }else{
            itemView.free.visible()
        }

        Utils.displayImage(itemView.context, item.circular_thumbnail,
                R.drawable.ic_circular_play_placeholder,
                R.drawable.ic_circular_play_placeholder, itemView.img_thumbnail)
        itemView.img_thumbnail.resizeView(ListItemCircularThumbnail())

        itemView.txtTitle.text = item.title
        itemView.duration.text = item.getFormatedDuration()

    }
    }