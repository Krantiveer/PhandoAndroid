package com.perseverance.phando.home.list

import android.view.View
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.invisible
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*

class HomeFragmentHorizontalListItemViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

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

            itemView.img_thumbnail.resizeView(ListItemThumbnail())
            Utils.displayImage(itemView.context, item.thumbnail,
                    R.drawable.video_placeholder,
                    R.drawable.error_placeholder, itemView.img_thumbnail)
            itemView.txtTitle.text = item.title
           // itemView.duration.text = item.formatedDuration
            if(BuildConfig.APPLICATION_ID.equals("com.perseverance.jm31")){
                itemView.txtTitle.visible()
                itemView.duration.visible()
            }
            val duration :Int = item.duration
            val lastWatchTime = item.lastWatchTime

            lastWatchTime?.let {
                if (it>0){
                    try {
                        val progress : Int = (it*100) / duration
                        itemView.watchingProgress.progress = progress
                        itemView.watchingProgress.visible()
                    } catch (e: Exception) {
                    }
                }else{
                    itemView.watchingProgress.invisible()
                }
            }?:itemView.watchingProgress.invisible()

           // itemView.invalidate()
        }
    }