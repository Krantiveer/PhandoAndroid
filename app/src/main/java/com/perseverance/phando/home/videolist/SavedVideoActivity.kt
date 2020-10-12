package com.perseverance.phando.home.videolist

import android.os.Bundle
import android.view.MenuItem
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.utils.BaseRecycleMarginDecoration
import kotlinx.android.synthetic.main.activity_saved_video_list.*

abstract class SavedVideoActivity : BaseScreenTrackingActivity(), AdapterClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_video_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val manager = GridLayoutManager(this@SavedVideoActivity, 2)
        recycler_view_base.layoutManager = manager
        recycler_view_base.setHasFixedSize(true)
        val decoration = BaseRecycleMarginDecoration(this@SavedVideoActivity)
        recycler_view_base.addItemDecoration(decoration)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
