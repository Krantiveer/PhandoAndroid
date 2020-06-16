package com.perseverance.phando.home.mediadetails

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
import com.videoplayer.DownloadMetadata
import kotlinx.android.synthetic.main.tuple_grid_video_item.view.*

class SavedMediaItemViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<DownloadMetadata, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: DownloadMetadata) {
        itemView.tag = item
        itemView.free.gone()
        itemView.img_thumbnail.resizeView(GridItemThumbnail())
        Utils.displayImage(itemView.context, item.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.error_placeholder, itemView.img_thumbnail)
        itemView.txtTitle.text = item.title
    }
}