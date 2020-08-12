package com.perseverance.phando.notification

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class NotificationListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<NotificationData, AdapterClickListener, BaseViewHolder<NotificationData, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<NotificationData, AdapterClickListener> {
        return NotificationListViewHolder(inflate(R.layout.item_my_list, parent), listener)
    }


}
