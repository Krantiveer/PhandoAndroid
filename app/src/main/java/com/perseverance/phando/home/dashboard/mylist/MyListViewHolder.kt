package com.perseverance.phando.home.dashboard.mylist

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.invisible
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_my_list.view.*


class MyListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<Video, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(video: Video) {
        itemView.tag = video
        Utils.displayImage(itemView.context, video.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.video_placeholder, itemView.img_thumbnail)
        itemView.img_thumbnail.resizeView(ListItemThumbnail(), true)
        itemView.title.text = video.title

        video.rating?.let {
            itemView.rating.text = it.toString()
        } ?: itemView.rating.gone()


        itemView.details.text = video.description
        itemView.option.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(itemView.context, R.style.popup_option)
            val popup = PopupMenu(wrapper, itemView.option)
            popup.inflate(R.menu.menu_my_list_options)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    return when (item?.itemId) {
                        R.id.menu_delete -> {
                            listener.onItemClick("${video.id},${video.type}")
                            true
                        }

                        else -> false
                    }
                }

            })
            popup.show()
        }
    }
}