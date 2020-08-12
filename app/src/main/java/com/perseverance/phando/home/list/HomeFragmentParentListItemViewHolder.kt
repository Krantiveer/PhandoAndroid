package com.perseverance.phando.home.list

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.home.dashboard.models.BrowseData
import kotlinx.android.synthetic.main.home_widget.view.*

open class HomeFragmentParentListItemViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<BrowseData, AdapterClickListener>(itemView, listener) {

    init {
        itemView.txt_see_all.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: BrowseData) {

        itemView.txt_title.tag = item
        itemView.txt_see_all.tag = item
        //itemView.home_widget.setBackgroundColor(itemView.resources.getColor(R.color.black))
        val videos = item.list
        itemView.txt_title.text = item.title
        val linearLayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        itemView.recyclerview_home_items.layoutManager = linearLayoutManager
        val recyclerAdapter = HomeFragmentChildListAdapter(itemView.context, listener, item.displayType, item.image_orientation)
        recyclerAdapter.items = videos as MutableList<Video>
        itemView.recyclerview_home_items.adapter = recyclerAdapter

        if (item.list.size > 3 && item.id != 0) {
            itemView.txt_see_all.visible()
        } else {
            itemView.txt_see_all.gone()
        }


    }

}