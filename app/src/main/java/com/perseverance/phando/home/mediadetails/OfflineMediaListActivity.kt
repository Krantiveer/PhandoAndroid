package com.perseverance.phando.home.mediadetails

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import com.perseverance.phando.BaseScreenTrackingActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.patrikanews.utils.getViewModel
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.toast
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
import com.perseverance.phando.utils.BaseRecycleMarginDecoration
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import com.videoplayer.VideoPlayerMetadata
import com.videoplayer.VideoSdkUtil
import kotlinx.android.synthetic.main.activity_offline_media.*
import kotlinx.android.synthetic.main.layout_header_new.*

class OfflineMediaListActivity : BaseScreenTrackingActivity(), AdapterClickListener {
    override var screenName="OfflineMediaList"
    var adapter: OfflineMediaListAdapter? = null
    var downloadBroadcastReceiver: BroadcastReceiver = DownloadBroadcastReceiver()
    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(this)?.downloadMetadataDao()
    }
    val mediaDetailViewModel by lazy {
        getViewModel<MediaDetailViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_media)
//        setSupportActionBar(toolbar)
//        title = "Downloads"
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)

        txtTitle.text = "Downloads"

        imgBack.setOnClickListener {
            finish()
        }
        val manager = LinearLayoutManager(this@OfflineMediaListActivity)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val decoration = BaseRecycleMarginDecoration(this@OfflineMediaListActivity)
        recyclerView.addItemDecoration(decoration)
        adapter = OfflineMediaListAdapter(this@OfflineMediaListActivity, this)
        recyclerView.adapter = adapter
        downloadMetadataDao?.getAllDownloadLiveData()?.observe(this, Observer {
            if (it.isNotEmpty()) {
                adapter?.items = it
            } else {
                toast("Download is empty")
                finish()
            }
        })

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

    override fun onItemClick(data: Any) {
        if (data is DownloadMetadata) {
            if (data.media_url.isNullOrEmpty()) {

                DialogUtils.showDialog(this@OfflineMediaListActivity, "Alert!", "Media is not downloaded. Do you want to download?", "Yes", "No", object : AppDialogListener {
                    override fun onNegativeButtonPressed() {

                    }

                    override fun onPositiveButtonPressed() {
                        if (!Utils.isNetworkAvailable(this@OfflineMediaListActivity)) {
                            toast(BaseConstants.NETWORK_ERROR)
                        } else {
                            progressBar.visible()
                            mediaDetailViewModel.getMediaUrlAndStartDownload(data.document_id).observe(this@OfflineMediaListActivity, Observer {
                                val result = it ?: return@Observer

                                when (result.status) {
                                    LoadingStatus.SUCCESS -> {
                                        val videoPlayerMetadata = VideoPlayerMetadata.UriSample(
                                                null,
                                                Uri.parse(result.data?.media_url),
                                                null,
                                                false,
                                                null,
                                                null,
                                                null,
                                                null)
                                        VideoSdkUtil.startDownload(this@OfflineMediaListActivity, videoPlayerMetadata, data?.title)
                                        downloadMetadataDao?.insert(DownloadMetadata(data?.document_id,
                                                data?.title,
                                                data?.description,
                                                data?.thumbnail,
                                                data?.media_url

                                        ))
                                        progressBar.gone()
                                    }
                                    LoadingStatus.ERROR -> {
                                        progressBar.gone()
                                        result.message?.let { it1 -> toast(it1) }
                                    }
                                }
                            })
                        }

                    }

                })
            } else {
                startActivity(OffLineMediaDetailActivity.getDetailIntent(this@OfflineMediaListActivity as Context, data as DownloadMetadata))

            }
        } else {
            DialogUtils.showDialog(this@OfflineMediaListActivity, "Alert!", "Do you want to delete saved video", "Yes", "No", object : AppDialogListener {
                override fun onNegativeButtonPressed() {

                }

                override fun onPositiveButtonPressed() {
                    VideoSdkUtil.deleteDownloadedInfo(application, data as String)
                    downloadMetadataDao?.deleteById(data)

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
                if (it.getBooleanExtra("refresh", false)) {
                    Handler().postDelayed(Runnable {
                        //  adapter?.notifyDataSetChanged()
                    }, 500)

                }
            }
        }
    }
}
