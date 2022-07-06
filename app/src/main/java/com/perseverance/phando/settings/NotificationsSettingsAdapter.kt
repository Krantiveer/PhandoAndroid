package com.perseverance.phando.settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.phando.R
import com.perseverance.phando.data.NotificationsData
import com.perseverance.phando.data.NotificationsSettingsModel
import kotlinx.android.synthetic.main.item_notification_setting_list.view.*

class NotificationsSettingsAdapter(
    var mContext: Context,
    var mData: ArrayList<NotificationsData>,
    var mClick: AdapterClick,
) : RecyclerView.Adapter<NotificationsSettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_setting_list, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = mData[position].title

        holder.swValue.isChecked = mData[position].value

        holder.swValue.setOnCheckedChangeListener { buttonView, isChecked ->
            mData[position].value = isChecked
            mClick.onItemClick(mData[position])
        }

//        holder.itemView.setOnClickListener {
//            mClick.onItemClick(mData[position])
//        }

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle = itemView.txtTitle
        var swValue = itemView.swValue
    }

    interface AdapterClick {
        fun onItemClick(data: NotificationsData)
    }

}