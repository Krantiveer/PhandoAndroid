
package com.perseverance.phando.home.list

import android.content.Context
import android.view.ViewGroup
import com.perseverance.phando.R
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.genericAdopter.GenericRecyclerViewAdapter


class HomeFragmentChildListAdapter(context: Context, listener: AdapterClickListener, val displayType: String, val imageOrientation: Int?=0) : GenericRecyclerViewAdapter<Video, AdapterClickListener, BaseViewHolder<Video, AdapterClickListener>>(context, listener) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Video, AdapterClickListener> {


        when(displayType){

            "CIRCULAR_GRID" ->{

                return HomeFragmentCategoryListCircularViewHolder(inflate(R.layout.tuple_home_circular_video_item, parent), listener)
            }
            "GRID" ->{
                return HomeFragmentHorizontalListItemViewHolder(inflate(R.layout.tuple_home_video_item, parent), listener,imageOrientation)
            }
            "List" ->{

                return HomeFragmentVerticalListItemViewHolder(inflate(R.layout.tuple_home_video_item_list_style, parent), listener,imageOrientation)
            }
            "CONTINUE_WATCHING" ->{
                return HomeFragmentHorizontalListItemViewHolder(inflate(R.layout.tuple_home_video_item, parent), listener, imageOrientation)
             //   return HomeFragmentContinueWatchingItemViewHolder(inflate(R.layout.tuple_home_video_item_list_style, parent), listener)
            }
            else ->{
                return HomeFragmentHorizontalListItemViewHolder(inflate(R.layout.tuple_home_video_item, parent), listener, imageOrientation)
            }
        }


    }


}
