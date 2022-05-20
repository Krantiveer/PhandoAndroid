package com.perseverance.phando.notification

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_my_list.view.*

class NotificationListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<NotificationData, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(video: NotificationData) {
        itemView.tag = video
        if (video.free == 0) { // if paid video then show premium icon
            itemView.free.visible()
        } else {
            itemView.free.gone()
        }
        Utils.displayImage(itemView.context, video.thumbnail,
                R.drawable.new_video_placeholder,
                R.drawable.new_error_placeholder, itemView.img_thumbnail)

        itemView.img_thumbnail.resizeView(ListItemThumbnail(), true)
        itemView.title.text = video.title

        video.rating?.let {
            itemView.rating.text = it.toString()
        } ?: itemView.rating.gone()

//
//        video.duration?.let {
//            itemView.duration.text = video.getFormatedDuration()
//        }?:itemView.duration.invisible()

        itemView.details.text = video.description
        itemView.option.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(itemView.context, R.style.popup_option)
            val popup = PopupMenu(wrapper, itemView.option)
            popup.inflate(R.menu.menu_my_list_options)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    return when (item?.itemId) {
                        R.id.menu_delete -> {
                            listener.onItemClick(video.dbID)
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