package com.perseverance.phando.home.series

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.perseverance.patrikanews.utils.gone
import com.perseverance.patrikanews.utils.visible
import com.perseverance.phando.R
import com.perseverance.phando.home.dashboard.repo.DataLoadingStatus
import com.perseverance.phando.home.dashboard.repo.LoadingStatus
import kotlinx.android.synthetic.main.activity_audio_series_list.*

class AudioSeriesList : AppCompatActivity() {
    var songAdapter: SongAdapter? = null
    var allSongs: List<Episode> = ArrayList()
    var player: ExoPlayer? = null

    private val videoListViewModelObserver =
        Observer<DataLoadingStatus<TVSeriesResponseDataNew>> { it ->
            progressBar.gone()
            when (it?.status) {
                LoadingStatus.LOADING -> {
                    progressBar.visible()
                }
                LoadingStatus.ERROR -> {
                    it.message?.let {
                        Toast.makeText(this@AudioSeriesList, it, Toast.LENGTH_LONG).show()
                    }
                }
                LoadingStatus.SUCCESS -> {
                    it.data?.let { tvSeriesResponse ->
                        onGetVideosSuccess(tvSeriesResponse)
                    }
                }
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem: MenuItem = menu!!.findItem(R.id.action_search)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_series_list)
        val viewModel = ViewModelProvider(this)[SeriesViewModel::class.java]

        // setToolbar
        setSupportActionBar(toolBar)
        supportActionBar!!.title = "AudioList"

        viewModel.callForSeries("241").observe(this, videoListViewModelObserver)

        val trackSelector = DefaultTrackSelector()

// Create a DefaultLoadControl

// Create a DefaultLoadControl
        val loadControl = DefaultLoadControl()

// Build the ExoPlayer instance

// Build the ExoPlayer instance
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()


    }

    private fun onGetVideosSuccess(tvSeriesResponseData: TVSeriesResponseDataNew) {

        allSongs = tvSeriesResponseData.seasons.get(0).episodes
        songAdapter = SongAdapter(this, allSongs, player)
        recyclerViewList.adapter = songAdapter
    }


}