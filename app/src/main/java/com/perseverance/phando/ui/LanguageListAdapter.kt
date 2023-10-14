package com.perseverance.phando.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.db.Language
import kotlinx.android.synthetic.main.item_language_list.view.*

class LanguageListAdapter(
    var mContext: Context,
    var mData: ArrayList<Language>,
    var mClick: AdapterClick,
) : RecyclerView.Adapter<LanguageListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_language_list, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = mData[position].language

        if (mData[position].language_text != null){
            holder.txtNameOriginal.text = mData[position].language_text
        }

        if (mData[position].isLanguageSelected ) {
            holder.imgChecked.visible()

        } else {
            holder.imgChecked.gone()
        }


        holder.itemView.setOnClickListener {
            mData[position].isLanguageSelected = !mData[position].isLanguageSelected
            //  mData[position].isSelected = !mData[position].isSelected
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
        var txtName = itemView.txtName
        var txtNameOriginal = itemView.txtNameOriginal
        var imgChecked = itemView.imgChecked
    }

    interface AdapterClick {
        fun onItemClick(data: Language)
    }

}