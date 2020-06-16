package com.perseverance.phando.home.videolist

import android.text.TextUtils
import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.db.FavoriteVideo
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.tuple_video.view.*

class FavoriteListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<FavoriteVideo, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }
    override fun onBind(item: FavoriteVideo) {
        itemView.tag = item
        if(item.isFree == 0) { // if paid video then show premium icon
            itemView.free.visible()
        }else{
            itemView.free.gone()
        }

        if (TextUtils.isEmpty(item.thumbnail)) {

            Utils.displayImage(itemView.context, Utils.createHeaderThumbnailUrl(item),
                    R.drawable.new_video_placeholder,
                    R.drawable.new_error_placeholder, itemView.img_thumbnail)
        } else {
            Utils.displayImage(itemView.context, item.thumbnail,
                    R.drawable.new_video_placeholder,
                    R.drawable.new_error_placeholder, itemView.img_thumbnail)
        }
        itemView.img_thumbnail.resizeView(ListItemThumbnail())
        itemView.txtTitle.text = item.title
        itemView.duration.text = item.formatedDuration
        MyLog.e("url------",item.toString())

    }
    }