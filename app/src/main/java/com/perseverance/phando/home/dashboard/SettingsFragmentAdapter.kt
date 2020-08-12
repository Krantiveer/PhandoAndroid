package com.perseverance.phando.home.dashboard

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class SettingsFragmentAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<SettingsFragment.SettingItems, AdapterClickListener, BaseViewHolder<SettingsFragment.SettingItems, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SettingsFragment.SettingItems, AdapterClickListener> {
        return SettingFragmentViewHolder(inflate(R.layout.item_setting, parent), listener)
    }


}
