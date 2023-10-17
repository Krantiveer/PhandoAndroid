package com.perseverance.phando.home.mediadetails

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.perseverance.phando.R
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata

import kotlinx.coroutines.SupervisorJob


object CreateNotification {
    const val CHANNEL_ID = "channel1"
    const val ACTION_PREVIUOS = "actionprevious"
    const val ACTION_PLAY = "actionplay"
    const val ACTION_NEXT = "actionnext"
    var notification: Notification? = null
     fun createNotification(
        context: Context?,
        track: RelatedEpisode,
        playbutton: Int,
        pos: Int,
        size: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val notificationManagerCompat = NotificationManagerCompat.from(
                context!!
            )
            val mediaSessionCompat = MediaSessionCompat(context, "tag")
            val pendingIntentPrevious: PendingIntent?
            val drw_previous: Int
            if (pos == 0) {
                pendingIntentPrevious = null
                drw_previous = 0
            } else {
                val intentPrevious = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_PREVIUOS)
                pendingIntentPrevious = PendingIntent.getBroadcast(
                    context, 0,
                    intentPrevious, PendingIntent.FLAG_IMMUTABLE
                )
                drw_previous = R.drawable.exo_icon_previous
            }
            val intentPlay = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context, 0,
                intentPlay, PendingIntent.FLAG_IMMUTABLE
            )
            val pendingIntentNext: PendingIntent?
            val drw_next: Int
            if (pos == size) {
                pendingIntentNext = null
                drw_next = 0
            } else {
                val intentNext = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(
                    context, 0,
                    intentNext, PendingIntent.FLAG_IMMUTABLE
                )
                drw_next = R.drawable.exo_icon_next
            }
            //resolveUriAsBitmap(Uri.parse(track.thumbnail), context)


            val notificationLayout = RemoteViews(context.packageName, R.layout.custom_notification_layout)

            Log.e("@@thumbnail", track.thumbnail)


            notificationLayout.setTextViewText(R.id.tvAudioNameNotif, track.title)
            notificationLayout.setTextViewText(R.id.tvAppTitle, "Vyas")
            notificationLayout.setImageViewResource(R.id.btnPlayPauseNotif, R.drawable.ic_play)
            notificationLayout.setImageViewResource(R.id.iViewNotif,R.drawable.app_logo)

            notificationLayout.setProgressBar(R.id.seekBarNotif, 100, 10, false)

            val playPauseIntent = pendingIntentPlay

            notificationLayout.setOnClickPendingIntent(R.id.btnPlayPauseNotif, playPauseIntent)



            notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo) //  .setLargeIcon(bitmapFinal)
               .setContentTitle(track.title)
             //   .setLargeIcon(BitmapFactory.decodeResource(context.resources,
             //       R.drawable.app_logo))
              //  .setCustomContentView(notificationLayout)
               // .setCustomBigContentView(notificationLayout)
             //   .setLargeIcon( resolveUriAsBitmap(Uri.parse(track.thumbnail), context))
                .setContentText(track.detail)
               .setOnlyAlertOnce(true) //show notification for only first time
                .setShowWhen(false)
                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction(playbutton, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManagerCompat.notify(1, notification!!)
        }
    }

    fun createNotificationDownload(
        context: Context?,
        track: DownloadMetadata,
        playbutton: Int,
        pos: Int,
        size: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val notificationManagerCompat = NotificationManagerCompat.from(
                context!!
            )
            val mediaSessionCompat = MediaSessionCompat(context, "tag")
            val pendingIntentPrevious: PendingIntent?
            val drw_previous: Int
            if (pos == 0) {
                pendingIntentPrevious = null
                drw_previous = 0
            } else {
                val intentPrevious = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_PREVIUOS)
                pendingIntentPrevious = PendingIntent.getBroadcast(
                    context, 0,
                    intentPrevious, PendingIntent.FLAG_IMMUTABLE
                )
                drw_previous = R.drawable.exo_icon_previous
            }
            val intentPlay = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context, 0,
                intentPlay, PendingIntent.FLAG_IMMUTABLE
            )
            val pendingIntentNext: PendingIntent?
            val drw_next: Int
            if (pos == size) {
                pendingIntentNext = null
                drw_next = 0
            } else {
                val intentNext = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(
                    context, 0,
                    intentNext, PendingIntent.FLAG_IMMUTABLE
                )
                drw_next = R.drawable.exo_icon_next
            }
            //resolveUriAsBitmap(Uri.parse(track.thumbnail), context)


            val notificationLayout = RemoteViews(context.packageName, R.layout.custom_notification_layout)

          //  Log.e("@@thumbnail", track.thumbnail)


            notificationLayout.setTextViewText(R.id.tvAudioNameNotif, track.title)
            notificationLayout.setTextViewText(R.id.tvAppTitle, "Vyas")
            notificationLayout.setImageViewResource(R.id.btnPlayPauseNotif, R.drawable.ic_play)
            notificationLayout.setImageViewResource(R.id.iViewNotif,R.drawable.app_logo)

            notificationLayout.setProgressBar(R.id.seekBarNotif, 100, 10, false)

            val playPauseIntent = pendingIntentPlay

            notificationLayout.setOnClickPendingIntent(R.id.btnPlayPauseNotif, playPauseIntent)



            notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo) //  .setLargeIcon(bitmapFinal)
               .setContentTitle(track.title)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                    R.drawable.app_logo))
              //  .setCustomContentView(notificationLayout)
               // .setCustomBigContentView(notificationLayout)
             //   .setLargeIcon( resolveUriAsBitmap(Uri.parse(track.thumbnail), context))
              //  .setContentText(track)
              // .setOnlyAlertOnce(true) //show notification for only first time
                .setShowWhen(false)
                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction(playbutton, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManagerCompat.notify(1, notification!!)
        }
    }

    const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.app_logo)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    private fun setPlayPauseButtonState(contentView: RemoteViews, isPlaying: Boolean) {
        val buttonIconRes = if (isPlaying) R.drawable.player_pause else R.drawable.ic_play
        contentView.setImageViewResource(R.id.btnPlayPauseNotif, buttonIconRes)
    }

    // Function to create a PendingIntent for the play/pause button click
    private fun createPlayPauseIntent(context: Context): PendingIntent {
        val playPauseIntent = Intent(context, NotificationActionService::class.java)
        playPauseIntent.action = "PLAY_PAUSE_ACTION" // Replace with your desired action

        return PendingIntent.getService(context, 0, playPauseIntent, 0)
    }


}