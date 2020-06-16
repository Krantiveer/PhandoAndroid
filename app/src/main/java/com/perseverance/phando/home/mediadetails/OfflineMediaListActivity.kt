package com.perseverance.phando.home.mediadetails

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.patrikanews.utils.toast
import com.perseverance.phando.R
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.utils.BaseRecycleMarginDecoration
import com.perseverance.phando.utils.DialogUtils
import com.videoplayer.DownloadMetadata
import com.videoplayer.PhandoPlayerView
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.activity_offline_media.*

class OfflineMediaListActivity : AppCompatActivity(), AdapterClickListener {

    var adapter: OfflineMediaListAdapter? = null
    var downloadBroadcastReceiver: BroadcastReceiver = DownloadBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_media)
        setSupportActionBar(toolbar)
        title = "Downloads"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val manager = LinearLayoutManager(this@OfflineMediaListActivity)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val decoration = BaseRecycleMarginDecoration(this@OfflineMediaListActivity)
        recyclerView.addItemDecoration(decoration)
        adapter = OfflineMediaListAdapter(this@OfflineMediaListActivity, this)
        recyclerView.adapter = adapter
        refreshList()
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

    fun refreshList() {
        val downloadedVideoList = VideoSdkUtil.getDownloadedVideo(this.application)
        if (downloadedVideoList.isNotEmpty()) {
            adapter?.items = downloadedVideoList
        }else{
            toast("Download is empty")
            finish()
        }
    }

    override fun onItemClick(data: Any) {
        if (data is DownloadMetadata) {
            startActivity(OffLineMediaDetailActivity.getDetailIntent(this@OfflineMediaListActivity as Context, data as DownloadMetadata))
        } else {
            DialogUtils.showDialog(this@OfflineMediaListActivity, "Alert!", "Do you want to delete saved video", "Yes", "No", object : AppDialogListener {
                override fun onNegativeButtonPressed() {

                }

                override fun onPositiveButtonPressed() {
                    VideoSdkUtil.deleteDownloadedInfo(application, data as String)

                }

            })
        }
    }
    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(downloadBroadcastReceiver,
                IntentFilter("download_event"))
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(downloadBroadcastReceiver)
    }

   inner class DownloadBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent?.let {
               if (it.getBooleanExtra("refresh",false)){
                   Handler().postDelayed(Runnable {
                       refreshList()
                   },1000)

               }
            }
        }
    }
}
