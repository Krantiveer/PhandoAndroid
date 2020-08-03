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
import com.perseverance.phando.home.mediadetails.MediaDetailActivity
import com.perseverance.phando.home.series.SeriesActivity
import com.perseverance.phando.utils.MyLog
import com.perseverance.phando.utils.PreferencesUtils
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val CHANNEL_ID = BuildConfig.APPLICATION_ID
    val CHANNEL_NAME = "Alert"

    //{ delay, vibrate, sleep, vibrate, sleep }
    val vibrate = longArrayOf(500, 100)
    val vibrateOff = longArrayOf(0, 0)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        try {
            val data = remoteMessage.data
            val id = data?.get("id")
            val type = data?.get("type")
            val isFree = data?.get("is_free")
            val thumbnail = data?.get("thumbnail")
            val poster = data?.get("poster")
            val title = data?.get("title")
            val maturity_rating = data?.get("maturity_rating")
            val rating = data?.get("rating")
            val detail = data?.get("detail")
            val description = data?.get("description")


            val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this@MyFirebaseMessagingService)
            val isNotificationOn = settingsPreferences.getBoolean("perf_notification", true)

            if (!isNotificationOn) {
                return
            }

            if (TextUtils.isEmpty(title)) {
                return
            }

            val notificationModel = NotificationData()
            AppDatabase.getInstance(this)?.notificationDao()?.insert(notificationModel)
            val baseVideo = NotificationData()
            baseVideo.id = id
            baseVideo.thumbnail = thumbnail
            baseVideo.title = title
            baseVideo.is_free = isFree!!.toInt()

            if (thumbnail.isNullOrBlank()) {
                sendNotification(title = title, body = detail, baseVideo = baseVideo)
            } else {
                Glide.with(Session.instance!!.applicationContext).asBitmap().load(thumbnail)
                        //.apply(RequestOptions().override(100, 100))
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                sendNotification(title = title, body = detail, baseVideo = baseVideo)
                                if (BuildConfig.DEBUG) MyLog.e("Notification image onLoadFailed - ${e?.message}")
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                sendNotification(title = title, body = detail, resource = resource, baseVideo = baseVideo)
                                return true
                            }

                        }).submit()
            }

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) MyLog.e("NOTIFICATION_EXCEPTION - ${e?.message}")
        }

    }

    private fun sendNotification(title: String?, body: String?, resource: Bitmap? = null, baseVideo: BaseVideo) {

        try {
            val settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this@MyFirebaseMessagingService)
            val isNotificationSoundIsOn = settingsPreferences.getBoolean("perf_notification_sound", true);
            val isNotificationVibrateIsOn = settingsPreferences.getBoolean("perf_notification_vibrate", true);

            var intent: Intent? = null
            if ("T".equals(baseVideo.mediaType)) {
                intent = Intent(this, SeriesActivity::class.java)
                intent.putExtra(Key.CATEGORY, baseVideo)
            } else {
                intent = MediaDetailActivity.getDetailIntent(this, baseVideo)

            }
            val pendingIntent = PendingIntent.getActivity(this, 101, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setLargeIcon(BitmapFactory.decodeResource(resources,
                            R.drawable.ic_notification))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true)
                    .setContentTitle(if (TextUtils.isEmpty(title)) resources.getString(R.string.app_name) else title)
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
            val mCartItemCount = PreferencesUtils.getIntegerPreferences("NOTIFICATION_COUNT")
            PreferencesUtils.saveIntegerPreferences("NOTIFICATION_COUNT", mCartItemCount + 1)
        } catch (e: Exception) {
            MyLog.e("sendNotification_failed")
        }
    }


}
