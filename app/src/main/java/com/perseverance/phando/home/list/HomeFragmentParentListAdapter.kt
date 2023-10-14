package com.perseverance.phando.home.list

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter
import com.perseverance.phando.home.dashboard.models.BrowseData


class HomeFragmentParentListAdapter(context: Context, listener: AdapterClickListener) :
    GenericRecyclerViewAdapter<BrowseData, AdapterClickListener, BaseViewHolder<BrowseData,
            AdapterClickListener>>(context, listener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<BrowseData, AdapterClickListener> {
        return HomeFragmentParentListItemViewHolder(inflate(R.layout.home_widget, parent), listener)
    }

}

