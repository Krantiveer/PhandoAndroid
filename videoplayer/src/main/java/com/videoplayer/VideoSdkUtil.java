package com.videoplayer;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoSdkUtil {

    public static List<DownloadMetadata> getDownloadedVideo(Application application) {
        List<DownloadMetadata> downloadMetadataList = new ArrayList<>();
        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
        HashMap<Uri, Download> downloads = downloadTracker.getDownloads();
        for (Uri key : downloads.keySet()) {
            Log.e("download", key.toString());
            String fromUtf8Bytes = Util.fromUtf8Bytes(downloads.get(key).request.data);
            Log.e("download", fromUtf8Bytes);
            DownloadMetadata downloadMetadata = new Gson().fromJson(fromUtf8Bytes, DownloadMetadata.class);
            downloadMetadataList.add(downloadMetadata);
            Log.e("download", downloadMetadata.toString());
        }
        return downloadMetadataList;
    }

    public static DownloadInfo getDownloadedInfo(Application application, String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }
        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
        Download download = downloadTracker.getDownloadInfo(Uri.parse(uri));
        if (download == null) {
            return null;
        }
        return new DownloadInfo(download.state, (int) download.getPercentDownloaded());
    }

    public static void deleteDownloadedInfo(Application application, String uri) {
        if (!TextUtils.isEmpty(uri)) {
            DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
            downloadTracker.deleteDownload(uri);
        }

    }

    public static void pauseDownload(Application application) {
        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
        downloadTracker.pauseDownload();

    }

    public static void resumeDownload(Application application) {
        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
        downloadTracker.resumeDownload();
    }


}
