package com.perseverance.phando.home.generes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.phando.R
//import com.perseverance.phando.data.NotificationsData
import com.perseverance.phando.data.NotificationsSettingsModel
import kotlinx.android.synthetic.main.item_genres.view.*
import kotlinx.android.synthetic.main.item_grid_text.view.*
import kotlinx.android.synthetic.main.item_notification_setting_list.view.*

class GenresAdapter(
    var mContext: Context,
    var mData: ArrayList<GenresResponse>,
    var mClick: AdapterClick,
) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genres, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.genresName.text = mData[position].name
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
        var genresName = itemView.genresNameText
    }

    interface AdapterClick {
        fun onItemClick(data: GenresResponse)
    }

}