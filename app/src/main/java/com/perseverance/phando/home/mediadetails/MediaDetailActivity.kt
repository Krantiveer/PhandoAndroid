package com.perseverance.phando.home.mediadetails

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.patrikanews.utils.*
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.BaseVideo
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.payment.MediaplaybackData
import com.perseverance.phando.home.mediadetails.payment.PurchaseOption
import com.perseverance.phando.home.mediadetails.payment.PurchaseOptionBottomSheetFragment
import com.perseverance.phando.home.mediadetails.payment.PurchaseOptionSelection
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.home.videolist.BaseCategoryListAdapter
import com.perseverance.phando.payment.subscription.CreateOrderResponse
import com.perseverance.phando.payment.subscription.SubscriptionPackageActivity
import com.perseverance.phando.payment.subscription.SubscriptionsViewModel
import com.perseverance.phando.utils.*
import com.qait.sadhna.LoginActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.videoplayer.*
import com.videoplayer.VideoPlayerMetadata.UriSample
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.activity_video_details.progressBar
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_detail.recyclerView
import kotlinx.android.synthetic.main.fragment_mylist.*
import org.json.JSONObject
import java.util.*

class MediaDetailActivity : AppCompatActivity(), AdapterClickListener, PhandoPlayerCallback, PaymentResultListener, PurchaseOptionSelection {
    private var razorpayOrdertId: String? = null

    companion object {
        // Important: These constants are persisted into DownloadIndex. Do not change them.
        const val STATE_QUEUED = 0

        /** The download is stopped for a specified [.stopReason].  */
        const val STATE_STOPPED = 1

        /** The download is currently started.  */
        const val STATE_DOWNLOADING = 2

        /** The download completed.  */
        const val STATE_COMPLETED = 3

        /** The download failed.  */
        const val STATE_FAILED = 4

        /** The download is being removed.  */
        const val STATE_REMOVING = 5

        /** The download will restart after all downloaded data is removed.  */
        const val STATE_RESTARTING = 7
        const val REQUEST_CODE_RENT = 111
        const val REQUEST_CODE_BUY = 112
        const val REQUEST_CODE_PACKAGE = 113
        const val ARG_VIDEO = "param_video"
        fun getDetailIntent(context: Context, video: BaseVideo): Intent {
            if (video.isFree == 0 && PreferencesUtils.getLoggedStatus().isEmpty()) {
                val intent = Intent(context, LoginActivity::class.java)
                return intent
            } else {
                val intent = Intent(context, MediaDetailActivity::class.java)

                intent.apply {
                    val arg = Bundle()
                    arg.apply {
                        putSerializable(ARG_VIDEO, video)
                    }
                    putExtras(arg)
                }

                return intent
            }
        }
    }

    var isVideoPlayed = false
    var isTrailerPlaying = false
    var isPlayerstartSent = false

    val playerViewModel by lazy {
        getViewModel<MediaDetailViewModel>()
    }
    var nextMediaMetadata: MediaplaybackData? = null
    private val subscriptionsViewModel by lazy {
        ViewModelProvider(this).get(SubscriptionsViewModel::class.java)
    }
    val videoMetadataModelObserver = Observer<DataLoadingStatus<MediaplaybackData>> {

        when (it.status) {

            LoadingStatus.LOADING -> {
                progressBar.visible()

            }
            LoadingStatus.ERROR -> {
                progressBar.gone()
                it.message?.let {
                    Toast.makeText(this@MediaDetailActivity, it, Toast.LENGTH_LONG).show()
                }
//                if (it.message.equals("Please Subscribe")) {
//                    val intent = Intent(this@MediaDetailActivity, SubscriptionPackageActivity::class.java)
//                    startActivityForResult(intent, 101)
//                } else {
//                    it.message?.let {
//                        Toast.makeText(this@MediaDetailActivity, it, Toast.LENGTH_LONG).show()
//                        mediaMetadata = null
//                    }
//
//                }

            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                it.data?.let {
                    onGetVideoMetaDataSuccess(it)
                }

            }
        }

    }
    val nextVideoMetadataModelObserver = Observer<DataLoadingStatus<MediaplaybackData>> {

        when (it.status) {

            LoadingStatus.LOADING -> {
                // progressBar.visible()

            }
            LoadingStatus.ERROR -> {
//                progressBar.gone()
//                if (it.message.equals("Please Subscribe")) {
//                    val intent = Intent(this@MediaDetailActivity, SubscriptionPackageActivity::class.java)
//                    startActivityForResult(intent, 101)
//                } else {
//                    it.message?.let {
//                        Toast.makeText(this@MediaDetailActivity, it, Toast.LENGTH_LONG).show()
//                        mediaMetadata = null
//                    }
//
//                }

            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                it.data?.let {
                    nextMediaMetadata = it
                }

            }
        }

    }
    val favoriteObserver = Observer<Int> {
        when (it) {
            0 -> {
                imgMyList.setImageResource(R.drawable.ic_detail_mylist)

            }
            1 -> {
                imgMyList.setImageResource(R.drawable.ic_detail_added_in_mylist)

            }
        }

    }

