package com.perseverance.phando.home.series

import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.HomeActivity
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.ui.WaitingDialog
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Utils
import kotlinx.android.synthetic.main.activity_series.*


class SeriesActivity : BaseScreenTrackingActivity(), AdapterClickListener {

    override var screenName = BaseConstants.SERIES_SCREEN
    private var waitingDialog: WaitingDialog? = null
    private lateinit var episodeAdapter: EpisodeListAdapter
    private lateinit var trailerAdapter: TrailerListAdapter
    private lateinit var baseVideo: Video
    private var fromDyLink: Boolean = false
    private val homeViewModel by lazy {
        ViewModelProviders.of(this).get(SeriesViewModel::class.java)
    }
    private val notificationDao by lazy {
        AppDatabase.getInstance(this@SeriesActivity)?.notificationDao()
    }
    private val videoListViewModelObserver = Observer<DataLoadingStatus<TVSeriesResponseDataNew>> { it ->
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
                it.data?.let {tvSeriesResponse->
                    onGetVideosSuccess(tvSeriesResponse)
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
        fromDyLink = intent.getBooleanExtra("fromDyLink", false)
        baseVideo = intent.getParcelableExtra(Key.CATEGORY)?:Video()
        homeViewModel.callForSeries(baseVideo.id.toString()).observe(this, videoListViewModelObserver)

        rv_season_episodes.layoutManager = LinearLayoutManager(this@SeriesActivity)
        //recycler_view_base.setHasFixedSize(true)
        episodeAdapter = EpisodeListAdapter(this@SeriesActivity, this)
        trailerAdapter = TrailerListAdapter(this@SeriesActivity, this)
        val videos = ArrayList<Episode>()
        episodeAdapter?.items = videos
        rv_season_episodes.adapter = episodeAdapter
        rv_season_trailer.adapter = trailerAdapter
        viewMore.setOnClickListener {
            if (seriesDescription.visibility == View.VISIBLE) {
                seriesDescription.gone()
                viewMore.setImageResource(R.drawable.ic_detail_arrow_down)
            } else {
                seriesDescription.visible()
                viewMore.setImageResource(R.drawable.ic_detail_arrow_up)
            }
        }
        vw_share_series.setOnClickListener {
            shareSeriesUrl()
        }
    }

    private fun onGetVideosSuccess(tvSeriesResponseData: TVSeriesResponseDataNew) {
        prepareShareMedia(tvSeriesResponseData.shareUrl)
        banner_img.visibility = View.VISIBLE
        seriesTitle.text = tvSeriesResponseData.title

        val otherText = StringBuilder()
        tvSeriesResponseData.rating.let {
            otherInfo.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this@SeriesActivity, R.drawable.ic_rating), null, null, null)
            otherText.append(it)
        }
        tvSeriesResponseData.maturityRating.let {
            otherText.append(" | $it")
        }
        tvSeriesResponseData.genres.let {
            otherText.append(" | " + it.joinToString())
        }
        otherInfo.text = otherText.toString()
        Utils.displayImage(this@SeriesActivity, tvSeriesResponseData.thumbnail,
                R.drawable.video_placeholder, R.drawable.error_placeholder, banner_img)

        //seasonSelector.adapter=AlgorithmAdapter(this@SeriesActivity, tvSeriesResponseData.seasons)
        seasonSelector.adapter = ArrayAdapter(this, R.layout.spinner_item, tvSeriesResponseData.seasons)
        seasonSelector.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedSeason = parent.getItemAtPosition(position) as Season
                refreshThumbnailAndData(selectedSeason)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        seasonSelector.setSelection(0)
//        val selectedSeason = tvSeriesResponseData.seasons[0]
//        refreshThumbnailAndData(selectedSeason)
//        try {
//            episodeAdapter.addAll(selectedSeason.episodes)
//            if (selectedSeason.episodes.isNullOrEmpty()) vw_episodes_container.gone()
//        } catch (e: Exception) {
//            vw_episodes_container.gone()
//        }
//        try {
//            trailerAdapter.addAll(selectedSeason.trailers)
//            if (selectedSeason.trailers.isNullOrEmpty()) vw_trailer_container.gone()
//        } catch (e: Exception) {
//            vw_trailer_container.gone()
//        }

