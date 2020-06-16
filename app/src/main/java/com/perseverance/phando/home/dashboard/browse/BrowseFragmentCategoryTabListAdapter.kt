package com.perseverance.phando.home.dashboard.browse

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter
import com.perseverance.phando.home.dashboard.models.CategoryTab


class BrowseFragmentCategoryTabListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<CategoryTab, AdapterClickListener, BaseViewHolder<CategoryTab, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<CategoryTab, AdapterClickListener> {

        return BrowseFragmentCategoryTabViewHolder(inflate(R.layout.item_home_tab, parent), listener)
    }


}
