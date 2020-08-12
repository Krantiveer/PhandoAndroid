package com.perseverance.phando.home.series

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import kotlinx.android.synthetic.main.home_widget.view.*

class SeriesListViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<SeriesData, AdapterClickListener>(itemView, listener) {

    init {
        //itemView.txt_title.setOnClickListener { v -> listener.onItemClick(v.tag) }
        //itemView.txt_see_all.setOnClickListener { v -> listener.onItemClick(v.tag) }

    }

    override fun onBind(item: SeriesData) {
        itemView.txt_see_all.gone()
        itemView.txt_title.tag = item
        itemView.txt_see_all.tag = item
        // itemView.home_widget.setBackgroundColor(itemView.resources.getColor(R.color.black))
        if (BuildConfig.APPLICATION_ID.equals("com.perseverance.jm31")) {
            if (item.episodes.isEmpty()) {
                itemView.txt_title.text = "No data found"

            } else {
                itemView.txt_title.text = "Chapter " + item.season_no + " - " + item.season_title

            }
        } else {
            if (item.episodes.isEmpty()) {
                itemView.txt_title.text = "No Episodes found"

            } else {
                itemView.txt_title.text = item.title

            }
        }

        val linearLayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        itemView.recyclerview_home_items.layoutManager = linearLayoutManager
        val recyclerAdapter = EpisodeListAdapter(itemView.context, listener)
        recyclerAdapter.items = item.episodes
        itemView.recyclerview_home_items.adapter = recyclerAdapter


    }
}