    val downloadObserver = Observer<DownloadInfo> {

        it?.let {
            when (it.status) {
                STATE_COMPLETED -> {
                    txtDownload?.text = "Download"
                    imgDownload.setImageResource(R.drawable.ic_detail_downloaded)
                }
                STATE_QUEUED -> {
                    txtDownload?.text = "${it.progress}%"
                }
                STATE_DOWNLOADING -> {
                    txtDownload?.text = "${it.progress}%"
                }
                STATE_FAILED -> {
                    txtDownload?.text = "Download"
                    imgDownload.setImageResource(R.drawable.ic_detail_downloaded)
                }
                STATE_QUEUED -> {

                }
                STATE_STOPPED -> {
                    txtDownload?.text = "Download"
                    imgDownload.setImageResource(R.drawable.ic_detail_downloaded)
                }
                STATE_REMOVING -> {
                    txtDownload?.text = "Download"
                    imgDownload.setImageResource(R.drawable.ic_detail_downloaded)
                }
                STATE_RESTARTING -> {

                }
                else -> {

                }
            }
        } ?: run {
            imgDownload.setImageResource(R.drawable.ic_detail_download)

        }


    }

    val likeObserver = Observer<Int> {
        when (it) {
            0 -> {
                imgLike.setImageResource(R.drawable.ic_detail_like)

            }
            1 -> {
                imgLike.setImageResource(R.drawable.ic_detail_like_selected)

            }
        }

    }
    val dislikeObserver = Observer<Int> {
        when (it) {
            0 -> {
                val drawable = ContextCompat.getDrawable(this@MediaDetailActivity, R.drawable.ic_dislike)
                // dislike?.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)

            }
            1 -> {
                val drawable = ContextCompat.getDrawable(this@MediaDetailActivity, R.drawable.ic_dislike_selected)
                //  dislike?.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            }
        }

    }


    val messageObserver = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }

    var mListener: SimpleOrientationEventListener? = null
    private val handler = Handler()
    private var mediaMetadata: MediaMetadata? = null
    private lateinit var mediaplaybackData: MediaplaybackData
    private var trailerListAdapter: TrailerListAdapter? = null


    private fun setDataToPlayer(addUrl: String? = null, mediaUrl: String, seekTo: Long = 0) {
        playerThumbnailContainer.gone()
        phandoPlayerView.visible()
        val intent = Intent()
        val uri = Uri.parse(mediaUrl)
        val sample: VideoPlayerMetadata = UriSample(
                null,
                uri,
                null,
                false,
                null,
                if (addUrl.isNullOrEmpty()) null else Uri.parse(addUrl),
                null,
                null)
        intent.putExtra(
                PhandoPlayerView.PREFER_EXTENSION_DECODERS_EXTRA, false)
        val abrAlgorithm = PhandoPlayerView.ABR_ALGORITHM_DEFAULT
        intent.putExtra(PhandoPlayerView.ABR_ALGORITHM_EXTRA, abrAlgorithm)
        intent.putExtra(PhandoPlayerView.TUNNELING_EXTRA, false)
        intent.putExtra(PhandoPlayerView.PLAYER_LOGO, R.mipmap.ic_launcher)
        intent.putExtra(PhandoPlayerView.KEY_POSITION, seekTo)
        sample.addToIntent(intent)
        phandoPlayerView.setVideoData(intent)
        phandoPlayerView.setDefaultArtwork(getDrawable(R.mipmap.ic_launcher))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_video_details)
        TrackingUtils.sendScreenTracker(BaseConstants.MEDIA_DETAILS)
        //setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //toggleHideyBar()

