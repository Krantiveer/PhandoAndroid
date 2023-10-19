package com.perseverance.phando.home.mediadetails

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.exoplayer2.SimpleExoPlayer
import com.perseverance.phando.home.mediadetails.CreateNotification.ACTION_PLAY
import com.perseverance.phando.home.mediadetails.CreateNotification.ACTION_STOP

class OnClearFromRecentService : Service() {


    private lateinit var player: SimpleExoPlayer

    override fun onCreate() {
        super.onCreate()
        // Set up ExoPlayer
        player = SimpleExoPlayer.Builder(this).build()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_PLAY -> {
                val mediaUri = intent?.data
                // Prepare and play the media using ExoPlayer
            }
            ACTION_STOP -> {
                // Stop and release the ExoPlayer
                player.stop()
                player.release()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        stopSelf()
    }
}