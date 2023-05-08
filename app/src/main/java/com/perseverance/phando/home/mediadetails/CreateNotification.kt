package com.perseverance.phando.home.mediadetails

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.perseverance.phando.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.URL

object CreateNotification {
    const val CHANNEL_ID = "channel1"
    const val ACTION_PREVIUOS = "actionprevious"
    const val ACTION_PLAY = "actionplay"
    const val ACTION_NEXT = "actionnext"
    var notification: Notification? = null
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    private val builder: NotificationCompat.Builder? = null
    private var bitmapFinal: Bitmap? = null
     fun createNotification(
        context: Context?,
        track: RelatedEpisode,
        playbutton: Int,
        pos: Int,
        size: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            /* DownloadImageTask task = new DownloadImageTask(context, 101, track, playbutton, pos, size);
            task.execute(track.getThumbnail());*/
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

            notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo) //  .setLargeIcon(bitmapFinal)
                .setContentTitle(track.title)
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

/*    private class DownloadImageTask(
        private val mContext: Context,
        private val mNotificationId: Int,
        private val trackm: RelatedEpisode,
        private val playbuttonm: Int,
        private val posm: Int,
        private val sizem: Int
    ) : AsyncTask<String?, Void?, Bitmap?>() {
        protected override fun doInBackground(vararg urls: String): Bitmap? {
            val imageUrl = urls[0]
            var bitmap: Bitmap? = null
            try {
                val `in` = URL(imageUrl).openStream()
                bitmap = BitmapFactory.decodeStream(`in`)
                bitmapFinal = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                notification!!.largeIcon = result
                *//*    Intent intent = new Intent(mContext, MediaDetailActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);


                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);
                MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( mContext, "tag");

                PendingIntent pendingIntentPrevious;
                int drw_previous;
                if (posm == 0){
                    pendingIntentPrevious = null;
                    drw_previous = 0;
                } else {
                    Intent intentPrevious = new Intent(mContext, NotificationActionService.class)
                            .setAction(ACTION_PREVIUOS);
                    pendingIntentPrevious = PendingIntent.getBroadcast(mContext, 0,
                            intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
                    drw_previous = R.drawable.exo_icon_previous;
                }

                Intent intentPlay = new Intent(mContext, NotificationActionService.class)
                        .setAction(ACTION_PLAY);
                PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(mContext, 0,
                        intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

                PendingIntent pendingIntentNext;
                int drw_next;
                if (posm == sizem){
                    pendingIntentNext = null;
                    drw_next = 0;
                } else {
                    Intent intentNext = new Intent(mContext, NotificationActionService.class)
                            .setAction(ACTION_NEXT);
                    pendingIntentNext = PendingIntent.getBroadcast(mContext, 0,
                            intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
                    drw_next = R.drawable.exo_icon_next;
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.app_logo)
                        .setLargeIcon(result)
                        .setContentTitle(trackm.getTitle())
                        .setContentText(trackm.getDetail())
                        .setOnlyAlertOnce(true)//show notification for only first time
                        .setShowWhen(false)
                        .addAction(drw_previous, "Previous", pendingIntentPrevious)
                        .addAction(playbuttonm, "Play", pendingIntentPlay)
                        .addAction(drw_next, "Next", pendingIntentNext)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSessionCompat.getSessionToken()))
                       // .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

               // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                notificationManagerCompat.notify(mNotificationId, builder.build());*//*
            }
        }
    }*/

  /*  private  fun resolveUriAsBitmap(uri: Uri, context: Context?): Bitmap? {
        return withContext(Dispatchers.IO) {
            // Block on downloading artwork.
            Glide.with(context!!).applyDefaultRequestOptions(glideOptions)
                .asBitmap()
                .load(uri)
                .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                .get()
        }
    }*/
    const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px
    private val glideOptions = RequestOptions()
        .fallback(R.drawable.app_logo)
        .diskCacheStrategy(DiskCacheStrategy.DATA)



}