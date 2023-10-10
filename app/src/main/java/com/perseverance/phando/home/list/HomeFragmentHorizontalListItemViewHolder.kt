package com.perseverance.phando.home.list

import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.invisible
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.GridItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*

class HomeFragmentHorizontalListItemViewHolder(
    itemView: View,
    listener: AdapterClickListener,
    val imageOrientation: Int? = 0
) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: Video) {
        itemView.tag = item
        if (item.is_free == 1) { // if paid video then show premium icon
            itemView.free.gone()
        } else {
            itemView.free.visible()
        }

        itemView.img_thumbnail.resizeView(GridItemThumbnail(imageOrientation))
        Utils.displayImage(
            itemView.context,
            if (imageOrientation == 0) item.thumbnail else item.thumbnail,
            R.drawable.video_placeholder,
            R.drawable.error_placeholder,
            itemView.img_thumbnail
        )
        itemView.txtTitle.text = item.title

        /* item.rating?.let {
             itemView.rating.visible()
             itemView.rating.text = decimalFormat(item.rating!!)
         }?:*/
        itemView.rating.gone()
//         itemView.duration.text = item.formatedDuration
        itemView.txtTitle.visible()
        item.duration?.let {
            itemView.duration.visible()
            itemView.duration.text = item.getFormatedDuration()
        }
        val duration: Int? = item.duration

        val lastWatchTime = item.last_watch_time

        lastWatchTime?.let {
            if (it > 0) {
                try {
                    val progress: Int = (it * 100) / duration!!
                    itemView.watchingProgress.progress = progress
                    itemView.watchingProgress.visible()
                } catch (e: Exception) {
                }
            } else {
                itemView.watchingProgress.invisible()
            }
        } ?: itemView.watchingProgress.invisible()

        // itemView.invalidate()
    }
}