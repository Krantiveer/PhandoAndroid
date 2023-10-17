package com.perseverance.phando.home.videolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.Category
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.MediaListViewModel
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.ui.WaitingDialog
import com.perseverance.phando.utils.*
import com.perseverance.phando.videoplayer.VideosModel
import kotlinx.android.synthetic.main.fragment_base.*
import kotlinx.android.synthetic.main.layout_header_new.*

class BaseVideoListActivity : BaseScreenTrackingActivity(), SwipeRefreshLayout.OnRefreshListener,
    AdapterClickListener {

    override var screenName = BaseConstants.VIDEO_CATEGORY_SCREEN
    private var waitingDialog: WaitingDialog? = null
    private lateinit var id: String
    private lateinit var title: String
    private lateinit var type: String
    private var imageOrientation: Int = 0
    private var adapter: BaseCategoryListAdapter? = null
    private var endlessScrollListener: EndlessScrollListener? = null
    private var pCount: Int = 0
    private lateinit var homeViewModel: MediaListViewModel

    private val videoListViewModelObserver = Observer<VideosModel> { videoModel ->
        if (videoModel!!.throwable == null) {
            onGetVideosSuccess(videoModel.videos,
                BaseConstants.Video.CATEGORY,
                videoModel.pageCount,
                videoModel.category)
        } else {
            onGetVideosError(Utils.getErrorMessage(videoModel.throwable),
                BaseConstants.Video.CATEGORY,
                videoModel.pageCount,
                videoModel.category)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_list)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)

        id = intent.getStringExtra("id") ?: ""
        title = intent.getStringExtra("title") ?: ""
        type = intent.getStringExtra("type")?:""

        imageOrientation = intent.getIntExtra("imageOrientation", 0)
//        setTitle(title)

        txtTitle.text = title

        imgBack.setOnClickListener {
            finish()
        }
        homeViewModel = ViewModelProviders.of(this).get(MediaListViewModel::class.java)

        homeViewModel.videoListMutableLiveData.observe(this, videoListViewModelObserver)

        val manager = GridLayoutManager(this@BaseVideoListActivity, 2)
        rv_season_episodes.layoutManager = manager
        rv_season_episodes.setHasFixedSize(true)
        /* val decoration = BaseRecycleMarginDecoration(this@BaseVideoListActivity)
         rv_season_episodes.addItemDecoration(decoration)*/
        if (imageOrientation.equals("vertical") ||imageOrientation.equals("1")){
            adapter = BaseCategoryListAdapter(this@BaseVideoListActivity, this, 1)

        }  else {
            adapter = BaseCategoryListAdapter(this@BaseVideoListActivity, this, 0)

        }
        val videos = ArrayList<Video>()
        adapter?.items = videos
        rv_season_episodes.adapter = adapter
        endlessScrollListener = object : EndlessScrollListener(manager) {
            override fun onLoadMore(currentPage: Int) {
                pCount = currentPage
                footer_progress_base.visibility = View.VISIBLE
                loadVideos(pCount, false)
            }
        }
        swipetorefresh_base.setOnRefreshListener(this)

        lbl_no_video_base.setOnClickListener { loadVideos(0, true) }

        loadVideos(0, true)
    }


    private fun onGetSeriesError(errorMessage: String?, pageCount: Int) {


    }


    private fun loadVideos(pageCount: Int, showProgress: Boolean) {
        if (pageCount == 0) {
            showProgress("Loading, please wait...")
        }
        if (title == "EPISODES") {
            homeViewModel.callForEpisodes(id, pageCount, BaseConstants.LIMIT_VIDEOS)
        } else {
            homeViewModel.callForVideos(id, pageCount, BaseConstants.LIMIT_VIDEOS, type)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgress()
    }
    fun onGetVideosSuccess(
        tempVideos: List<Video>,
        type: BaseConstants.Video,
        pCount: Int,
        category: Category?,
    ) {
        dismissProgress()
        footer_progress_base.visibility = View.GONE
        lbl_no_video_base.visibility = View.GONE
        rv_season_episodes.visibility = View.VISIBLE

        if (swipetorefresh_base.isRefreshing) {
            swipetorefresh_base.isRefreshing = false
        }

        if (pCount == 0 && tempVideos.size == 0) {
            lbl_no_video_base.text =
                String.format(BaseConstants.RETRY_LABEL, BaseConstants.VIDEOS_NOT_FOUND_ERROR)
            lbl_no_video_base.visibility = View.VISIBLE
            rv_season_episodes.visibility = View.GONE
            return
        }

        if (pCount == 0) {
            adapter!!.items.clear()
            if (tempVideos.size >= BaseConstants.LIMIT_VIDEOS) {
                rv_season_episodes.removeOnScrollListener(endlessScrollListener!!)
                rv_season_episodes.addOnScrollListener(endlessScrollListener!!)
            }
            adapter!!.items = tempVideos
            return
        }
        adapter!!.addAll(tempVideos)
    }


    private fun onGetVideosError(
        errorMessage: String,
        type: BaseConstants.Video,
        pCount: Int,
        category: Category?,
    ) {
        dismissProgress()
        footer_progress_base.visibility = View.GONE
        swipetorefresh_base.isRefreshing = false
        if (pCount > 0) {
            //pageCount -= BaseConstants.LIMIT_VIDEOS
            endlessScrollListener!!.rollback(pCount - BaseConstants.LIMIT_VIDEOS)
        } else {
            lbl_no_video_base.text = String.format(BaseConstants.RETRY_LABEL, errorMessage)
            lbl_no_video_base.visibility = View.VISIBLE
            rv_season_episodes.visibility = View.GONE
        }
    }


    override fun onRefresh() {
        pCount = 0
        endlessScrollListener!!.init()
        loadVideos(0, false)
    }

    fun showProgress(message: String) {
        if (waitingDialog == null) {
            waitingDialog = WaitingDialog(this@BaseVideoListActivity)
            waitingDialog!!.setMessage(message)
        }
        if (!this@BaseVideoListActivity.isFinishing) {
            waitingDialog!!.show()
        }
    }

    fun dismissProgress() {
        if (waitingDialog != null && waitingDialog!!.isShowing) {
            waitingDialog!!.dismiss()
            waitingDialog = null
        }
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
        if (Utils.isNetworkAvailable(this@BaseVideoListActivity)) {
            /*   val intent = Intent(this@BaseActivity, PlayerListActivity::class.java)
               intent.putExtra(Key.VIDEO, data as Video)
               startActivity(intent)*/
            /*startActivity(MediaDetailActivity.getDetailIntent(this@BaseActivity as Context, data as BaseVideo))
            Utils.animateActivity(this@BaseActivity, "next")*/
            if (data is Video) {
                val video = data
                if ("T".equals(video.type)) {
                    val intent = Intent(this@BaseVideoListActivity, SeriesActivity::class.java)
                    intent.putExtra(Key.CATEGORY, video)
                    startActivity(intent)
                } else {
                    startActivity(MediaDetailActivity.getDetailIntent(this@BaseVideoListActivity as Context,
                        video))
                    Utils.animateActivity(this@BaseVideoListActivity, "next")
                }
            } else {
                DialogUtils.showMessage(this@BaseVideoListActivity,
                    "BASE VIDEO CAST",
                    Toast.LENGTH_SHORT,
                    false)
            }
        } else {
            DialogUtils.showMessage(this@BaseVideoListActivity,
                BaseConstants.CONNECTION_ERROR,
                Toast.LENGTH_SHORT,
                false)
        }
    }
}
