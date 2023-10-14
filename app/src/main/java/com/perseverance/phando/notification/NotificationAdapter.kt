package com.perseverance.phando.notification

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.resizeView
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.resize.ListItemThumbnail
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_my_list_notification.view.*

class NotificationAdapter(
    var mContext: Context,
    var mData: ArrayList<NotificationData>,
    var mClick: AdapterClick,
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_list_notification, parent, false)
        return ViewHolder(v)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mData[position].free == 0) {
            holder.free.gone()
        } else {
            holder.free.visible()
        }

        Log.e("@@", mData[position].thumbnail.toString())
      /*  Utils.displayImage(mContext, mData[position].thumbnail,
            R.drawable.new_video_placeholder,
            R.drawable.new_error_placeholder, holder.img_thumbnail)
*/

        Glide.with(mContext)
            .load(mData[position].thumbnail)
            .into(holder.img_thumbnail)

        holder.img_thumbnail.resizeView(ListItemThumbnail(), true)
        holder.title.text = mData[position].title

        /*video.rating?.let {
            itemView.rating.text = it.toString()
        } ?:*/ holder.rating.gone()

//
//        video.duration?.let {
//            itemView.duration.text = video.getFormatedDuration()
//        }?:itemView.duration.invisible()

        holder.details.text = mData[position].detail
        holder.option.setOnClickListener {
            val wrapper: Context = ContextThemeWrapper(mContext, R.style.popup_option)
            val popup = PopupMenu(wrapper, holder.option)
            popup.inflate(R.menu.menu_my_list_options)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {

                    return when (item?.itemId) {
                        R.id.menu_delete -> {
                          //  listener.onItemClick(mData[position].id)
                            true
                        }

                        else -> false
                    }
                }

            })
            popup.show()
        }
        holder.itemView.setOnClickListener {
            mClick.onItemClick(mData[position])
        }

    }
    override fun getItemCount(): Int {
        return mData.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_thumbnail = itemView.img_thumbnail
        var free = itemView.free
        var dateTime = itemView.dateTime
        var play = itemView.play
        var option = itemView.option
        var title = itemView.title
        var rating = itemView.rating
        var details = itemView.details
        var durationTime = itemView.durationTime
    }
    interface AdapterClick {
        fun onItemClick(data: NotificationData)
    }

}