
package com.perseverance.phando.home.mediadetails

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata


class SavedMediaListAdapter(context: Context, listener: AdapterClickListener) : GenericRecyclerViewAdapter<DownloadMetadata, AdapterClickListener, BaseViewHolder<DownloadMetadata, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<DownloadMetadata, AdapterClickListener> {
        return SavedMediaItemViewHolder(inflate(R.layout.tuple_grid_video_item, parent), listener)
    }


}
