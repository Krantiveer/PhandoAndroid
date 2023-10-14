package com.perseverance.phando.home.mediadetails

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.patrikanews.utils.getViewModel
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.BaseScreenTrackingActivity
import com.perseverance.phando.R
import com.perseverance.phando.constants.BaseConstants
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
import com.perseverance.phando.utils.*
import com.videoplayer.*
import com.videoplayer.VideoPlayerMetadata.UriSample
import kotlinx.android.synthetic.main.activity_offline_media.*
import kotlinx.android.synthetic.main.activity_video_details_offline.*
import kotlinx.android.synthetic.main.activity_video_details_offline.detailContent
import kotlinx.android.synthetic.main.activity_video_details_offline.fragmentContainer
import kotlinx.android.synthetic.main.activity_video_details_offline.imgHeaderImage
import kotlinx.android.synthetic.main.activity_video_details_offline.phandoPlayerView
import kotlinx.android.synthetic.main.activity_video_details_offline.play
import kotlinx.android.synthetic.main.activity_video_details_offline.playerThumbnail
import kotlinx.android.synthetic.main.activity_video_details_offline.root
import kotlinx.android.synthetic.main.activity_video_details_offline.toolbar
import kotlinx.android.synthetic.main.audio_player_controller.*
import kotlinx.android.synthetic.main.content_detail_offline.*
import kotlinx.android.synthetic.main.content_detail_offline.recyclerView
import java.io.File

