package com.perseverance.phando.notification

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.BaseVideo
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.videolist.BaseListActivity
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.PreferencesUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_saved_video_list.*

class NotificationListActivity : BaseListActivity() {

    private val videoListViewModelObserver = Observer<List<NotificationData>> {
        it?.let {
            if (it.isNotEmpty()) {
                val adapter = NotificationListAdapter(this@NotificationListActivity, this)
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
        title = "Notifications"
        manager = LinearLayoutManager(this@NotificationListActivity)
        recycler_view_base.layoutManager=manager
        AppDatabase.getInstance(this@NotificationListActivity)?.notificationDao()?.getNotifications()?.observe(this@NotificationListActivity,videoListViewModelObserver)
        PreferencesUtils.saveIntegerPreferences("NOTIFICATION_COUNT", 0)
    }

    override fun onItemClick(data: Any) {
        if (Utils.isNetworkAvailable(this@NotificationListActivity)) {
            startActivity(MediaDetailActivity.getDetailIntent(this@NotificationListActivity as Context, data as BaseVideo))
            Utils.animateActivity(this@NotificationListActivity, "next")
        } else {
            DialogUtils.showMessage(this@NotificationListActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }

}
