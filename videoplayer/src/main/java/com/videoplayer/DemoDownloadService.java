/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.videoplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import java.util.List;

import static com.videoplayer.VideoPlayerApplication.DOWNLOAD_NOTIFICATION_CHANNEL_ID;


/**
 * A service for downloading media.
 */
public class DemoDownloadService extends DownloadService {

    private static final int JOB_ID = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    public DemoDownloadService() {
        super(
                FOREGROUND_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                R.string.exo_download_notification_channel_name,
                /* channelDescriptionResourceId= */ 0);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        VideoPlayerApplication application = (VideoPlayerApplication) getApplication();
        DownloadManager downloadManager = application.getDownloadManager();
        DownloadNotificationHelper downloadNotificationHelper =
                application.getDownloadNotificationHelper();
        downloadManager.addListener(
                new TerminalStateNotificationHelper(
                        this, downloadNotificationHelper, FOREGROUND_NOTIFICATION_ID + 1));
        return downloadManager;
    }

    @Override
    protected PlatformScheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }

    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        Intent intent = new Intent("download_event");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        return ((VideoPlayerApplication) getApplication())
                .getDownloadNotificationHelper()
                .buildProgressNotification(
                        R.drawable.ic_download, /* contentIntent= */ null, /* message= */ null, downloads);
    }

    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     * <p>This helper will outlive the lifespan of a single instance of {@link DemoDownloadService}.
     * It is static to avoid leaking the first {@link DemoDownloadService} instance.
     */
    private static final class TerminalStateNotificationHelper implements DownloadManager.Listener {

        private final Context context;
        private final DownloadNotificationHelper notificationHelper;

        private int nextNotificationId;

        public TerminalStateNotificationHelper(
                Context context, DownloadNotificationHelper notificationHelper, int firstNotificationId) {
            this.context = context.getApplicationContext();
            this.notificationHelper = notificationHelper;
            nextNotificationId = firstNotificationId;
        }

        @Override
        public void onDownloadChanged(DownloadManager manager, Download download) {
            Notification notification;
            String title = Util.fromUtf8Bytes(download.request.data);
            Intent intent = new Intent("download_event");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            if (download.state == Download.STATE_COMPLETED) {
                Intent sendIntent = new Intent();
                sendIntent.setAction("android.intent.action.OfflineMediaListActivity");


                PendingIntent intent1 = null;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    intent1 = PendingIntent.getActivity(context, 1000, sendIntent, PendingIntent.FLAG_IMMUTABLE);
                }
                else
                {
                    intent1 = PendingIntent.getActivity(context, 1000, sendIntent,PendingIntent.FLAG_ONE_SHOT);
                }

              //  PendingIntent intent1 = PendingIntent.getActivity(context, 1000, sendIntent, PendingIntent.FLAG_IMMUTABLE);
                notification =
                        notificationHelper.buildDownloadCompletedNotification(
                                R.drawable.ic_download_done,
                                /* contentIntent= */ intent1,
                                title);
            } else if (download.state == Download.STATE_FAILED) {
                notification =
                        notificationHelper.buildDownloadFailedNotification(
                                R.drawable.ic_download_done,
                                /* contentIntent= */ null,
                                title);
            } else if (download.state == Download.STATE_FAILED) {
                notification =
                        notificationHelper.buildDownloadFailedNotification(
                                R.drawable.ic_download_done,
                                /* contentIntent= */ null,
                                title);
            } else {
                return;
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification);
        }
    }
}