            handler.postDelayed(landscopeRunnable, 200)

        } else {
            handler.postDelayed(portrateRunnable, 200)
        }
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // TODO: The system bars are visible. Make any desired
                // adjustments to your UI, such as showing the action bar or
                // other navigational controls.
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (!isFullScreen()) {
                        // toggleHideyBar()

                    }

                }
            } else {
                // TODO: The system bars are NOT visible. Make any desired
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
            }
        }
        val decoration = BaseRecycleMarginDecoration(this@MediaDetailActivity)
        recyclerView.addItemDecoration(decoration)


        playerViewModel.getVideoDetailMutableLiveData().observe(this, videoMetadataModelObserver)
        playerViewModel.getNextEpisodeVideoDetailMutableLiveData().observe(this, nextVideoMetadataModelObserver)
        playerViewModel.message.observe(this, messageObserver)

        playerViewModel.isInWishlist.observe(this, favoriteObserver)
        playerViewModel.isLiked.observe(this, likeObserver)
        playerViewModel.isDisliked.observe(this, dislikeObserver)
        playerViewModel.downloadStatus.observe(this, downloadObserver)

        val video = intent?.getSerializableExtra(ARG_VIDEO) as BaseVideo
        playerViewModel.refreshMediaMetadata(video)
        MyLog.d("Player Image", video.thumbnail)
        Utils.displayImage(this, video.thumbnail, R.drawable.video_placeholder, R.drawable.video_placeholder, playerThumbnail)
        favorite.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                playerViewModel.reloadTrigger.value?.let {
                    playerViewModel.addToMyList(it.entryId, it.mediaType)
                }
            }

        }
        like.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                playerViewModel.reloadTrigger.value?.let {
                    playerViewModel.addToLike(it.entryId, it.mediaType)
                }
            }

        }
