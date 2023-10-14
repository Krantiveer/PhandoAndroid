package com.perseverance.phando.category

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.perseverance.phando.R
import com.perseverance.phando.adapter.ListDialogAdapter
import com.perseverance.phando.home.dashboard.models.CategoryTab
import kotlinx.android.synthetic.main.item_dialog_list.view.*
import java.util.ArrayList

class CategoryListAdapter(
    var mContext: Context,
    var mData: ArrayList<CategoryTab>,
    var mClick: AdapterClick
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_list, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = mData[position].displayName


        if (mData[position].icon.isNotEmpty()){
            Glide.with(mContext)
                .load(mData[position].icon)
                .into(holder.imgIcon)
        }  else {
            Glide.with(mContext)
                .load(R.drawable.app_logo)
                .into(holder.imgIcon)
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
        var txtName = itemView.txtName
        var imgIcon = itemView.imgCatImage
    }

    interface AdapterClick {
        fun onItemClick(data: CategoryTab)
    }

}