class OffLineMediaDetailActivity : BaseScreenTrackingActivity(), AdapterClickListener,
    PhandoPlayerCallback, Player.EventListener, Playable {
    override var screenName = "OffLineMediaDetail"
    var notificationManager: NotificationManager? = null

    var episodes: List<DownloadMetadata>? = ArrayList<DownloadMetadata>()

    companion object {
        const val ARG_VIDEO = "param_video"
        fun getDetailIntent(context: Context, downloadMetadata: DownloadMetadata): Intent {
            val intent = Intent(context, OffLineMediaDetailActivity::class.java)
            intent.apply {
                val arg = Bundle()
                arg.apply {
                    putSerializable(ARG_VIDEO, downloadMetadata)
                }
                putExtras(arg)
            }
            return intent

        }
    }

    val playerViewModel by lazy {
        getViewModel<MediaDetailViewModel>()
    }

    val messageObserver = Observer<String> {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }

    var mListener: SimpleOrientationEventListener? = null
    private val handler = Handler()
    private var downloadMetadata: DownloadMetadata? = null

    val downloadMetadataDao by lazy {
        AppDatabase.getInstance(this)?.downloadMetadataDao()
    }

     var audioPlayerExpo: SimpleExoPlayer? = null
    var isPlaying = false

    var position = 0
    var positionSong = 0
    var currentSongId: Int = 0


    private fun setDataToPlayer(addUrl: String? = null, mediaUrl: String, seekTo: Long = 0) {

        if (mediaUrl!!.endsWith(".mp3")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    createChannel()
                    registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
                    startService(Intent(baseContext, OnClearFromRecentService::class.java))
                }



            phandoPlayerView.onDestroy()
            Log.e("@@media_url", downloadMetadata!!.media_url!!)
            audioOfflineMedia.visible()
            download.visible()
            audioOfflineMedia.showController()
            audioOfflineMedia.controllerShowTimeoutMs = 0
            audioOfflineMedia.controllerHideOnTouch = false


            setAudioPlayer(
                mediaUrl,
                downloadMetadata!!.thumbnail.toString(),
                downloadMetadata!!.title.toString()
            )

        }
        else {

            videoTitle.text = downloadMetadata!!.title
            playerThumbnail.gone()
            releasePlayer()
            audioOfflineMedia.gone()
            phandoPlayerView.visible()
            play.gone()
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
                null
            )
            intent.putExtra(
                PhandoPlayerView.PREFER_EXTENSION_DECODERS_EXTRA, false
            )
            val abrAlgorithm = PhandoPlayerView.ABR_ALGORITHM_DEFAULT
            intent.putExtra(PhandoPlayerView.ABR_ALGORITHM_EXTRA, abrAlgorithm)
            intent.putExtra(PhandoPlayerView.TUNNELING_EXTRA, false)
            intent.putExtra(PhandoPlayerView.PLAYER_LOGO, R.mipmap.ic_launcher)
            intent.putExtra(PhandoPlayerView.KEY_POSITION, seekTo)
            sample.addToIntent(intent)
            phandoPlayerView.setVideoData(intent)
            //   phandoPlayerView.setDefaultArtwork(getDrawable(R.mipmap.ic_launcher))

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }

        setContentView(R.layout.activity_video_details_offline)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscope()
        } else {
            portrate()
        }






        val decoration = BaseRecycleMarginDecoration(this@OffLineMediaDetailActivity)
        recyclerView.addItemDecoration(decoration)
        playerViewModel.message.observe(this, messageObserver)
        downloadMetadata = intent?.getSerializableExtra(ARG_VIDEO) as DownloadMetadata
        Log.e("@@thumbnailData", downloadMetadata?.thumbnail.toString())
        Utils.displayImage(
            this,
            downloadMetadata?.thumbnail.toString(),
            R.drawable.video_placeholder,
            R.drawable.video_placeholder,
            playerThumbnail
        )
        detailContent.visible()
        videoTitle.text = downloadMetadata?.title
        videoDescription.text = downloadMetadata?.description
        otherInfo.gone()


        mListener = object : SimpleOrientationEventListener(this@OffLineMediaDetailActivity) {
            override fun onChanged(lastOrientation: Int, orientation: Int) {
                if (Settings.System.getInt(
                        contentResolver,
                        Settings.System.ACCELEROMETER_ROTATION,
                        0
                    ) != 1
                ) {
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
        setRelatedVideo()
        playVideo()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (downloadMetadata!!.media_url!!.endsWith(".mp3")){

                createChannel()
                registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
                startService(Intent(baseContext, OnClearFromRecentService::class.java))
            }

        }

        download.setOnClickListener {
            DialogUtils.showDialog(
                this@OffLineMediaDetailActivity,
                "Alert!",
                "Do you want to delete saved video",
                "Yes",
                "No",
                object : AppDialogListener {
                    override fun onNegativeButtonPressed() {

                    }

                    override fun onPositiveButtonPressed() {
                        downloadMetadataDao?.insert(downloadMetadata!!.apply {
                            status = 1
                        })
                        VideoSdkUtil.deleteDownloadedInfo(
                            application,
                            this@OffLineMediaDetailActivity.downloadMetadata?.media_url
                        )
                        Handler().postDelayed({
                            finish()
                        }, 1000)

                    }

                })
        }

        play.setOnClickListener {
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

      /*  exo_pause.setOnClickListener {
            if (isPlaying) {
                onTrackPause()
            } else {
                onTrackPlay()
            }
        }

        exo_play.setOnClickListener {
            if (isPlaying) {
                onTrackPause()

            } else {
                onTrackPlay()
            }
        }*/


    }

    private fun playVideo() {
        downloadMetadata?.media_url?.let {
            play.gone()
            setDataToPlayer(addUrl = null, mediaUrl = downloadMetadata?.media_url!!, seekTo = 0)
            currentSongId = downloadMetadata?.document_id!!.toInt()
        }
    }

    private fun setRelatedVideo() {

        downloadMetadataDao?.getAllDownloadLiveData()?.observe(this, Observer {
            if (it.isNotEmpty()) {
                relatedContainer.visible()
                val manager = GridLayoutManager(this@OffLineMediaDetailActivity, 2)
                recyclerView.layoutManager = manager
                recyclerView.setHasFixedSize(true)
                val adapter = SavedMediaListAdapter(this@OffLineMediaDetailActivity, this)
                adapter.items = it
                episodes = it

                episodes!!.forEachIndexed { index, element ->
                    if (element.document_id == currentSongId.toString()) {
                        Log.e("@@in", index.toString())
                        positionSong = index
                    }
                }
                recyclerView.adapter = adapter
                if (downloadMetadata!!.media_url!!.endsWith(".mp3")) {

                    if (isPlaying) {
                        onTrackPlay()
                    } else {
                        onTrackPause()
                    }

                } }
            else {
                    relatedContainer.gone()
                }


        })
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

    fun landscope() {
        root.fitsSystemWindows = false;
        root.requestApplyInsets()
        hideSystemUI()
        handler.post {
            val width = window.decorView.width
            val height = Util.getScreenHeight(this@OffLineMediaDetailActivity)
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
            val width = Util.getScreenWidthForVideo(this@OffLineMediaDetailActivity)
            val height = Util.getScreenHeightForVideo(this@OffLineMediaDetailActivity)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }

        }
        return true
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
        runOnUiThread {
            mListener?.enable()

            if (downloadMetadata!!.media_url!!.endsWith(".mp3")){

                if (audioPlayerExpo != null && isPlaying) {
                    audioPlayerExpo!!.seekTo(position.toLong())
                    audioPlayerExpo!!.playWhenReady = true
                }
            }

        }

    }

    override fun onPause() {
        super.onPause()
        mListener?.disable()

        if (downloadMetadata!!.media_url!!.endsWith(".mp3")){

            if (audioPlayerExpo != null && audioPlayerExpo!!.playWhenReady) {
                position = audioPlayerExpo!!.contentPosition.toInt()
                audioPlayerExpo!!.playWhenReady = true
            }

        }
    }

    override fun onItemClick(data: Any) {
        when (data) {
            is DownloadMetadata -> {
                downloadMetadata = data
                playVideo()
            }
        }

    }


    override fun onOrientationClicked() {
        changeOrientation()
    }

    override fun onSettingClicked() {
        val mBottomSheetDialog = BottomSheetDialog(this)
        val sheetView: View =
            layoutInflater.inflate(R.layout.bottomsheet_settings_control_options, null)
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


            if (it.action == "playerstart") {

            } else {

                when (it.action) {
                    "adplay" -> {
                        imgHeaderImage.gone()
                    }
                    "aderror" -> imgHeaderImage.visible()
                    "adended" -> imgHeaderImage.visible()
                    "play-100" -> {
                        onTrackNext()
                    }
                    else -> {
                    }
                }
            }
        }

    }

    override fun onPlayerProgress(time: Long) {

    }

    override fun onDownloadStateChanged() {
        downloadMetadata?.media_url?.let {
            playerViewModel.refreshDownloadStatus(it)
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

    fun setAudioPlayer(mediaUrl: String, thumbnail: String, title: String) {

        releasePlayer()
        Utils.displayImage(
            this,
            thumbnail,
            R.drawable.video_placeholder,
            R.drawable.video_placeholder,
            imgAudioThumbNail
        )

        videoTitle.text = title
        val renderersFactory = DefaultRenderersFactory(this)
        val trackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelectSelector = DefaultTrackSelector(trackSelectionFactory)
        val loadControl = DefaultLoadControl()

        audioPlayerExpo = ExoPlayerFactory.newSimpleInstance(
            this,
            renderersFactory,
            trackSelectSelector,
            loadControl
        )
        audioPlayerExpo!!.addListener(this)
        audioPlayerExpo!!.playWhenReady = true
        val dataSourceFactory = DefaultDataSourceFactory(this, getString(R.string.app_name))
        val extractorsFactory = DefaultExtractorsFactory()

        val mediaSource = ProgressiveMediaSource
            .Factory(dataSourceFactory, extractorsFactory)
            .createMediaSource(Uri.parse(mediaUrl))
        audioPlayerExpo!!.prepare(mediaSource)
        audioOfflineMedia.player = audioPlayerExpo
        isPlaying = true
        audioPlayerExpo!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                Log.e("@@state", playWhenReady.toString())
                if (playbackState == Player.STATE_ENDED) {

                    onTrackNext()
                }
                if (playbackState == Player.STATE_READY) {
                    progressBarAudio.gone()
                }
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBarAudio.visible()
                }
            }
        })
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.extras!!.getString("actionname")
            when (action) {
                CreateNotification.ACTION_PREVIUOS -> onTrackPrevious()
                CreateNotification.ACTION_PLAY -> if (isPlaying) {
                    onTrackPause()
                } else {
                    onTrackPlay()
                }
                CreateNotification.ACTION_NEXT -> onTrackNext()
            }
        }
    }


    override fun onTrackPrevious() {
        positionSong--
        CreateNotification.createNotificationDownload(
            this@OffLineMediaDetailActivity, episodes!!.get(positionSong),
            R.drawable.player_pause, positionSong, episodes!!.size - 1
        )
        setAudioPlayer(
            episodes!!.get(positionSong).media_url.toString(),
            episodes!!.get(positionSong).thumbnail.toString(),
            episodes!!.get(positionSong).title.toString()
        )
        /* mediaMetadata?.prev_media?.let {
             prevMediaMetadata?.let {
                 onGetVideoMetaDataSuccess(it)
             }
         }*/

    }

    override fun onTrackPlay() {
        CreateNotification.createNotificationDownload(
            this@OffLineMediaDetailActivity, episodes!!.get(positionSong),
            R.drawable.player_pause, positionSong, episodes!!.size - 1
        )
        play.setImageResource(R.drawable.player_play)

        isPlaying = true

        if (audioPlayerExpo != null) {
            audioPlayerExpo!!.seekTo(position.toLong())
            audioPlayerExpo!!.playWhenReady = true
        }
    }

    override fun onTrackPause() {
        if (downloadMetadata != null) {
            CreateNotification.createNotificationDownload(
                this@OffLineMediaDetailActivity, episodes!!.get(positionSong),
                R.drawable.ic_play_arrow_black_24dp, positionSong, episodes!!.size - 1
            )
            play.setImageResource(R.drawable.ic_play_arrow_black_24dp)
        }
        isPlaying = false
        if (audioPlayerExpo != null && audioPlayerExpo!!.getPlayWhenReady()) {
            position = audioPlayerExpo!!.contentPosition.toInt()
            audioPlayerExpo!!.playWhenReady = false
        }
    }

    override fun onTrackNext() {
        Log.e("@@next", positionSong.toString())
        positionSong++

        if (positionSong < episodes!!.size) {

            if (episodes!![positionSong].media_url!!.endsWith(".mp3")){
                CreateNotification.createNotificationDownload(
                    this@OffLineMediaDetailActivity, episodes!!.get(positionSong),
                    R.drawable.player_pause, positionSong, episodes!!.size - 1)

                setAudioPlayer(
                    episodes!!.get(positionSong).media_url.toString(),
                    episodes!!.get(positionSong).thumbnail.toString(),
                    episodes!!.get(positionSong).title.toString()
                )
            }  else {
                playVideo()
            }


        } else {
            // Handle the case when the current song is the last song
            // You can choose to restart the playlist or perform any other desired action
        }
    }


    /* override fun onTrackNext() {
         Log.e("@@next", positionSong.toString())
         positionSong++
         CreateNotification.createNotificationDownload(
             this@OffLineMediaDetailActivity, episodes!!.get(positionSong),
             R.drawable.player_pause, positionSong, episodes!!.size - 1
         )

         setAudioPlayer(
             episodes!!.get(positionSong).media_url.toString(),
             episodes!!.get(positionSong).thumbnail.toString(),
             episodes!!.get(positionSong).title.toString()
         )
         *//* mediaMetadata?.next_media?.let {
                nextMediaMetadata?.let {
                    onGetVideoMetaDataSuccess(it)
                }
            }*//*
    }*/

    override fun onStop() {
        super.onStop()
        if (downloadMetadata!!.media_url!!.endsWith(".mp3")){
            if ( audioPlayerExpo != null) {
                releasePlayer()
            }
        }

    }

    private fun releasePlayer() {
        try {
            if ( audioPlayerExpo != null) {
                audioPlayerExpo?.release()
            }
        } catch (e: Exception) {

        }
    }

    private fun createChannel() {

       /* if (downloadMetadata!!.media_url!!.endsWith(".mp3")) {

            if (isPlaying) {
                onTrackPlay()
            } else {
                onTrackPause()
            }

        }*/
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_HIGH
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager != null) {
                notificationManager!!.createNotificationChannel(channel)
            }

    }

    override fun onDestroy() {
        super.onDestroy()

        if (downloadMetadata!!.media_url!!.endsWith(".mp3")){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (notificationManager != null) {
                    notificationManager!!.cancelAll()
                    unregisterReceiver(broadcastReceiver)
                }
            }

        }

    }

}
