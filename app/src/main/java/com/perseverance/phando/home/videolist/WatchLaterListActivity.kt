package com.perseverance.phando.home.videolist

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.BaseVideo
import com.perseverance.phando.db.WatchLaterVideo
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_saved_video_list.*

class WatchLaterListActivity : SavedVideoActivity() {

    private val videoListViewModelObserver = Observer<List<WatchLaterVideo>> {
        it?.let {
            if (it.isNotEmpty()) {
                val adapter = WatchLaterListAdapter(this@WatchLaterListActivity, this)
                adapter.items = it
                recycler_view_base.adapter = adapter
            }else{
                recycler_view_base.gone()
                lbl_no_video_base.text = "Watch Later not found"
                lbl_no_video_base.visible()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Watch Later"
        AppDatabase.getInstance(this@WatchLaterListActivity)?.watchLaterVideoDao()?.getWatchLaterVideos(1)?.observe(this@WatchLaterListActivity,videoListViewModelObserver)
    }

    override fun onItemClick(data: Any) {
        if (Utils.isNetworkAvailable(this@WatchLaterListActivity)) {
            startActivity(MediaDetailActivity.getDetailIntent(this@WatchLaterListActivity as Context, data as BaseVideo))
            Utils.animateActivity(this@WatchLaterListActivity, "next")
        } else {
            DialogUtils.showMessage(this@WatchLaterListActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }

}
