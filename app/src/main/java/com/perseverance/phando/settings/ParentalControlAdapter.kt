package com.perseverance.phando.settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.phando.R
import com.perseverance.phando.data.MaturityRating
import kotlinx.android.synthetic.main.item_parental_list.view.*

class ParentalControlAdapter(
    var mContext: Context,
    var mData: ArrayList<MaturityRating>,
    var mClick: AdapterClick,
) : RecyclerView.Adapter<ParentalControlAdapter.ViewHolder>() {

    var isViewDisabled = false

    @SuppressLint("NotifyDataSetChanged")
    fun setIsViewDisabled(boolean: Boolean) {
        isViewDisabled = boolean
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parental_list, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = mData[position].setting_name

        if (!isViewDisabled) {
            holder.llMain.isClickable = true
            holder.llMain.isEnabled = true
            holder.llChild.setBackgroundResource(R.drawable.btn_enable)
            if (mData[position].setting_value == 1) {
                holder.swValue.setImageDrawable(mContext.getDrawable(R.drawable.ic_check))
            } else {
                holder.swValue.setImageDrawable(mContext.getDrawable(R.drawable.ic_uncheck))
            }
        } else {
            holder.llMain.isClickable = false
            holder.llMain.isEnabled = false
            holder.llChild.setBackgroundResource(R.drawable.btn_disable)
            holder.swValue.setImageDrawable(mContext.getDrawable(R.drawable.ic_uncheck))
        }

//        if (mData[position].setting_value == 1) {
//            holder.swValue.setImageDrawable(mContext.getDrawable(R.drawable.ic_check))
//        } else {
//            holder.swValue.setImageDrawable(mContext.getDrawable(R.drawable.ic_uncheck))
//        }
        holder.itemView.setOnClickListener {
            if (mData[position].setting_value == 1) {
                mData[position].setting_value = 0
            } else {
                mData[position].setting_value = 1
            }
            mClick.onItemClick(mData[position])
            notifyItemChanged(position)
        }
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
        var llMain = itemView.llMain
        var llChild = itemView.llChild
    }

    interface AdapterClick {
        fun onItemClick(data: MaturityRating)
    }

}