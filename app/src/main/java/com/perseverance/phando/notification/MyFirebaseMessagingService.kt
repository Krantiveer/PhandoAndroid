package com.perseverance.phando.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.perseverance.phando.BuildConfig
import com.perseverance.phando.R
import com.perseverance.phando.Session
import com.perseverance.phando.constants.Key
import com.perseverance.phando.db.AppDatabase
import com.perseverance.phando.db.Video
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.utils.MyLog
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val CHANNEL_ID = BuildConfig.APPLICATION_ID
    val CHANNEL_NAME = "Alert"

    //{ delay, vibrate, sleep, vibrate, sleep }
    val vibrate = longArrayOf(500, 100)
    val vibrateOff = longArrayOf(0, 0)


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        MyLog.e("@@RemoteMessage", remoteMessage.data.toString())
        try {
            val data = remoteMessage.data
            val id = data?.get("id")
            val notificationTitle = data?.get("notification_title")
            val type = data?.get("type")
            val isFree = data?.get("is_free")
            val thumbnail = data?.get("thumbnail")
            val poster = data?.get("poster")
            val title = data?.get("title")
            val maturity_rating = data?.get("maturity_rating")
            val rating = data?.get("rating")
            val detail = data?.get("detail")

            if (!("T".equals(type) || "M".equals(type))) {
                return
            }

            val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this@MyFirebaseMessagingService)
            val isNotificationOn = settingsPreferences.getBoolean("perf_notification", true)

            if (!isNotificationOn) {
                return
            }

            if (TextUtils.isEmpty(title)) {
                return
            }


            val baseVideo = Video()
            baseVideo.id = id?.toInt()
            baseVideo.thumbnail = thumbnail
            baseVideo.title = notificationTitle
            baseVideo.type = type
            baseVideo.description = detail
            baseVideo.rating = rating?.toInt()
            baseVideo.is_free = isFree!!.toInt()

            val notificationModel = NotificationData()
            val dbId = System.currentTimeMillis()
            notificationModel.dbID = dbId
            notificationModel.id = id?.toInt()
            notificationModel.thumbnail = thumbnail
            notificationModel.title = notificationTitle
            notificationModel.type = type
            notificationModel.description = detail
            notificationModel.rating = rating?.toInt()
            notificationModel.free = isFree!!.toInt()

            AppDatabase.getInstance(this)?.notificationDao()?.insert(notificationModel)

            if (thumbnail.isNullOrBlank()) {
                sendNotification(notificationTitle = notificationTitle, body = detail, baseVideo = baseVideo, dbId = dbId)
            } else {
                Glide.with(Session.instance!!.applicationContext).asBitmap().load(thumbnail)
                        //.apply(RequestOptions().override(100, 100))
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                sendNotification(notificationTitle = notificationTitle, body = detail, baseVideo = baseVideo, dbId = dbId)
                                if (BuildConfig.DEBUG) MyLog.e("Notification image onLoadFailed - ${e?.message}")
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                sendNotification(notificationTitle = notificationTitle, body = detail, resource = resource, baseVideo = baseVideo, dbId = dbId)
                                return true
                            }

                        }).submit()
            }

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) MyLog.e("NOTIFICATION_EXCEPTION - ${e?.message}")
        }

    }

    private fun sendNotification(notificationTitle: String?, body: String?, resource: Bitmap? = null, baseVideo: Video, dbId: Long) {

        try {
            val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this@MyFirebaseMessagingService)
            val isNotificationSoundIsOn = settingsPreferences.getBoolean("perf_notification_sound", true);
            val isNotificationVibrateIsOn = settingsPreferences.getBoolean("perf_notification_vibrate", true);

            var intent: Intent? = null
            if ("T".equals(baseVideo.type)) {
                intent = Intent(this, SeriesActivity::class.java)
                intent.putExtra(Key.CATEGORY, baseVideo)
            } else {
                intent = MediaDetailActivity.getDetailIntent(this, baseVideo)

            }
            intent.putExtra(Key.NOTIFICATION_DB_ID, dbId)
            val pendingIntent = PendingIntent.getActivity(this, 101, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(resources,
                            R.drawable.app_logo))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setContentTitle(if (TextUtils.isEmpty(notificationTitle)) resources.getString(R.string.app_name) else notificationTitle)
                    .setContentText(body)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(body))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)



            resource?.let {
                val style = NotificationCompat.BigPictureStyle()
                style.bigPicture(it)
                        .setSummaryText(body)
                notificationBuilder.setStyle(style)
            }

            if (isNotificationSoundIsOn) {
                notificationBuilder.setSound(defaultSoundUri)
            }

            if (isNotificationVibrateIsOn) {
                notificationBuilder.setVibrate(vibrate)
            } else {
                notificationBuilder.setVibrate(vibrateOff)
            }

            val mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val androidChannel = NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                androidChannel.enableLights(true)
                if (isNotificationVibrateIsOn) {
                    androidChannel.enableVibration(true)
                } else {
                    androidChannel.enableVibration(false)
                }
                androidChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                mManager.createNotificationChannel(androidChannel)
            }
            mManager.notify(Random().nextInt(100) + 1, notificationBuilder.build())
        } catch (e: Exception) {
            MyLog.e("sendNotification_failed")
        }
    }


}
