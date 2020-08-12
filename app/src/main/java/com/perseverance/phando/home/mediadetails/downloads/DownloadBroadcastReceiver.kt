package com.perseverance.phando.home.mediadetails.downloads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadBroadcastReceiver : BroadcastReceiver() {
    val downloadBroadcastReceiverListener: DownloadBroadcastReceiverListener? = null
    override fun onReceive(context: Context, intent: Intent) {

        downloadBroadcastReceiverListener?.doWork(intent)
    }

    interface DownloadBroadcastReceiverListener {
        fun doWork(intent: Intent?)
    }
}