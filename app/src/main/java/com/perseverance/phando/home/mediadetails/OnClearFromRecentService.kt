package com.perseverance.phando.home.mediadetails

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.exoplayer2.SimpleExoPlayer

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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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