//        dislike.setOnClickListener {
//            playerViewModel.reloadTrigger.value?.let {
//                playerViewModel.addToDislike(it.entryId, it.mediaType)
//            }
//        }
        if (BuildConfig.APPLICATION_ID == "com.perseverance.anvitonmovies") {
            share.gone()
        }
        share.setOnClickListener {
            shareVideoUrl()
        }

        download.setOnClickListener {
            //playerViewModel.refreshDownloadStatus(mediaMetadata?.media_url!!)
            playerViewModel.downloadStatus.value?.let {
                val mBottomSheetDialog = BottomSheetDialog(this)
                val sheetView: View = layoutInflater.inflate(R.layout.fragment_download_control_options, null)
                mBottomSheetDialog.setContentView(sheetView)
                val downloadDelete = sheetView.findViewById<TextView>(R.id.downloadDelete)
                val downloadResume = sheetView.findViewById<TextView>(R.id.downloadResume)
                val downloadStop = sheetView.findViewById<TextView>(R.id.downloadStop)
                downloadDelete.setOnClickListener {
                    VideoSdkUtil.deleteDownloadedInfo(application, mediaMetadata?.media_url)
                    mBottomSheetDialog.dismiss()
                }
                downloadResume.setOnClickListener {
                    VideoSdkUtil.resumeDownload(application)
                    mBottomSheetDialog.dismiss()
                }
                downloadStop.setOnClickListener {
                    VideoSdkUtil.pauseDownload(application)
                    mBottomSheetDialog.dismiss()
                }

                when (it.status) {

                    STATE_COMPLETED -> {
                        downloadResume.gone()
                        downloadStop.gone()
//                        DialogUtils.showDialog(this@MediaDetailActivity, "Alert!", "Do you want to delete saved video", "Yes", "No", object : AppDialogListener {
//                            override fun onNegativeButtonPressed() {
//
//                            }
//
//                            override fun onPositiveButtonPressed() {
//                                VideoSdkUtil.deleteDownloadedInfo(application, mediaMetadata?.media_url)
//                            }
//
//                        })

                    }
                    STATE_DOWNLOADING -> {
                        downloadResume.gone()
                    }
                    STATE_FAILED -> {

                    }
                    STATE_QUEUED -> {
                        downloadStop.gone()
                    }
                    STATE_STOPPED -> {
                        downloadStop.gone()
                    }
                    STATE_REMOVING -> {
                        downloadDelete.gone()
                    }
                    STATE_RESTARTING -> {

                    }
//
//                    3 -> {
//
//                    }
//                    2 -> {
//                        toast("Media is being download")
//
//                    }
//                    0, 1, 4, 5 -> {
//                        val videoPlayerMetadata = UriSample(
//                                null,
//                                Uri.parse(mediaMetadata?.media_url),
//                                null,
//                                false,
//                                null,
//                                null,
//                                null,
//                                null)
//                        val downloadMetadata = Gson().toJson(DownloadMetadata(mediaMetadata?.media_id,
//                                mediaMetadata?.type, mediaMetadata?.title, mediaMetadata?.detail, mediaMetadata?.thumbnail, mediaMetadata?.media_url,
//                                mediaMetadata?.getOtherText()))
//
//                        phandoPlayerView.startDownload(videoPlayerMetadata, downloadMetadata)
//                    }
//                    else -> {
//
//                    }
                }
                mBottomSheetDialog.show()
            } ?: run {
                mediaMetadata?.media_url?.let {
                    val videoPlayerMetadata = UriSample(
                            null,
                            Uri.parse(it),
                            null,
                            false,
                            null,
                            null,
                            null,
                            null)
                    val downloadMetadata = Gson().toJson(DownloadMetadata(mediaMetadata?.media_id,
                            mediaMetadata?.type, mediaMetadata?.title, mediaMetadata?.detail, mediaMetadata?.thumbnail, mediaMetadata?.media_url,
                            mediaMetadata?.getOtherText()))
                    phandoPlayerView.startDownload(videoPlayerMetadata, downloadMetadata)
                }
            }
        }
        play.setOnClickListener {

            playVideo()

        }
        watchNow.setOnClickListener {
            //mediaMetadata?.trailers?.get(0)?.let { it1 -> onItemClick(it1) }
            playVideo()


        }

        playerSetting.setOnClickListener {
            phandoPlayerView.openSettings()
        }
        orientation.setOnClickListener {
            changeOrientation()
        }
        headerBack.setOnClickListener {
            onBackPressed()
        }
        viewMore.setOnClickListener {
            if (videoDescription.visibility == View.VISIBLE) {
                videoDescription.gone()
                viewMore.setImageResource(R.drawable.ic_detail_arrow_down)
            } else {
                videoDescription.visible()
                viewMore.setImageResource(R.drawable.ic_detail_arrow_up)
            }
        }
        nextEpisode.setOnClickListener {
            mediaMetadata?.next_media?.let {
                nextMediaMetadata?.let {
                    onGetVideoMetaDataSuccess(it)
                }
            }
        }
        skipIntro.setOnClickListener {
            mediaMetadata?.intro?.let {
                if (it.endTime > 0) {
                    phandoPlayerView.seekTo(it.endTime)
                    skipIntro.gone()
                }
            }
        }

        rentMedia.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                message.setOnClickListener {
                    val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                    startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)
                }

            } else {
                val purchaseOptionBottomSheetFragment = PurchaseOptionBottomSheetFragment()
                val bundle = Bundle()
                bundle.putParcelable("payment_option", rentMedia.tag as PurchaseOption)
                purchaseOptionBottomSheetFragment.arguments = bundle
                purchaseOptionBottomSheetFragment.show(supportFragmentManager, purchaseOptionBottomSheetFragment.getTag())
            }
        }
        buyMedia.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {

                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)

            } else {
                val purchaseOptionBottomSheetFragment = PurchaseOptionBottomSheetFragment()
                val bundle = Bundle()
                bundle.putParcelable("payment_option", buyMedia.tag as PurchaseOption)
                purchaseOptionBottomSheetFragment.arguments = bundle
                purchaseOptionBottomSheetFragment.show(supportFragmentManager, purchaseOptionBottomSheetFragment.getTag())
            }
        }
        packageMedia.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                message.setOnClickListener {
                    val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                    startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)
                }

            } else {
                val intent = Intent(this@MediaDetailActivity, SubscriptionPackageActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_PACKAGE)
            }
        }

    }

    private fun playVideo() {
        when (mediaplaybackData.mediaCode) {
            "free", "rented", "buyed", "package_purchased" -> {
                isVideoPlayed = true
                isTrailerPlaying = false
                mediaMetadata?.media_url?.let {
                    play.gone()
                    setDataToPlayer(addUrl = mediaMetadata?.ad_url_mobile_app, mediaUrl = mediaMetadata?.media_url!!, seekTo = mediaMetadata!!.last_watch_time)
                }
            }
            "package_media" -> {
                toast("This is a premium content. Please purchase to watch.")
            }
            "rent_or_buy" -> {
                toast("This is a premium content. Please purchase to watch.")
            }
            "only_rent" -> {
                toast("This is a premium content. Please purchase to watch.")
            }
            "only_buy" -> {
                toast("This is a premium content. Please purchase to watch.")
            }
            "rented_and_can_buy" -> {
                isVideoPlayed = true
                isTrailerPlaying = false
                mediaMetadata?.media_url?.let {
                    play.gone()
                    setDataToPlayer(addUrl = mediaMetadata?.ad_url_mobile_app, mediaUrl = mediaMetadata?.media_url!!, seekTo = mediaMetadata!!.last_watch_time)
                }
            }
        }


    }

    private fun playVideoTrailer(trailerUrl: String) {
        play.gone()
        isVideoPlayed = true
        isTrailerPlaying = true
        setDataToPlayer(addUrl = mediaMetadata?.ad_url_mobile_app, mediaUrl = trailerUrl)
    }

    override fun onPurchaseOptionSelected(purchaseOption: PurchaseOption) {
        createOrder(purchaseOption.payment_info.payment_type, purchaseOption.payment_info.media_id.toString(), purchaseOption.payment_info.type)
    }

    fun onGetVideoMetaDataSuccess(mediaplaybackData: MediaplaybackData) {
        isPlayerstartSent = false
        detailContent.visible()
        this.mediaplaybackData = mediaplaybackData
        if (!isVideoPlayed) {
            play.visible()
        }
        mediaplaybackData.note?.let {
            if (it.isNullOrBlank()) {
                purchaseNote.gone()
            } else {
                purchaseNote.text = it
                purchaseNote.visible()
            }
        }

        when (mediaplaybackData.mediaCode) {
            "free", "rented", "buyed", "package_purchased" -> {
                actionControlersBuy.gone()
            }
            "package_media" -> {
                rentMedia.gone()
                buyMedia.gone()
                packageMedia.visible()
                actionControlersBuy.visible()
            }
            "rent_or_buy" -> {
                rentMedia.visible()
                buyMedia.visible()
                packageMedia.gone()
                actionControlersBuy.visible()
                mediaplaybackData.purchase_option.forEach {
                    when (it.key) {
                        "rent_price" -> {
                            rentMedia.text = "Rent from Rs ${it.value}"
                            rentMedia.tag = it
                        }
                        "purchase_price" -> {
                            buyMedia.text = "Buy from Rs ${it.value}"
                            buyMedia.tag = it
                        }
                    }
                }
            }
            "only_rent" -> {
                rentMedia.visible()
                buyMedia.gone()
                packageMedia.gone()
                actionControlersBuy.visible()
                mediaplaybackData.purchase_option.forEach {
                    when (it.key) {
                        "rent_price" -> {
                            rentMedia.text = "Rent from Rs ${it.value}"
                            rentMedia.tag = it
                        }

                    }
                }
            }
            "only_buy" -> {
                rentMedia.gone()
                buyMedia.visible()
                packageMedia.gone()
                actionControlersBuy.visible()
                mediaplaybackData.purchase_option.forEach {
                    when (it.key) {

                        "purchase_price" -> {
                            buyMedia.text = "Buy from Rs ${it.value}"
                            buyMedia.tag = it
                        }
                    }
                }
            }
            "rented_and_can_buy" -> {
                rentMedia.gone()
                buyMedia.visible()
                packageMedia.gone()
                actionControlersBuy.visible()
                mediaplaybackData.purchase_option.forEach {
                    when (it.key) {
                        "rent_price" -> {
                            rentMedia.text = "Rented from Rs ${it.value}"
                            rentMedia.tag = it
                            rentMedia.isEnabled = false
                        }
                        "purchase_price" -> {
                            buyMedia.text = "Buy from Rs ${it.value}"
                            buyMedia.tag = it
                        }
                    }
                }
            }
        }
        actionControlers.visible()
        this.mediaMetadata = mediaplaybackData.data
        playerViewModel.refreshDownloadStatus(mediaMetadata?.media_url!!)
        videoTitle.text = mediaMetadata?.title
        videoDescription.text = mediaMetadata?.detail
        //Utils.makeTextViewResizable(videoDescription, 3, "View More", true)
        ratingLogo.gone()

        val otherText = StringBuilder()

        mediaMetadata?.rating.let {
            otherText.append(it)
            ratingLogo.visible()
        }
        mediaMetadata?.maturity_rating.let {
            otherText.append(" | $it")
        }
        mediaMetadata?.genres?.let {

            otherText.append(" | " + it.joinToString())
        }
        mediaMetadata?.actors?.let {

            otherText.append(" | " + it.joinToString())
        }
        otherInfo.text = otherText.toString()



        mListener = object : SimpleOrientationEventListener(this@MediaDetailActivity) {
            override fun onChanged(lastOrientation: Int, orientation: Int) {
                if (Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
                    return
                }

                // Toast.makeText(this@MediaDetailActivity,""+getResources().getConfiguration().orientation,Toast.LENGTH_SHORT).show()
                when (orientation) {
                    SimpleOrientationEventListener.ORIENTATION_LANDSCAPE -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                    }
                    SimpleOrientationEventListener.ORIENTATION_LANDSCAPE_REVERSE -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    }
                    SimpleOrientationEventListener.ORIENTATION_PORTRAIT -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    SimpleOrientationEventListener.ORIENTATION_PORTRAIT_REVERSE -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    }
                }

            }
        }
        mListener?.enable()

        playerViewModel.isInWishlist.postValue(mediaMetadata?.is_wishlist)
        playerViewModel.isLiked.postValue(mediaMetadata?.is_like)
        playerViewModel.isDisliked.postValue(mediaMetadata?.is_dislike)
        setRelatedVideo()


        if (play.visibility != View.VISIBLE) {
            playVideo()
        } else {
            if (mediaMetadata!!.last_watch_time > 0) {
                playVideo()
            }
        }
        mediaMetadata?.next_media?.let {
            playerViewModel.getNextEpisodeMediaMetadata(BaseVideo().apply {
                mediaType = it.type
                entryId = it.id.toString()
            })
        }


    }

    private fun setRelatedVideo() {
        mediaMetadata?.let {
            if (it.trailers.isNotEmpty()) {
                trailerContainer.visible()
                val manager = LinearLayoutManager(this@MediaDetailActivity, LinearLayoutManager.HORIZONTAL, false)
                trailerRecyclerView.layoutManager = manager
                trailerListAdapter = TrailerListAdapter(this@MediaDetailActivity, this)
                trailerListAdapter?.items = it.trailers
                trailerRecyclerView.adapter = trailerListAdapter
            } else {
                trailerContainer.gone()
            }

            if (it.related.isNotEmpty()) {
                relatedContainer.visible()
                val manager = GridLayoutManager(this@MediaDetailActivity, 2)
                recyclerView.layoutManager = manager
                recyclerView.setHasFixedSize(true)
                val adapter = BaseCategoryListAdapter(this@MediaDetailActivity, this)
                adapter.items = it.related
                recyclerView.adapter = adapter
            } else {
                relatedContainer.gone()
            }
        }


    }

    private fun changeOrientation() {
        val currentOrientation = resources.configuration.orientation

        when (currentOrientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                // runHandler(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                // runHandler(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            }
        }

    }

    fun runHandler(orientation: Int) {
        Handler().postDelayed({
            requestedOrientation = orientation
        }, 1000)

    }

    val portrateRunnable = Runnable {
        portrate()
    }
    val landscopeRunnable = Runnable {
        landscope()
    }

    fun landscope() {
        //val width = Util.getScreenWidth(this@MediaDetailActivity)
        hideSystemUI()
        val width = window.decorView.width
        val height = Util.getScreenHeight(this@MediaDetailActivity)
        fragmentContainer.requestLayout()
        fragmentContainer.layoutParams.height = height
        fragmentContainer.layoutParams.width = width
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        window.decorView.apply {
//            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
//        }


    }

    fun portrate() {
        showSystemUI()
        val width = Util.getScreenWidthForVideo(this@MediaDetailActivity)
        //  val width = window.decorView.width
        val height = Util.getScreenHeightForVideo(this@MediaDetailActivity)
        fragmentContainer.requestLayout()
        fragmentContainer.layoutParams.height = height
        fragmentContainer.layoutParams.width = width
        //  window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setRelatedVideo()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handler.removeCallbacksAndMessages(null)
        toggleHideyBar()
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                handler.postDelayed(landscopeRunnable, 200)
                orientation.setImageResource(R.drawable.player_screen_zoom_out)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                handler.postDelayed(portrateRunnable, 200)
                orientation.setImageResource(R.drawable.player_screen_zoom_in)

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // menuInflater.inflate(R.menu.menu_video_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
//            R.id.action_share -> {
//                shareVideoUrl()
//            }

        }
        return true
    }

    private fun shareVideoUrl() {
        playerViewModel.reloadTrigger.value?.let {
            val title = videoTitle.text.toString()
            val mediaType = it.mediaType
            it.entryId?.let {
                var url = FeatureConfigClass().baseUrl + "watch/"
                if (mediaType.equals("E")) {
                    url += "tvshow/episode/"
                } else {
                    url += "movie/"
                }
                val shareText = "${title}\n${url}${it}"
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    //val shareTextHtml = fromHtml("<p>${shareText}</p>")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Share"))
            }

        }
    }

    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    override fun onBackPressed() {
        val currentOrientation = resources.configuration.orientation
        when (currentOrientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                super.onBackPressed()
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }


    }

    override fun onResume() {
        super.onResume()
        mListener?.enable()

    }

    override fun onPause() {
        super.onPause()
        mListener?.disable()
        updateCurrentPositionOnServer()

    }

    private fun updateCurrentPositionOnServer() {
        if (isTrailerPlaying) {
            return
        }
        mediaMetadata?.let { mediaMetaData ->
            phandoPlayerView.currentPosition?.let {
                if (it > 0) {
                    playerViewModel.setContinueWatchingTime(mediaMetaData?.document_media_id.toString(), it.toString())
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoginActivity.REQUEST_CODE_LOGIN -> {
                if (resultCode == Activity.RESULT_OK) {
                    playerViewModel.refreshMediaMetadata(playerViewModel.reloadTrigger.value)
                } else {

                }
            }

            REQUEST_CODE_RENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    playerViewModel.refreshMediaMetadata(playerViewModel.reloadTrigger.value)
                } else {

                }
            }
            REQUEST_CODE_BUY -> {
                if (resultCode == Activity.RESULT_OK) {
                    playerViewModel.refreshMediaMetadata(playerViewModel.reloadTrigger.value)
                } else {

                }
            }
            REQUEST_CODE_PACKAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    playerViewModel.refreshMediaMetadata(playerViewModel.reloadTrigger.value)
                } else {

                }
            }
        }


    }

    override fun onItemClick(data: Any) {
        updateCurrentPositionOnServer()
        when (data) {
            is Video -> {

                if ("T".equals(data.mediaType)) {
                    val intent = Intent(this@MediaDetailActivity, SeriesActivity::class.java)
                    intent.putExtra(Key.CATEGORY, data)
                    startActivity(intent)
                } else {
                    Utils.displayImage(this, data.thumbnail, R.drawable.video_placeholder, R.drawable.video_placeholder, playerThumbnail)
                    playerViewModel.refreshMediaMetadata(data)
                }
            }

            is Trailer -> {
                playVideoTrailer(data.media_url)
//                mediaMetadata?.trailers?.forEach {
//                    it.isSelected = it.id==data.id
//                }
                trailerListAdapter?.clear()
                trailerListAdapter?.addAll(mediaMetadata?.trailers)
            }
        }

    }

    override fun onPlayerEvent(playerTrackingEvent: PlayerTrackingEvent?) {
        playerTrackingEvent?.let {
            val eventData = java.lang.StringBuilder()
            mediaMetadata?.series?.let {
                eventData.append(it.tvseries_title)
                eventData.append(",Season " + it.season_no + ",")
            }
            eventData.append(mediaMetadata?.title)

            if (it.action == "playerstart") {
                if (!isPlayerstartSent){
                    TrackingUtils.sendVideoEvent(eventData.toString(), it.action)
                TrackingUtils.sendScreenTracker(BaseConstants.MEDIA_DETAILS, eventData.toString())
                isPlayerstartSent = true
            } else {
            }

        } else {
            TrackingUtils.sendVideoEvent(eventData.toString(), it.action)
            when (it.action) {
                "adplay" -> {
                    imgHeaderImage.gone()
                }
                "aderror" -> imgHeaderImage.visible()
                "adended" -> imgHeaderImage.visible()
                "play-100" -> {
                    mediaMetadata?.next_media?.let {
                        nextMediaMetadata?.let {
                            onGetVideoMetaDataSuccess(it)
                        }
                    }
                }
                else -> {
                }

            }
        }
    }

}

