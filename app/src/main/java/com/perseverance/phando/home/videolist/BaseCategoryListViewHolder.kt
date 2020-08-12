package com.perseverance.phando.home.videolist

import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.GridItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*
import kotlinx.android.synthetic.main.tuple_video.view.*
import kotlinx.android.synthetic.main.tuple_video.view.duration
import kotlinx.android.synthetic.main.tuple_video.view.free
import kotlinx.android.synthetic.main.tuple_video.view.img_thumbnail
import kotlinx.android.synthetic.main.tuple_video.view.txtTitle

class BaseCategoryListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

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

        itemView.img_thumbnail.resizeView(GridItemThumbnail())
        Utils.displayImage(itemView.context, item.thumbnail,
                R.drawable.new_video_placeholder,
                R.drawable.new_error_placeholder, itemView.img_thumbnail)


        itemView.txtTitle.text = item.title
        itemView.duration.text = item.getFormatedDuration()
        if (BuildConfig.APPLICATION_ID.equals("com.perseverance.jm31")) {
            itemView.txtTitle.visible()
            itemView.duration.visible()
            //itemView.txtTitle.setTextColor(itemView.context.resources.getColor(R.color.black))
        }


    }
}