        vw_play_series.setOnClickListener {
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

    private fun refreshThumbnailAndData(selectedSeason: Season) {
        Utils.displayImage(this@SeriesActivity, selectedSeason.thumbnail,
                R.drawable.video_placeholder, R.drawable.error_placeholder, banner_img)
        selectedSeason.episodes.let { episodeAdapter.items = it
            if(it.isEmpty()) vw_episodes_container.gone()} ?:vw_episodes_container.gone()
        selectedSeason.trailers?.let { trailerAdapter.items = it
            if (it.isEmpty()) vw_trailer_container.gone()} ?:vw_trailer_container.gone()
        val seasonDescription = (selectedSeason.detail ?: "") + "\n\n" + (selectedSeason.other_credits ?: "")
        seriesDescription.text = seasonDescription
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

    override fun onPrepareSupportNavigateUpTaskStack(builder: TaskStackBuilder) {
        super.onPrepareSupportNavigateUpTaskStack(builder)
        builder.editIntentAt(builder.intentCount - 1)?.putExtras(Intent(this@SeriesActivity, HomeActivity::class.java))
    }

    override fun onItemClick(data: Any) {
        when (data) {
            is Episode -> {
                if (Utils.isNetworkAvailable(this@SeriesActivity)) {
                    val baseVideo = Video()
                    baseVideo.id = data.id
                    baseVideo.thumbnail = data.thumbnail
                    baseVideo.title = data.id.toString()
                    baseVideo.is_free = data.is_free
                    baseVideo.type = data.type
                    startActivity(MediaDetailActivity.getDetailIntent(this@SeriesActivity, baseVideo))
                    Utils.animateActivity(this@SeriesActivity, "next")
                } else {
                    DialogUtils.showMessage(this@SeriesActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                }
            }
            is TrailerX -> {
                if (Utils.isNetworkAvailable(this@SeriesActivity)) {
                    val baseVideo = Video()
                    baseVideo.id = when (data.type) {
                        "S" -> {
                            data.seasonId
                        }
                        "E" -> {
                            data.episodeId
                        }
                        else -> {
                            data.id
                        }
                    }
                    baseVideo.thumbnail = data.thumbnail
                    baseVideo.title = data.id.toString()
                    baseVideo.is_free = 1
                    baseVideo.type = data.type
                    startActivity(MediaDetailActivity.getDetailIntent(this@SeriesActivity, baseVideo))
                    Utils.animateActivity(this@SeriesActivity, "next")
                } else {
                    DialogUtils.showMessage(this@SeriesActivity, BaseConstants.CONNECTION_ERROR, Toast.LENGTH_SHORT, false)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (fromDyLink) {
            startActivity(Intent(this@SeriesActivity, HomeActivity::class.java))
            finish()
        } else super.onBackPressed()
    }

    private fun shareSeriesUrl() {
        dynamicLink?.let {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, it)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share"))
        }
    }

    var dynamicLink: String? = null
    private fun prepareShareMedia(linkUrl: String) {
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true
        sanitizer.parseUrl(linkUrl)
        val shareTitle = sanitizer.getValue("title")
        val shareThumbnail = sanitizer.getValue("thumbnail")

        Firebase.dynamicLinks.shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse(linkUrl)
            domainUriPrefix = BuildConfig.DOMAIN_URI
            androidParameters(packageName) {
                minimumVersion = 6
            }
            iosParameters(packageName) {
                appStoreId = "1524436726"
                minimumVersion = "2.3"
            }
            googleAnalyticsParameters {
                source = "android"
                medium = "app"
            }
            socialMetaTagParameters {
                title = shareTitle
                imageUrl = Uri.parse(shareThumbnail)
            }
        }.addOnSuccessListener { result ->
            result?.previewLink.let {
                // Log.i("previewLink", it.toString())
            }
            dynamicLink = result?.shortLink.toString()
        }

//        val dynamicLink = Firebase.dynamicLinks.dynamicLink { // or Firebase.dynamicLinks.shortLinkAsync
//            link = Uri.parse(linkUrl)
//            domainUriPrefix = BuildConfig.DOMAIN_URI
//            androidParameters(packageName) {
//                minimumVersion = 6
//            }
//            iosParameters(packageName) {
//                appStoreId = "1524436726"
//                minimumVersion = "2.3"
//            }
//            googleAnalyticsParameters {
//                source = "android"
//                medium = "app"
//            }
//
//            socialMetaTagParameters {
//                title = shareTitle
//                imageUrl = Uri.parse(shareThumbnail)
//                //  description = "This link works whether the app is installed or not!"
//            }
//        }
//        Log.e("dynamicLink : ",dynamicLink.uri.toString())

    }
}
