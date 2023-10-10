package com.videoplayer;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoSdkUtil {

    public static List<String> getDownloadedVideo(Application application) {
        List<String> downloadMetadataList = new ArrayList<>();
        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
        HashMap<Uri, Download> downloads = downloadTracker.getDownloads();
        for (Uri key : downloads.keySet()) {
            downloadMetadataList.add(key.toString());
        }
        return downloadMetadataList;
    }


    public static void deleteAllDownloadedVideo(Application application) {
        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
        HashMap<Uri, Download> downloads = downloadTracker.getDownloads();
        for (Uri key : downloads.keySet()) {
                downloadTracker.deleteDownload(key);
        }
    }

//    public static List<DownloadMetadata> getDownloadedVideo(Application application) {
//        List<DownloadMetadata> downloadMetadataList = new ArrayList<>();
//        DownloadTracker downloadTracker = ((VideoPlayerApplication) application).getDownloadTracker();
//        HashMap<Uri, Download> downloads = downloadTracker.getDownloads();
//        for (Uri key : downloads.keySet()) {
//            Log.e("download", key.toString());
//            String fromUtf8Bytes = Util.fromUtf8Bytes(downloads.get(key).request.data);
//            Log.e("download", fromUtf8Bytes);
//            DownloadMetadata downloadMetadata = new Gson().fromJson(fromUtf8Bytes, DownloadMetadata.class);
//            downloadMetadataList.add(downloadMetadata);
//            Log.e("download", downloadMetadata.toString());
//        }
//        return downloadMetadataList;
//    }

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

    public static void startDownload(AppCompatActivity appCompatActivity, VideoPlayerMetadata intentAsSample, String playerTitle) {
        VideoPlayerApplication videoPlayerApplication = (VideoPlayerApplication) appCompatActivity.getApplication();
        DownloadTracker downloadTracker = videoPlayerApplication.getDownloadTracker();


        int downloadUnsupportedStringId = getDownloadUnsupportedStringId(intentAsSample);
        if (downloadUnsupportedStringId != 0) {
            Toast.makeText(videoPlayerApplication, downloadUnsupportedStringId, Toast.LENGTH_LONG)
                    .show();
        } else {
            VideoPlayerMetadata.UriSample uriSample = (VideoPlayerMetadata.UriSample) intentAsSample;
          //  Log.e("@@extension",  uriSample.extension);
            RenderersFactory renderersFactory =
                    //videoPlayerApplication.buildRenderersFactory(isNonNullAndChecked(preferExtensionDecodersMenuItem));
                    videoPlayerApplication.buildRenderersFactory(false);
            downloadTracker.toggleDownload(
                    appCompatActivity.getSupportFragmentManager(),
                    playerTitle,
                    uriSample.uri,
                    uriSample.extension,
                    renderersFactory);
        }
    }

    public static void startDownloadMp3(AppCompatActivity appCompatActivity, VideoPlayerMetadata intentAsSample, String playerTitle) {
        VideoPlayerApplication videoPlayerApplication = (VideoPlayerApplication) appCompatActivity.getApplication();
        DownloadTracker downloadTracker = videoPlayerApplication.getDownloadTracker();


        int downloadUnsupportedStringId = getDownloadUnsupportedStringId(intentAsSample);
        if (downloadUnsupportedStringId != 0) {
            Toast.makeText(videoPlayerApplication, downloadUnsupportedStringId, Toast.LENGTH_LONG)
                    .show();
        } else {
            VideoPlayerMetadata.UriSample uriSample = (VideoPlayerMetadata.UriSample) intentAsSample;
          //  Log.e("@@extension",  uriSample.extension);
            RenderersFactory renderersFactory =
                    //videoPlayerApplication.buildRenderersFactory(isNonNullAndChecked(preferExtensionDecodersMenuItem));
                    videoPlayerApplication.buildRenderersFactory(false);
            downloadTracker.toggleDownload(
                    appCompatActivity.getSupportFragmentManager(),
                    playerTitle,
                    uriSample.uri,
                    "mp3",
                    renderersFactory);
        }
    }
    private static int getDownloadUnsupportedStringId(VideoPlayerMetadata sample) {

        if (sample instanceof VideoPlayerMetadata.PlaylistSample) {
            return R.string.download_playlist_unsupported;
        }
        VideoPlayerMetadata.UriSample uriSample = (VideoPlayerMetadata.UriSample) sample;
        if (uriSample.drmInfo != null) {
            return R.string.download_drm_unsupported;
        }
        if (uriSample.isLive) {
            return R.string.download_live_unsupported;
        }
//        if (uriSample.adTagUri != null) {
//            return R.string.download_ads_unsupported;
//        }
        String scheme = uriSample.uri.getScheme();
        if (!("http".equals(scheme) || "https".equals(scheme))) {
            return R.string.download_scheme_unsupported;
        }
        return 0;
    }

}
