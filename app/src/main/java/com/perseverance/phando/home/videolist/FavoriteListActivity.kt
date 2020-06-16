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
import com.perseverance.phando.db.FavoriteVideo
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_saved_video_list.*

class FavoriteListActivity : SavedVideoActivity() {

    private val videoListViewModelObserver = Observer<List<FavoriteVideo>> {
        it?.let {
            if (it.isNotEmpty()) {
                val adapter = FavoriteListAdapter(this@FavoriteListActivity, this)
                adapter.items = it
                recycler_view_base.adapter = adapter
            }else{
                recycler_view_base.gone()
                lbl_no_video_base.text = "Favourites not found"
                lbl_no_video_base.visible()

            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Favourites"
        AppDatabase.getInstance(this@FavoriteListActivity)?.favoriteVideoDao()?.getFavoriteVideos(1)?.observe(this@FavoriteListActivity,videoListViewModelObserver)
    }

    override fun onItemClick(data: Any) {
        if (Utils.isNetworkAvailable(this@FavoriteListActivity)) {
            startActivity(MediaDetailActivity.getDetailIntent(this@FavoriteListActivity as Context, data as BaseVideo))
            Utils.animateActivity(this@FavoriteListActivity, "next")
        } else {
            DialogUtils.showMessage(this@FavoriteListActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }

}