override fun onPlayerProgress(currentProgress: Long) {
    if (isTrailerPlaying) {
        return
    }
    mediaMetadata?.next_media?.let {

        if (it.next_episode_start_time > 0) {
            if (currentProgress in it.next_episode_start_time..phandoPlayerView.totalDuration) {
                nextMediaMetadata?.let {
                    if (nextEpisode.visibility != View.VISIBLE) {
                        nextEpisode.visible()
                    }
                }
            } else {
                if (nextEpisode.visibility == View.VISIBLE) {
                    nextEpisode.gone()
                }
            }
        }
    }
    mediaMetadata?.intro?.let {

        if (it.startTime > 0) {
            if (currentProgress in it.startTime..it.endTime - 1) {
                if (skipIntro.visibility != View.VISIBLE) {
                    skipIntro.visible()
                }
            } else {
                if (skipIntro.visibility == View.VISIBLE) {
                    skipIntro.gone()
                }
            }

        }
    }
}

override fun onDownloadStateChanged() {
    mediaMetadata?.media_url?.let {
        playerViewModel.refreshDownloadStatus(it)
    }

}

override fun updateSettingButton(enable: Boolean) {
    playerSetting.isEnabled = enable
}

override fun onConrolVisibilityChange(visibility: Int) {
    when (visibility) {
        View.VISIBLE -> {
            headerBack.visible()
            orientation?.visible()
            playerSetting.visible()
        }
        else -> {
            headerBack.invisible()
            orientation?.invisible()
            playerSetting.invisible()
            supportActionBar?.hide()
        }
    }

}

