package com.perseverance.phando.home.dashboard.browse

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.genericAdopter.BaseViewHolder
import com.perseverance.phando.home.dashboard.models.CategoryTab
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.item_home_tab.view.*


class BrowseFragmentCategoryTabViewHolder(itemView: View, listener: AdapterClickListener) : BaseViewHolder<CategoryTab, AdapterClickListener>(itemView, listener) {
    var padding = 5

    init {
        padding = Utils.dpToPixel(itemView.context, itemView.context.getResources().getDimension(com.intuit.sdp.R.dimen._3sdp)).toInt()
        itemView.type.setOnClickListener {
            var categoryTab = it.tag as CategoryTab
            categoryTab.isFilter = false
            listener.onItemClick(categoryTab)
        }
        itemView.filter.setOnClickListener {
            var categoryTab = it.tag as CategoryTab
            categoryTab.isFilter = true
            listener.onItemClick(categoryTab)
        }
    }

    override fun onBind(item: CategoryTab) {
        itemView.type.tag = item
        itemView.filter.tag = item
        if (item.show) {
            itemView.type.visible()
            itemView.type.text = item.displayName
            if (item.showFilter) {
                if (item.filters.isNotEmpty()) {
                    itemView.filter.visible()
                    val selected = item.filters.filter {
                        it.isSelected == true
                    }
                    if (selected.isNotEmpty()) {
                        itemView.filter.text = selected.get(0).name
                        itemView.filter.setPadding(padding, 0, 0, 0);
                    }
                }


            } else {
                itemView.filter.gone()
            }
            if (adapterPosition == 0) {
                // itemView.type.setPadding(0,0,50,0);
            } else {
                itemView.type.setPadding(padding, 0, 0, 0);
            }
        } else {
            itemView.type.gone()
        }
        if (item.showFilter) {
            setScaleAnimation(itemView)
        }
    }

    private fun setScaleAnimation(view: View) {
        //val anim: Animation = ScaleAnimation(-1.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
        val anim: Animation = AnimationUtils.loadAnimation(view.context, R.anim.item_animation_fall_down) // Pivot point of Y scaling
        view.startAnimation(anim)
    }
}