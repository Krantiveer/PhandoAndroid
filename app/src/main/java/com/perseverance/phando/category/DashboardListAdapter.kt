package com.perseverance.phando.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.phando.R
import com.perseverance.phando.home.dashboard.models.BrowseData
import com.perseverance.phando.home.dashboard.models.CategoryTab
import kotlinx.android.synthetic.main.item_dialog_list.view.*
import java.util.ArrayList

class DashboardListAdapter (
    var mContext: Context,
    var mData: ArrayList<BrowseData>,
    var mClick: AdapterClick
) : RecyclerView.Adapter<DashboardListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_list, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = mData[position].title

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
        var txtName = itemView.txtName
    }

    interface AdapterClick {
        fun onItemClick(data: BrowseData)
    }

}