package com.perseverance.phando.home.mediadetails

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_my_list.view.*


class OfflineMediaListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<DownloadMetadata, AdapterClickListener>(itemView, listener) {

    init {
        itemView.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(downloadMetadata: DownloadMetadata) {
        itemView.tag = downloadMetadata
        Utils.displayImage(itemView.context, downloadMetadata.thumbnail,
                R.drawable.video_placeholder,
                R.drawable.video_placeholder, itemView.img_thumbnail)
        itemView.img_thumbnail.resizeView(ListItemThumbnail(), true)
        itemView.title.text = downloadMetadata.title
        itemView.details.text = downloadMetadata.description
        itemView.rating.gone()
        itemView.option.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(itemView.context, R.style.popup_option)
            val popup = PopupMenu(wrapper, itemView.option)
            popup.inflate(R.menu.menu_my_list_options)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    return when (item?.itemId) {
                        R.id.menu_delete -> {
                            listener.onItemClick(downloadMetadata.document_id)
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