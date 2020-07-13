package com.perseverance.phando.home.mediadetails

import android.annotation.SuppressLint
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
import android.text.Spannable
import android.text.Spanned
import android.text.style.StrikethroughSpan
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.perseverance.patrikanews.utils.*
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.FeatureConfigClass
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
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
import com.perseverance.phando.home.profile.login.LoginActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.videoplayer.*
import com.videoplayer.VideoPlayerMetadata.UriSample
import kotlinx.android.synthetic.main.activity_video_details.*
import kotlinx.android.synthetic.main.activity_video_details.progressBar
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.android.synthetic.main.content_detail.recyclerView
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MediaDetailActivity : AppCompatActivity(), AdapterClickListener, PhandoPlayerCallback, PaymentResultListener, PurchaseOptionSelection {
    private var razorpayOrdertId: String? = null
    val STRIKE_THROUGH_SPAN = StrikethroughSpan()
    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(this)?.downloadMetadataDao()
    }

    companion object {
        const val LOGIN_FOR_RENT = 1
        const val LOGIN_FOR_BUY = 2
        const val LOGIN_FOR_PACKAGE = 3

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
        fun getDetailIntent(context: Context, video: Video): Intent {
            if (video.is_free == 0 && PreferencesUtils.getLoggedStatus().isEmpty()) {
                val intent = Intent(context, LoginActivity::class.java)
                return intent
            } else {
                val intent = Intent(context, MediaDetailActivity::class.java)

                intent.apply {
                    val arg = Bundle()
                    arg.apply {
                        putParcelable(ARG_VIDEO, video)
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

    val mediaDetailViewModel by lazy {
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
                    imgDownload.setImageResource(R.drawable.ic_detail_download_inprogress)
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
        nextEpisode.gone()
        playerThumbnailContainer.gone()
        phandoPlayerView.visible()
        val intent = Intent()
        val uri = Uri.parse(mediaUrl)
        // val uri = Uri.parse("https://seventv.livebox.co.in/sevenwonderstvhls/live.m3u8")

        val subtitleUri = ArrayList<String>()
        mediaMetadata?.cc_files?.let {
            if (it.isNotEmpty()) {
                it.forEach { ccInfo ->
                    subtitleUri.add("${ccInfo.url},${ccInfo.mime_type},${ccInfo.language_code}")
                }

            }
        }
        var isLive = false
        mediaMetadata?.is_live?.let {
            isLive = it == 1
        }

        val subtitleInfo: VideoPlayerMetadata.SubtitleInfo? = if (subtitleUri == null || subtitleUri.isEmpty()) null else VideoPlayerMetadata.SubtitleInfo(
                subtitleUri,
                "application/ttml+xml",
                "en")
        val sample: VideoPlayerMetadata = UriSample(
                null,
                uri,
                null,
                isLive,
                null,
                if (addUrl.isNullOrEmpty()) null else Uri.parse(addUrl),
                null,
                subtitleInfo)
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
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscope()
        } else {
            portrate()
        }

        val decoration = BaseRecycleMarginDecoration(this@MediaDetailActivity)
        recyclerView.addItemDecoration(decoration)

        mediaDetailViewModel.getVideoDetailMutableLiveData().observe(this, videoMetadataModelObserver)
        mediaDetailViewModel.getNextEpisodeVideoDetailMutableLiveData().observe(this, nextVideoMetadataModelObserver)
        mediaDetailViewModel.message.observe(this, messageObserver)

        mediaDetailViewModel.isInWishlist.observe(this, favoriteObserver)
        mediaDetailViewModel.isLiked.observe(this, likeObserver)
        mediaDetailViewModel.isDisliked.observe(this, dislikeObserver)
        mediaDetailViewModel.downloadStatus.observe(this, downloadObserver)

        val video: Video = intent?.getParcelableExtra(ARG_VIDEO)!!
        mediaDetailViewModel.refreshMediaMetadata(video)
        MyLog.d("Player Image", video.thumbnail)
        Utils.displayImage(this, video.thumbnail, R.drawable.video_placeholder, R.drawable.video_placeholder, playerThumbnail)
        favorite.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                mediaDetailViewModel.reloadTrigger.value?.let {

                    mediaDetailViewModel.addToMyList(it.id.toString(), it.type!!)
                }
            }

        }
        like.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                mediaDetailViewModel.reloadTrigger.value?.let {
                    mediaDetailViewModel.addToLike(it.id.toString(), it.type!!)
                }
            }

        }
//        dislike.setOnClickListener {
//            playerViewModel.reloadTrigger.value?.let {
//                playerViewModel.addToDislike(it.entryId, it.type)
//            }
//        }
        if (BuildConfig.APPLICATION_ID == "com.perseverance.anvitonmovies") {
            share.gone()
        }
        share.setOnClickListener {
            shareVideoUrl()
        }

        download.setOnClickListener {
            val token = PreferencesUtils.getLoggedStatus()
            if (token.isEmpty()) {
                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)

                return@setOnClickListener
            }
            when (mediaplaybackData.mediaCode) {
                "package_media", "rent_or_buy", "only_rent", "only_buy" -> {
                    toast("This is a premium content. Please purchase to download.")
                    return@setOnClickListener
                }
            }
            mediaDetailViewModel.downloadStatus.value?.let {
                val mBottomSheetDialog = BottomSheetDialog(this)
                val sheetView: View = layoutInflater.inflate(R.layout.fragment_download_control_options, null)
                mBottomSheetDialog.setContentView(sheetView)
                val downloadDelete = sheetView.findViewById<TextView>(R.id.downloadDelete)
                val downloadResume = sheetView.findViewById<TextView>(R.id.downloadResume)
                val downloadStop = sheetView.findViewById<TextView>(R.id.downloadStop)
                downloadDelete.setOnClickListener {
                    deleteDownload()
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
                    }
                    STATE_DOWNLOADING -> {
                        downloadDelete.text = "Cancel"
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
                }
                mBottomSheetDialog.show()
            } ?: run {
                startDownload()

            }
        }
        play.setOnClickListener {

            playVideo()

        }
        watchNow.setOnClickListener {
            playVideo()


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

                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_RENT)


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
                startActivityForResult(intent, REQUEST_CODE_BUY)

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

                val intent = Intent(this@MediaDetailActivity, LoginActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_PACKAGE)
            } else {
                val intent = Intent(this@MediaDetailActivity, SubscriptionPackageActivity::class.java)
                startActivityForResult(intent, LoginActivity.REQUEST_CODE_LOGIN)
            }
        }

    }

    private fun startDownload() {
        mediaMetadata?.media_url?.let {
            if (it.isNullOrEmpty()) {
                return@let
            }
            progressBar.visible()
            val param = HashMap<String, String>()
            param["document_id"] = mediaMetadata?.document_media_id!!.toString()
            mediaDetailViewModel.saveUserDownload(param).observe(this, Observer {
                val result = it ?: return@Observer

                when (result.status) {
                    LoadingStatus.SUCCESS -> {
                        val videoPlayerMetadata = UriSample(
                                null,
                                Uri.parse(mediaMetadata?.media_url),
                                null,
                                false,
                                null,
                                null,
                                null,
                                null)
                        VideoSdkUtil.startDownload(this@MediaDetailActivity, videoPlayerMetadata, mediaMetadata?.title)
                        downloadMetadataDao?.insert(DownloadMetadata(mediaMetadata?.document_media_id!!.toString(),
                                mediaMetadata?.title,
                                mediaMetadata?.detail,
                                mediaMetadata?.thumbnail,
                                mediaMetadata?.media_url

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

    private fun deleteDownload() {
        mediaMetadata?.media_url?.let {
            if (it.isNullOrEmpty()) {
                return@let
            }
            progressBar.visible()
            val param = ArrayList<String>()
            param.add(mediaMetadata?.document_media_id!!.toString())
            mediaDetailViewModel.removeUserDownload(param).observe(this, Observer {
                val result = it ?: return@Observer
                progressBar.gone()
                when (result.status) {
                    LoadingStatus.SUCCESS -> {
                        downloadMetadataDao?.deleteById(mediaMetadata?.document_media_id.toString())
                        VideoSdkUtil.deleteDownloadedInfo(application, mediaMetadata?.media_url)
                    }
                    LoadingStatus.ERROR -> {
                        result.message?.let { it1 -> toast(it1) }
                    }
                }
            })

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

                    if (it.discount_percentage > 0) {

                        val discount = (it.value * it.discount_percentage) / 100
                        when (it.key) {
                            "rent_price" -> {
                                val originalPrice = "Rent at INR  ${number2digits(it.value)}/-"
                                rentMedia.setText("$originalPrice  \n${number2digits(it.value - discount)}/-", TextView.BufferType.SPANNABLE)
                                val spannable = rentMedia.getText() as Spannable
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 12, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                                rentMedia.tag = it
                            }
                            "purchase_price" -> {
                                val originalPrice = "Buy at INR  ${number2digits(it.value)}/-"
                                buyMedia.setText("$originalPrice  \n${number2digits(it.value - discount)}/-", TextView.BufferType.SPANNABLE)
                                val spannable = buyMedia.getText() as Spannable
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 11, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                buyMedia.tag = it
                            }
                        }

                    } else {
                        when (it.key) {
                            "rent_price" -> {
                                rentMedia.text = "Rent at INR ${number2digits(it.value)}/-"
                                rentMedia.tag = it
                            }
                            "purchase_price" -> {
                                buyMedia.text = "Buy at INR ${number2digits(it.value)}/-"
                                buyMedia.tag = it
                            }
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
                            if (it.discount_percentage > 0) {
                                val originalPrice = "Rent at INR  ${number2digits(it.value)}/-"
                                val discount = (it.value * it.discount_percentage) / 100
                                rentMedia.setText("$originalPrice  \n${number2digits(it.value - discount)}/-", TextView.BufferType.SPANNABLE)
                                val spannable = rentMedia.getText() as Spannable
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 12, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                            } else {
                                rentMedia.text = "Rent at INR ${number2digits(it.value)}/-"
                                rentMedia.tag = it
                            }
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
                            if (it.discount_percentage > 0) {
                                val originalPrice = "Buy at INR  ${number2digits(it.value)}/-"
                                val discount = (it.value * it.discount_percentage) / 100
                                buyMedia.setText("$originalPrice  \n${number2digits(it.value - discount)}/-", TextView.BufferType.SPANNABLE)
                                val spannable = buyMedia.getText() as Spannable
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 11, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                            } else {
                                buyMedia.text = "Buy at INR ${number2digits(it.value)}/-"
                                buyMedia.tag = it
                            }

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

                    if (it.discount_percentage > 0) {
                        val discount = (it.value * it.discount_percentage) / 100
                        when (it.key) {
                            "rent_price" -> {
                                val originalPrice = "Rent at INR  ${number2digits(it.value)}/-"
                                rentMedia.setText("$originalPrice  \n${number2digits(it.value - discount)}/-", TextView.BufferType.SPANNABLE)
                                val spannable = rentMedia.getText() as Spannable
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 12, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                                rentMedia.tag = it
                                rentMedia.isEnabled = false
                            }
                            "purchase_price" -> {
                                val originalPrice = "Buy at INR  ${number2digits(it.value)}/-"
                                buyMedia.setText("$originalPrice  \n${number2digits(it.value - discount)}/-", TextView.BufferType.SPANNABLE)
                                val spannable = buyMedia.getText() as Spannable
                                spannable.setSpan(STRIKE_THROUGH_SPAN, 11, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                                buyMedia.tag = it
                            }
                        }

                    } else {
                        when (it.key) {
                            "rent_price" -> {
                                rentMedia.text = "Rent at INR ${number2digits(it.value)}/-"
                                rentMedia.tag = it
                                rentMedia.isEnabled = false
                            }
                            "purchase_price" -> {
                                buyMedia.text = "Buy at INR ${number2digits(it.value)}/-"
                                buyMedia.tag = it
                            }
                        }
                    }

                }
            }
        }
        actionControlers.visible()
        this.mediaMetadata = mediaplaybackData.data
        mediaMetadata?.is_live?.let {
            download.isEnabled = it == 0
            if (it == 1) {
                txtPlay.text = "Go Live"
            }
        }
        mediaDetailViewModel.refreshDownloadStatus(mediaMetadata?.media_url!!)
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
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onChanged(lastOrientation: Int, orientation: Int) {
                if (Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
                    return
                }

                // Toast.makeText(this@MediaDetailActivity,""+getResources().getConfiguration().orientation,Toast.LENGTH_SHORT).show()
                when (orientation) {
                    ORIENTATION_LANDSCAPE -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                    }
                    ORIENTATION_LANDSCAPE_REVERSE -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    }
                    ORIENTATION_PORTRAIT -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    }
                    ORIENTATION_PORTRAIT_REVERSE -> {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    }
                }

            }
        }
        mListener?.enable()

        mediaDetailViewModel.isInWishlist.postValue(mediaMetadata?.is_wishlist)
        mediaDetailViewModel.isLiked.postValue(mediaMetadata?.is_like)
        mediaDetailViewModel.isDisliked.postValue(mediaMetadata?.is_dislike)
        setRelatedVideo()


        if (play.visibility != View.VISIBLE) {
            playVideo()
        } else {
            if (mediaMetadata!!.last_watch_time > 0 || mediaMetadata?.is_live == 1) {
                playVideo()
            }
        }
        mediaMetadata?.next_media?.let {
            mediaDetailViewModel.getNextEpisodeMediaMetadata(Video().apply {
                type = it.type
                id = it.id
            })
        }

//        when (mediaplaybackData.mediaCode) {
//            "free", "rented", "buyed", "package_purchased" -> {
//
//            }
//            else -> {
//                when (mediaDetailViewModel.loginFor.value) {
//                    LOGIN_FOR_BUY -> {
//                        buyMedia.performClick()
//                    }
//                    LOGIN_FOR_RENT -> {
//                        rentMedia.performClick()
//                    }
//                    LOGIN_FOR_PACKAGE -> {
//                        packageMedia.performClick()
//                    }
//                }
//            }
//        }
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

    @SuppressLint("SourceLockedOrientationActivity")
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

    fun landscope() {
        root.fitsSystemWindows = false;
        root.requestApplyInsets()
        hideSystemUI()
        handler.post {
            val width = window.decorView.width
            val height = Util.getScreenHeight(this@MediaDetailActivity)
            fragmentContainer.requestLayout()
            fragmentContainer.layoutParams.height = height
            fragmentContainer.layoutParams.width = width
        }
    }

    fun portrate() {
        root.fitsSystemWindows = true;
        root.requestApplyInsets()
        showSystemUI()
        handler.post {
            val width = Util.getScreenWidthForVideo(this@MediaDetailActivity)
            val height = Util.getScreenHeightForVideo(this@MediaDetailActivity)
            fragmentContainer.requestLayout()
            fragmentContainer.layoutParams.height = height
            fragmentContainer.layoutParams.width = width
        }
        setRelatedVideo()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handler.removeCallbacksAndMessages(null)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                landscope()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                portrate()

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
        mediaDetailViewModel.reloadTrigger.value?.let {
            val title = videoTitle.text.toString()
            val mediaType = it.type
            it.id?.let {
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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onBackPressed() {
        when (resources.configuration.orientation) {
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
                    mediaDetailViewModel.setContinueWatchingTime(mediaMetaData?.document_media_id.toString(), it.toString())
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoginActivity.REQUEST_CODE_LOGIN -> {
                if (resultCode == Activity.RESULT_OK) {
                    mediaDetailViewModel.refreshMediaMetadata(mediaDetailViewModel.reloadTrigger.value)
                    mediaDetailViewModel.loginFor.value= 0
                } else {

                }
            }

            REQUEST_CODE_RENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    mediaDetailViewModel.refreshMediaMetadata(mediaDetailViewModel.reloadTrigger.value)
                    mediaDetailViewModel.loginFor.value= LOGIN_FOR_RENT
                } else {

                }
            }
            REQUEST_CODE_BUY -> {
                if (resultCode == Activity.RESULT_OK) {
                    mediaDetailViewModel.refreshMediaMetadata(mediaDetailViewModel.reloadTrigger.value)
                    mediaDetailViewModel.loginFor.value= LOGIN_FOR_BUY
                } else {

                }
            }
            REQUEST_CODE_PACKAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    mediaDetailViewModel.refreshMediaMetadata(mediaDetailViewModel.reloadTrigger.value)
                    mediaDetailViewModel.loginFor.value= LOGIN_FOR_PACKAGE
                } else {

                }
            }
        }


    }

    override fun onItemClick(data: Any) {
        updateCurrentPositionOnServer()
        when (data) {
            is Video -> {

                if ("T".equals(data.type)) {
                    val intent = Intent(this@MediaDetailActivity, SeriesActivity::class.java)
                    intent.putExtra(Key.CATEGORY, data)
                    startActivity(intent)
                } else {
                    Utils.displayImage(this, data.thumbnail, R.drawable.video_placeholder, R.drawable.video_placeholder, playerThumbnail)
                    mediaDetailViewModel.refreshMediaMetadata(data)
                    mediaDetailViewModel.loginFor.value= 0
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

    override fun onOrientationClicked() {
        changeOrientation()
    }

    override fun onSettingClicked() {
        val mBottomSheetDialog = BottomSheetDialog(this)
        val sheetView: View = layoutInflater.inflate(R.layout.bottomsheet_settings_control_options, null)
        mBottomSheetDialog.setContentView(sheetView)
        val settingVideoQuality = sheetView.findViewById<TextView>(R.id.settingVideoQuality)
        val settingVideoCC = sheetView.findViewById<TextView>(R.id.settingVideoCC)
        settingVideoQuality.setOnClickListener {
            phandoPlayerView.openVideoSettings()
            mBottomSheetDialog.dismiss()
        }
        settingVideoCC.setOnClickListener {
            phandoPlayerView.openCCSettings()
            mBottomSheetDialog.dismiss()
        }
        mBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        mBottomSheetDialog.show()
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
                if (!isPlayerstartSent) {
                    TrackingUtils.sendVideoEvent(eventData.toString(), mediaMetadata?.analytics_category_id, it.action)
                    TrackingUtils.sendScreenTracker(BaseConstants.MEDIA_DETAILS, eventData.toString())
                    isPlayerstartSent = true
                } else {
                }
            } else {
                TrackingUtils.sendVideoEvent(eventData.toString(), mediaMetadata?.analytics_category_id, it.action)
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
            mediaDetailViewModel.refreshDownloadStatus(it)
        }

    }

    override fun updateSettingButton(enable: Boolean) {

    }

    override fun onConrolVisibilityChange(visibility: Int) {
        when (visibility) {
            View.VISIBLE -> {
                supportActionBar?.show()
            }
            else -> {
                supportActionBar?.hide()
            }
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
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

    private fun showSystemUI() {
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
                        mediaDetailViewModel.refreshMediaMetadata(mediaDetailViewModel.reloadTrigger.value)
                        mediaDetailViewModel.loginFor.value= 0
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
                        mediaDetailViewModel.refreshMediaMetadata(mediaDetailViewModel.reloadTrigger.value)
                        mediaDetailViewModel.loginFor.value= 0
                    }
                }

            }

        })
    }

    fun number2digits(number: Float): String {
        return String.format("%.2f", number)
    }
}
