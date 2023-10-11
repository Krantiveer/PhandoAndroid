package com.perseverance.phando.home.list

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.perseverance.patrikanews.utils.*
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.GridItemThumbnail
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.genre_item_images.view.*


class GenreItemViewHolder(
    itemView: View,
    listener: AdapterClickListener,
    val imageOrientation: Int? = 0,
) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: Video) {
        itemView.tag = item


        itemView.tvTitle.text = item.title
     //   itemView.img_thumbnail.resizeView(ListItemThumbnail(imageOrientation))


        if (imageOrientation == 1) {


           /* Utils.displayImage(
                itemView.context,
                if (item.poster.isNullOrEmpty()) item.thumbnail else item.poster,
                R.drawable.video_placeholder,
                R.drawable.error_placeholder,
                itemView.img_thumbnail
            )*/

        }
        else {

          /*  Utils.displayImage(
                itemView.context,
                item.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.error_placeholder,
                itemView.img_thumbnail
            )*/
        }


        // itemView.invalidate()
    }
}