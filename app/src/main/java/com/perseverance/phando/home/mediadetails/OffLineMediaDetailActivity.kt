package com.perseverance.phando.home.mediadetails

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.newgendroid.news.utils.AppDialogListener
import com.perseverance.patrikanews.utils.getViewModel
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.genericAdopter.AdapterClickListener
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
import com.perseverance.phando.utils.BaseRecycleMarginDecoration
import com.perseverance.phando.utils.DialogUtils
import com.perseverance.phando.utils.Util
import com.perseverance.phando.utils.Utils
import com.videoplayer.*
import com.videoplayer.VideoPlayerMetadata.UriSample
import kotlinx.android.synthetic.main.activity_video_details_offline.detailContent
import kotlinx.android.synthetic.main.activity_video_details_offline.fragmentContainer
import kotlinx.android.synthetic.main.activity_video_details_offline.phandoPlayerView
import kotlinx.android.synthetic.main.activity_video_details_offline.play
import kotlinx.android.synthetic.main.activity_video_details_offline.playerThumbnail
import kotlinx.android.synthetic.main.activity_video_details_offline.playerThumbnailContainer
import kotlinx.android.synthetic.main.activity_video_details_offline.root
import kotlinx.android.synthetic.main.activity_video_details_offline.toolbar
import kotlinx.android.synthetic.main.content_detail_offline.*

class OffLineMediaDetailActivity : AppCompatActivity(), AdapterClickListener, PhandoPlayerCallback {

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
        Utils.displayImage(this, downloadMetadata?.thumbnail, R.drawable.video_placeholder, R.drawable.video_placeholder, playerThumbnail)
        detailContent.visible()
        videoTitle.text = downloadMetadata?.title
        videoDescription.text = downloadMetadata?.description
        otherInfo.gone()


        mListener = object : SimpleOrientationEventListener(this@OffLineMediaDetailActivity) {
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
        setRelatedVideo()
        playVideo()
        download.setOnClickListener {
            DialogUtils.showDialog(this@OffLineMediaDetailActivity, "Alert!", "Do you want to delete saved video", "Yes", "No", object : AppDialogListener {
                override fun onNegativeButtonPressed() {

                }

                override fun onPositiveButtonPressed() {
                    downloadMetadataDao?.insert(downloadMetadata!!.apply {
                        status = 1
                    })
                    VideoSdkUtil.deleteDownloadedInfo(application, this@OffLineMediaDetailActivity.downloadMetadata?.media_url)
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

    }

    private fun playVideo() {
        downloadMetadata?.media_url?.let {
            play.gone()
            setDataToPlayer(addUrl = null, mediaUrl = downloadMetadata?.media_url!!, seekTo = 0)
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
                recyclerView.adapter = adapter
            } else {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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
        mListener?.enable()

    }

    override fun onPause() {
        super.onPause()
        mListener?.disable()

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

}