fun toggleHideyBar() {

    // BEGIN_INCLUDE (get_current_ui_flags)
    // The UI options currently enabled are represented by a bitfield.
    // getSystemUiVisibility() gives us that bitfield.
    val uiOptions: Int = getWindow().getDecorView().getSystemUiVisibility()
    var newUiOptions = uiOptions
    // END_INCLUDE (get_current_ui_flags)
    // BEGIN_INCLUDE (toggle_ui_flags)
    // Navigation bar hiding:  Backwards compatible to ICS.
    if (Build.VERSION.SDK_INT >= 14) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    // Status bar hiding: Backwards compatible to Jellybean
    if (Build.VERSION.SDK_INT >= 16) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    // Immersive mode: Backward compatible to KitKat.
    // Note that this flag doesn't do anything by itself, it only augments the behavior
    // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
    // all three flags are being toggled together.
    // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
    // Sticky immersive mode differs in that it makes the navigation and status bars
    // semi-transparent, and the UI flag does not get cleared when the user interacts with
    // the screen.
    if (Build.VERSION.SDK_INT >= 18) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    getWindow().getDecorView().setSystemUiVisibility(newUiOptions)
    //END_INCLUDE (set_ui_flags)
}

fun isFullScreen(): Boolean {
    return window.attributes.flags and
            WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
}

