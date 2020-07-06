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
import kotlinx.android.synthetic.main.tuple_grid_video_item.view.*
import kotlinx.android.synthetic.main.tuple_grid_video_item.view.duration
import kotlinx.android.synthetic.main.tuple_grid_video_item.view.free
import kotlinx.android.synthetic.main.tuple_grid_video_item.view.img_thumbnail
import kotlinx.android.synthetic.main.tuple_grid_video_item.view.txtTitle
import kotlinx.android.synthetic.main.tuple_home_video_item.view.*

class GridItemViewHolder(itemView: View, listener: AdapterClickListener,val imageOrientation: Int?=0) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

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


            itemView.img_thumbnail.resizeView(GridItemThumbnail(imageOrientation))
            Utils.displayImage(itemView.context,  if (imageOrientation==0) item.thumbnail else item.poster,
                    R.drawable.video_placeholder,
                    R.drawable.error_placeholder, itemView.img_thumbnail)
            itemView.txtTitle.text = item.title
            itemView.duration.text = item.getFormatedDuration()
            if(BuildConfig.APPLICATION_ID.equals("com.perseverance.jm31")){
                itemView.txtTitle.visible()
                itemView.duration.visible()
            }

           // itemView.invalidate()
        }
    }