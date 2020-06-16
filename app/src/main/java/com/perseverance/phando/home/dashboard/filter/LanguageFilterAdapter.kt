
package com.perseverance.phando.home.dashboard.filter

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.Language
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class LanguageFilterAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<Language, AdapterClickListener, BaseViewHolder<Language, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Language, AdapterClickListener> {
        return LanguageFilterViewHolder(inflate(R.layout.item_filter, parent), listener)
    }


}