override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        hideSystemUI()
    }
}

private fun hideSystemUI() {
    // root.fitsSystemWindows = false
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

// Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
private fun showSystemUI() {
    // root.fitsSystemWindows = true
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
}

//Payment
private fun createOrder(paymentType: String, mediaId: String, type: String) {
    val map: MutableMap<String, String> = HashMap()
    map["payment_type"] = paymentType
    map["media_id"] = mediaId
    map["type"] = type

    subscriptionsViewModel.createOrder(map).observe(this, androidx.lifecycle.Observer {

        progressBar.gone()

        when (it?.status) {
            LoadingStatus.LOADING -> {
                progressBar.visible()
            }
            LoadingStatus.ERROR -> {
                progressBar.gone()
                it.message?.let {
                    Toast.makeText(this@MediaDetailActivity, it, Toast.LENGTH_LONG).show()
                }
            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                if (it.data?.is_subscribed == 1) {
                    playerViewModel.refreshMediaMetadata(playerViewModel.reloadTrigger.value)
                } else {
                    startPayment(it.data)
                }

            }

        }

    })
}

private fun startPayment(orderResponse: CreateOrderResponse?) {

    orderResponse?.let { createOrderResponse ->
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID(createOrderResponse.key)
        try {
            razorpayOrdertId = createOrderResponse.gateway_order_id
            val options = JSONObject()
            options.put("name", createOrderResponse.app_name)
            options.put("description", createOrderResponse.description)
            options.put("amount", (createOrderResponse.order_details.amount * 100).toString())
            options.put("order_id", createOrderResponse.gateway_order_id)

            val prefill = JSONObject()
            prefill.put("email", createOrderResponse.user_email)
            prefill.put("contact", createOrderResponse.user_mobile)

            options.put("prefill", prefill)

            co.open(activity, options)

        } catch (e: Exception) {
            razorpayOrdertId = null
            toast("Error in payment: " + e.message)
            e.printStackTrace()
        }
    } ?: toast("Error in payment. Unable to get order ")

}

override fun onPaymentError(p0: Int, p1: String?) {
    // Log.e("razorpay", "$p0 : $p1")
}

override fun onPaymentSuccess(razorpayPaymentId: String?) {
    if (razorpayPaymentId == null) {
        toast("Payment failed")
        return
    }
    //Log.e("razorpay", "$razorpayPaymentId")
    val map: MutableMap<String, String> = HashMap()
    map["razorpay_order_id"] = razorpayOrdertId!!
    map["razorpay_payment_id"] = razorpayPaymentId

    subscriptionsViewModel.updateOrderOnServer(map).observe(this@MediaDetailActivity, androidx.lifecycle.Observer {

        progressBar.gone()

        when (it?.status) {
            LoadingStatus.LOADING -> {
                progressBar.visible()
            }
            LoadingStatus.ERROR -> {
                progressBar.gone()
                it.message?.let {
                    toast(it)
                }
            }
            LoadingStatus.SUCCESS -> {
                progressBar.gone()
                it.data?.message?.let { it1 -> toast(it1) }
                if (it.data?.status.equals("success", true)) {
                    playerViewModel.refreshMediaMetadata(playerViewModel.reloadTrigger.value)
                }
            }

        }

    })
}


}
