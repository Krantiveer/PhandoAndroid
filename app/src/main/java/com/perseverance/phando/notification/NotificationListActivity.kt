package com.perseverance.phando.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseListActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.fragment_base.*

class NotificationListActivity : BaseListActivity() {

    override var screenName =BaseConstants.NOTIFICATION_LIST_SCREEN
    private val notificationDao by lazy {
        AppDatabase.getInstance(this@NotificationListActivity)?.notificationDao()
    }

    private val videoListViewModelObserver = Observer<List<NotificationData>> {
        it?.let {
            if (it.isNotEmpty()) {
                val adapter = NotificationListAdapter(this@NotificationListActivity, this)
                adapter.items = it
                rv_season_episodes.adapter = adapter
            } else {
                rv_season_episodes.gone()
                lbl_no_video_base.text = "Notifications not found"
                lbl_no_video_base.visible()

            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Notifications"
        swipetorefresh_base.isEnabled = false
        manager = LinearLayoutManager(this@NotificationListActivity)
        rv_season_episodes.layoutManager = manager
        notificationDao?.getNotifications()?.observe(this@NotificationListActivity, videoListViewModelObserver)
        notificationDao?.markAllNotificationRead()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_notification, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(item.itemId==R.id.action_deleteAll){
//            notificationDao?.deleteAllNotifications()
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onItemClick(data: Any) {
        when (data) {
            is NotificationData -> {
                if (Utils.isNetworkAvailable(this@NotificationListActivity)) {
                    val baseVideo = Video()
                    baseVideo.id = data.id
                    baseVideo.thumbnail = data.thumbnail
                    baseVideo.title = data.title
                    baseVideo.type = data.type
                    baseVideo.description = data.detail
                    baseVideo.rating = data.rating
                    baseVideo.is_free = data.is_free
                    if ("T".equals(data.type)) {
                        val intent = Intent(this@NotificationListActivity, SeriesActivity::class.java)
                        intent.putExtra(Key.CATEGORY, baseVideo)
                        startActivity(intent)
                    } else {
                        startActivity(MediaDetailActivity.getDetailIntent(this@NotificationListActivity as Context, baseVideo))
                        Utils.animateActivity(this@NotificationListActivity, "next")
                    }
                } else {
                    DialogUtils.showMessage(this@NotificationListActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                }
            }
            is Long -> {
                notificationDao?.deleteNotifications(data)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        notificationDao?.markAllNotificationRead()
    }

}
