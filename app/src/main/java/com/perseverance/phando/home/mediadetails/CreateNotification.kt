package com.perseverance.phando.home.mediadetails;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.perseverance.phando.R;
import com.perseverance.phando.home.series.Episode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;
   private static NotificationCompat.Builder builder;
    private static Bitmap bitmapFinal;


    public static void createNotification(Context context, RelatedEpisode track, int playbutton, int pos, int size){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){



           /* DownloadImageTask task = new DownloadImageTask(context, 101, track, playbutton, pos, size);
            task.execute(track.getThumbnail());*/

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( context, "tag");

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (pos == 0){
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_PREVIUOS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_IMMUTABLE);
                drw_previous = R.drawable.exo_icon_previous;
            }

            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drw_next;
            if (pos == size){
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                        intentNext, PendingIntent.FLAG_IMMUTABLE);
                drw_next = R.drawable.exo_icon_next;
            }
           notification  = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.app_logo)
               //  .setLargeIcon(bitmapFinal)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getDetail())
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            notificationManagerCompat.notify(1, notification);
        }
    }



    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context mContext;
        private int mNotificationId;
      private   RelatedEpisode trackm;
      private   int playbuttonm;
      private int posm;
      private int sizem;

        public DownloadImageTask(Context context, int notificationId ,RelatedEpisode track, int playbutton, int pos, int size) {
            mContext = context;
            mNotificationId = notificationId;
            trackm = track;
            playbuttonm = playbutton;
            posm = pos;
            sizem = size;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                bitmapFinal= bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result ) {
            if (result != null) {


                notification.largeIcon = result;
            /*    Intent intent = new Intent(mContext, MediaDetailActivity.class);
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
                notificationManagerCompat.notify(mNotificationId, builder.build());*/
            }
        }
    }

}


