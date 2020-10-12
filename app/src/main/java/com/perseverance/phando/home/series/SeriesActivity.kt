package com.perseverance.phando.home.series

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.ui.WaitingDialog
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.TrackingUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_series.*
import kotlinx.android.synthetic.main.activity_series.otherInfo
import kotlinx.android.synthetic.main.activity_series.viewMore

class SeriesActivity : BaseScreenTrackingActivity(), AdapterClickListener {

    override var screenName=BaseConstants.SERIES_SCREEN

    private var waitingDialog: WaitingDialog? = null
    private var adapter: SeriesListAdapter? = null
    private lateinit var baseVideo: Video
    private val homeViewModel by lazy {
        ViewModelProviders.of(this).get(SeriesViewModel::class.java)
    }
    private val notificationDao by lazy {
        AppDatabase.getInstance(this@SeriesActivity)?.notificationDao()
    }
    val videoListViewModelObserver = Observer<DataLoadingStatus<TVSeriesResponseData>> {
        progressBar.gone()

        when (it?.status) {
            LoadingStatus.LOADING -> {
                progressBar.visible()
            }
            LoadingStatus.ERROR -> {
                it.message?.let {
                    Toast.makeText(this@SeriesActivity, it, Toast.LENGTH_LONG).show()
                }
            }
            LoadingStatus.SUCCESS -> {
                it.data?.let {
                    onGetVideosSuccess(it)
                }


            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        intent?.getLongExtra(Key.NOTIFICATION_DB_ID, 0)?.let {
            notificationDao?.markNotificationRead(it)
        }
        baseVideo = intent.getParcelableExtra(Key.CATEGORY)
        homeViewModel.callForSeries(baseVideo.id.toString()).observe(this, videoListViewModelObserver)

        recycler_view_base.layoutManager = LinearLayoutManager(this@SeriesActivity)
        //recycler_view_base.setHasFixedSize(true)
        adapter = SeriesListAdapter(this@SeriesActivity, this)
        val videos = ArrayList<SeriesData>()
        adapter?.items = videos
        recycler_view_base.adapter = adapter
        viewMore.setOnClickListener {
            if (seriesDescription.visibility == View.VISIBLE) {
                seriesDescription.gone()
                viewMore.setImageResource(R.drawable.ic_detail_arrow_down)
            } else {
                seriesDescription.visible()
                viewMore.setImageResource(R.drawable.ic_detail_arrow_up)
            }
        }
    }


    fun onGetVideosSuccess(tvSeriesResponseData: TVSeriesResponseData) {


        banner_img.visibility = View.VISIBLE
        // banner_img.layoutParams.height = Utils.getSeriesBannerProportionalHeight(this@SeriesActivity);
        seriesTitle.text = tvSeriesResponseData.title
        seriesDescription.text = tvSeriesResponseData.detail

        val otherText = StringBuilder()

        tvSeriesResponseData.rating.let {
            otherInfo.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this@SeriesActivity, R.drawable.ic_rating), null, null, null)
            otherText.append(it)
        }
        tvSeriesResponseData.maturity_rating.let {
            otherText.append(" | $it")
        }
        tvSeriesResponseData.genres.let {

            otherText.append(" | " + it.joinToString())
        }

        otherInfo.text = otherText.toString()

        Utils.displayImage(this@SeriesActivity, tvSeriesResponseData.thumbnail,
                R.drawable.video_placeholder, R.drawable.error_placeholder, banner_img)


        adapter!!.addAll(tvSeriesResponseData.seasons)

        play.setOnClickListener {
            if (tvSeriesResponseData.seasons.isNullOrEmpty()) {
                return@setOnClickListener
            }
            val seasons = tvSeriesResponseData.seasons
            if (seasons.isNotEmpty()) {
                val lastSeason = seasons.get(seasons.size - 1)
                lastSeason.trailer?.let {
                    if (Utils.isNetworkAvailable(this@SeriesActivity)) {

                        val baseVideo = Video()
                        baseVideo.id = it.id
                        baseVideo.thumbnail = lastSeason.thumbnail
                        baseVideo.title = lastSeason.title
                        baseVideo.is_free = lastSeason.is_free
                        baseVideo.type = it.type
                        startActivity(MediaDetailActivity.getDetailIntent(this@SeriesActivity, baseVideo))
                        Utils.animateActivity(this@SeriesActivity, "next")
                    } else {
                        DialogUtils.showMessage(this@SeriesActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                    }

                } ?: return@setOnClickListener

            }
//               tvSeriesResponseData.seasons.get(0).episodes.get(0).let {
//                   if (Utils.isNetworkAvailable(this@SeriesActivity)) {
//                       val baseVideo = Video()
//                       baseVideo.id = it.id
//                       baseVideo.thumbnail = it.thumbnail
//                       baseVideo.title = it.id.toString()
//                       baseVideo.is_free = it.is_free
//                       baseVideo.type = it.type
//                       startActivity(MediaDetailActivity.getDetailIntent(this@SeriesActivity, baseVideo))
//                       Utils.animateActivity(this@SeriesActivity, "next")
//                   } else {
//                       DialogUtils.showMessage(this@SeriesActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
//                   }
//               }

        }


    }

    fun showProgress(message: String) {
        if (waitingDialog == null) {
            waitingDialog = WaitingDialog(this@SeriesActivity)
            waitingDialog!!.setMessage(message)
        }
        if (!this@SeriesActivity.isFinishing) {
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
        var episode = data as Episode
        if (Utils.isNetworkAvailable(this@SeriesActivity)) {
            val baseVideo = Video()
            baseVideo.id = episode.id
            baseVideo.thumbnail = episode.thumbnail
            baseVideo.title = episode.id.toString()
            baseVideo.is_free = episode.is_free
            baseVideo.type = episode.type
            startActivity(MediaDetailActivity.getDetailIntent(this@SeriesActivity, baseVideo))
            Utils.animateActivity(this@SeriesActivity, "next")
        } else {
            DialogUtils.showMessage(this@SeriesActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
        }
    }
}
