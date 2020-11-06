package com.videoplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;


public class PhandoPlayerView extends FrameLayout implements
        LifecycleObserver {

    // Activity extras.

    public static final String SPHERICAL_STEREO_MODE_EXTRA = "spherical_stereo_mode";
    public static final String SPHERICAL_STEREO_MODE_MONO = "mono";
    public static final String SPHERICAL_STEREO_MODE_TOP_BOTTOM = "top_bottom";
    public static final String SPHERICAL_STEREO_MODE_LEFT_RIGHT = "left_right";

    // Actions.

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String ACTION_VIEW_LIST =
            "com.google.android.exoplayer.demo.action.VIEW_LIST";

    // Player configuration extras.

    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
    public static final String ABR_ALGORITHM_DEFAULT = "default";
    public static final String ABR_ALGORITHM_RANDOM = "random";

    // Media item configuration extras.

    public static final String PLEYER_TITLE = "player_title";
    public static final String PLAYER_LOGO = "player_logo";
    public static final String URI_EXTRA = "uri";
    public static final String EXTENSION_EXTRA = "extension";
    public static final String IS_LIVE_EXTRA = "is_live";

    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties";
    public static final String DRM_MULTI_SESSION_EXTRA = "drm_multi_session";
    public static final String PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders";
    public static final String TUNNELING_EXTRA = "tunneling";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    public static final String SUBTITLE_URI_EXTRA = "subtitle_uri";
    public static final String SUBTITLE_MIME_TYPE_EXTRA = "subtitle_mime_type";
    public static final String SUBTITLE_LANGUAGE_EXTRA = "subtitle_language";
    // For backwards compatibility only.
    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    // Saved instance state keys.

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    public static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    //Analytic
    private final Handler handler = new Handler();
    private Runnable currentPositionTracker;
    private long lastKnownPlaybackPercent = -1;
    private VideoPlayerApplication videoPlayerApplication;
    private PlayerView playerView;
    private boolean isShowingTrackSelectionDialog;
    private DownloadTracker downloadTracker;
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    //private DebugTextViewHelper debugViewHelper;
    //private TextView debugTextView;
    private boolean startAutoPlay;
    private TrackGroupArray lastSeenTrackGroupArray;
    private int startWindow;
    private long startPosition;
    private TextView exo_position;
    private TextView exo_duration;
    private DefaultTimeBar exo_progress;
    private TextView live;
    private ImageButton playerControlSetting;
    private ImageButton playerControlOrientation;
    // private PlayerNotificationManager playerNotificationManager;
    private PhandoPlayerCallback phandoPlayerCallback;

    // Fields used only for ad playback. The ads loader is loaded via reflection.
    public void setDefaultArtwork(Drawable drawable) {
        if (playerView != null) {
            if (drawable != null) {
                playerView.setDefaultArtwork(drawable);
            }
        }

    }

    private AdsLoader adsLoader;
    private Uri loadedAdTagUri;
    private Intent intent;
    BroadcastReceiver downloadBroadcastReceiver = new PlayerDownloadBroadcastReceiver();

    public PhandoPlayerView(Context context) {
        super(context);
        init(null, 0);
    }

    public PhandoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PhandoPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (getContext() instanceof PhandoPlayerCallback) {
            phandoPlayerCallback = (PhandoPlayerCallback) getContext();
        } else {

            throw new UnsupportedOperationException("Implement PhandoPlayerCallback");
        }
        videoPlayerApplication = ((VideoPlayerApplication) ((AppCompatActivity) getContext()).getApplication());
        ((AppCompatActivity) getContext()).getLifecycle().addObserver(this);
        downloadTracker = videoPlayerApplication.getDownloadTracker();
        //addObserver(this)
        LayoutInflater.from(getContext()).inflate(R.layout.phando_player_view, this);
        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        playerView = findViewById(R.id.player_view);
        exo_duration = findViewById(R.id.exo_duration);
        exo_position = findViewById(R.id.exo_position);
        live = findViewById(R.id.live);
        SpannableString spannable = new SpannableString("Live");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            spannable.setSpan(new BulletSpan(10, Color.RED,12), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        live.setText(spannable);
        exo_progress = findViewById(R.id.exo_progress);

        //debugTextView = findViewById(R.id.debug_text_view);
        playerControlOrientation = findViewById(R.id.playerControlOrientation);
        playerControlSetting = findViewById(R.id.playerControlSetting);
        playerControlOrientation.setOnClickListener(v -> {
            phandoPlayerCallback.onOrientationClicked();
        });
        playerControlSetting.setOnClickListener(v -> {
            phandoPlayerCallback.onSettingClicked();
        });
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {

            @Override
            public void onVisibilityChange(int visibility) {
                if (phandoPlayerCallback != null) {
                    phandoPlayerCallback.onConrolVisibilityChange(visibility);
                }
            }
        });
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
//    if (sphericalStereoMode != null) {
//      int stereoMode;
//      if (SPHERICAL_STEREO_MODE_MONO.equals(sphericalStereoMode)) {
//        stereoMode = C.STEREO_MODE_MONO;
//      } else if (SPHERICAL_STEREO_MODE_TOP_BOTTOM.equals(sphericalStereoMode)) {
//        stereoMode = C.STEREO_MODE_TOP_BOTTOM;
//      } else if (SPHERICAL_STEREO_MODE_LEFT_RIGHT.equals(sphericalStereoMode)) {
//        stereoMode = C.STEREO_MODE_LEFT_RIGHT;
//      } else {
//        showToast(R.string.error_unrecognized_stereo_mode);
//        finish();
//        return;
//      }
//      ((SphericalGLSurfaceView) playerView.getVideoSurfaceView()).setDefaultStereoMode(stereoMode);
//    }

//    if (savedInstanceState != null) {
//      trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
//      startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
//      startWindow = savedInstanceState.getInt(KEY_WINDOW);
//      startPosition = savedInstanceState.getLong(KEY_POSITION);
//    } else {
//
//    }
        DefaultTrackSelector.ParametersBuilder builder =
                new DefaultTrackSelector.ParametersBuilder(getContext());
        //boolean tunneling = intent.getBooleanExtra(TUNNELING_EXTRA, false);
        boolean tunneling = false;
        if (Util.SDK_INT >= 21 && tunneling) {
            builder.setTunnelingAudioSessionId(C.generateAudioSessionIdV21(getContext()));
        }
        trackSelectorParameters = builder.build();
        clearStartPosition();

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onStart() {
        if (
                Util.SDK_INT > 23 ||
                        player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {

        if (
                Util.SDK_INT <= 23 ||
                        player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(downloadBroadcastReceiver,
                new IntentFilter("download_event"));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        unRegisterCurrentPositionTracker();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(downloadBroadcastReceiver);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        releasePlayer();
        releaseAdsLoader();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        updateTrackSelectorParameters();
        updateStartPosition();
        bundle.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        bundle.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        bundle.putInt(KEY_WINDOW, startWindow);
        bundle.putLong(KEY_POSITION, startPosition);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            //this.stuff = bundle.getInt("stuff"); // ... load stuff
            if (bundle != null) {
                trackSelectorParameters = bundle.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
                startAutoPlay = bundle.getBoolean(KEY_AUTO_PLAY);
                startWindow = bundle.getInt(KEY_WINDOW);
                startPosition = bundle.getLong(KEY_POSITION);
                state = bundle.getParcelable("superState");
            } else {

            }
        }
        super.onRestoreInstanceState(state);
    }

//  @Override
//  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//      @NonNull int[] grantResults) {
//    if (grantResults.length == 0) {
//      // Empty results are triggered if a permission is requested while another request was already
//      // pending and can be safely ignored in this case.
//      return;
//    }
//    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//      initializePlayer();
//    } else {
//      showToast(R.string.storage_permission_denied);
//
//    }
//  }

    // Activity input

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    // OnClickListener methods

    public void setVideoData(Intent intent) {
        releasePlayer();
        releaseAdsLoader();
        clearStartPosition();
        startWindow = 0;
        startPosition = intent.getLongExtra(KEY_POSITION, 0) * 1000;
        this.intent = intent;
        initializePlayer();
        boolean isLive = intent.getBooleanExtra(IS_LIVE_EXTRA, false) ;
        if(isLive){
            exo_position.setVisibility(View.GONE);
            exo_duration.setVisibility(View.GONE);
            exo_progress.setVisibility(View.GONE);
            live.setVisibility(View.VISIBLE);
        }else {
            exo_position.setVisibility(View.VISIBLE);
            exo_duration.setVisibility(View.VISIBLE);
            exo_progress.setVisibility(View.VISIBLE);
            live.setVisibility(View.GONE);
        }
    }


    public void openVideoSettings() {
        if (!isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog
                    .show(((AppCompatActivity) getContext()).getSupportFragmentManager(), /* tag= */ null);
        }
    }

    public void openCCSettings() {
        if (!isShowingTrackSelectionDialog
                && SubtitleTrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true;
            SubtitleTrackSelectionDialog trackSelectionDialog =
                    SubtitleTrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog
                    .show(((AppCompatActivity) getContext()).getSupportFragmentManager(), /* tag= */ null);
        }
    }

//    public void startDownload(VideoPlayerMetadata intentAsSample, String playerTitle) {
//        int downloadUnsupportedStringId = getDownloadUnsupportedStringId(intentAsSample);
//        if (downloadUnsupportedStringId != 0) {
//            Toast.makeText(getContext(), downloadUnsupportedStringId, Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            VideoPlayerMetadata.UriSample uriSample = (VideoPlayerMetadata.UriSample) intentAsSample;
//            RenderersFactory renderersFactory =
//                    //videoPlayerApplication.buildRenderersFactory(isNonNullAndChecked(preferExtensionDecodersMenuItem));
//                    videoPlayerApplication.buildRenderersFactory(false);
//            downloadTracker.toggleDownload(
//                    ((AppCompatActivity) getContext()).getSupportFragmentManager(),
//                    playerTitle,
//                    uriSample.uri,
//                    uriSample.extension,
//                    renderersFactory);
//        }
//    }

    private void initializePlayer() {
        if (player == null) {
            //Intent intent = getIntent();

            mediaSource = createTopLevelMediaSource(intent);
            if (mediaSource == null) {
                return;
            }

            TrackSelection.Factory trackSelectionFactory;
            String abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_EXTRA);
            if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT.equals(abrAlgorithm)) {
                trackSelectionFactory = new AdaptiveTrackSelection.Factory();
            } else if (ABR_ALGORITHM_RANDOM.equals(abrAlgorithm)) {
                trackSelectionFactory = new RandomTrackSelection.Factory();
            } else {
                showToast(R.string.error_unrecognized_abr_algorithm);

                return;
            }

            boolean preferExtensionDecoders =
                    intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
            RenderersFactory renderersFactory =
                    videoPlayerApplication.buildRenderersFactory(preferExtensionDecoders);

            trackSelector = new DefaultTrackSelector(/* context= */ getContext(), trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);
            lastSeenTrackGroupArray = null;

            player =
                    new SimpleExoPlayer.Builder(/* context= */ getContext(), renderersFactory)
                            .setTrackSelector(trackSelector)
                            .build();
            player.addListener(new PlayerEventListener());
            player.setPlayWhenReady(startAutoPlay);
            playerView.setPlayer(player);
            playerView.setPlaybackPreparer(new PlaybackPreparer() {
                @Override
                public void preparePlayback() {

                    player.retry();
                }
            });
            // debugViewHelper = new DebugTextViewHelper(player,debugTextView);
            // debugViewHelper.start();
            if (adsLoader != null) {
                adsLoader.setPlayer(player);
            }
        }
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        player.prepare(mediaSource, !haveStartPosition, false);
        updateButtonVisibility();
    }

    @Nullable
    private MediaSource createTopLevelMediaSource(Intent intent) {
        if (intent == null) {
            return null;
        }
        String action = intent.getAction();
        boolean actionIsListView = ACTION_VIEW_LIST.equals(action);
        if (!actionIsListView && !ACTION_VIEW.equals(action)) {
            showToast(getContext().getString(R.string.unexpected_intent_action, action));
            return null;
        }

        VideoPlayerMetadata intentAsSample = VideoPlayerMetadata.createFromIntent(intent);
        VideoPlayerMetadata.UriSample[] samples =
                intentAsSample instanceof VideoPlayerMetadata.PlaylistSample
                        ? ((VideoPlayerMetadata.PlaylistSample) intentAsSample).children
                        : new VideoPlayerMetadata.UriSample[]{(VideoPlayerMetadata.UriSample) intentAsSample};

        boolean seenAdsTagUri = false;
        for (VideoPlayerMetadata.UriSample sample : samples) {
            seenAdsTagUri |= sample.adTagUri != null;
            if (!Util.checkCleartextTrafficPermitted(sample.uri)) {
                showToast(R.string.error_cleartext_not_permitted);
                return null;
            }
            if (Util.maybeRequestReadExternalStoragePermission(/* activity= */
                    (AppCompatActivity) getContext(), sample.uri)) {
                // The player will be reinitialized if the permission is granted.
                return null;
            }
        }

        MediaSource[] mediaSources = new MediaSource[samples.length];
        for (int i = 0; i < samples.length; i++) {
            mediaSources[i] = createLeafMediaSource(samples[i]);
            //  source = new MergingMediaSource(videoSource, subtitleSource);

            VideoPlayerMetadata.SubtitleInfo subtitleInfo = samples[i].subtitleInfo;
            if (subtitleInfo != null) {


                List<MediaSource> subtitleMediaSource = new ArrayList<>();
                subtitleMediaSource.add(mediaSources[i]);
                for (String subtitleUri : subtitleInfo.uri) {
                    String[] strings = subtitleUri.split(",");
                    Format subtitleFormat =
                            Format.createTextSampleFormat(
                                    /* id= */ null,
                                    strings[1],
                                    C.SELECTION_FLAG_DEFAULT,
                                    strings[2]);
                    MediaSource tempSubtitleMediaSource =
                            new SingleSampleMediaSource.Factory(dataSourceFactory)
                                    .createMediaSource(Uri.parse(strings[0]), subtitleFormat, C.TIME_UNSET);
                    subtitleMediaSource.add(tempSubtitleMediaSource);
                }

                mediaSources[i] = new MergingMediaSource(subtitleMediaSource.toArray(new MediaSource[0]));
            }
        }
        MediaSource mediaSource =
                mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);

        if (seenAdsTagUri) {
            Uri adTagUri = samples[0].adTagUri;
            if (actionIsListView) {
                showToast(R.string.unsupported_ads_in_concatenation);
            } else {
                if (!adTagUri.equals(loadedAdTagUri)) {
                    releaseAdsLoader();
                    loadedAdTagUri = adTagUri;
                }
                MediaSource adsMediaSource = createAdsMediaSource(mediaSource, adTagUri);
                if (adsMediaSource != null) {
                    mediaSource = adsMediaSource;
                } else {
                    showToast(R.string.ima_not_loaded);
                }
            }
        } else {
            releaseAdsLoader();
        }

        return mediaSource;
    }

    private MediaSource createLeafMediaSource(VideoPlayerMetadata.UriSample parameters) {
        VideoPlayerMetadata.DrmInfo drmInfo = parameters.drmInfo;
        int errorStringId = R.string.error_drm_unknown;
        DrmSessionManager<ExoMediaCrypto> drmSessionManager = null;
        if (drmInfo == null) {
            drmSessionManager = DrmSessionManager.getDummyDrmSessionManager();
        } else if (Util.SDK_INT < 18) {
            errorStringId = R.string.error_drm_unsupported_before_api_18;
        } else if (!MediaDrm.isCryptoSchemeSupported(drmInfo.drmScheme)) {
            errorStringId = R.string.error_drm_unsupported_scheme;
        } else {
            MediaDrmCallback mediaDrmCallback =
                    createMediaDrmCallback(drmInfo.drmLicenseUrl, drmInfo.drmKeyRequestProperties);
            drmSessionManager =
                    new DefaultDrmSessionManager.Builder()
                            .setUuidAndExoMediaDrmProvider(drmInfo.drmScheme, FrameworkMediaDrm.DEFAULT_PROVIDER)
                            .setMultiSession(drmInfo.drmMultiSession)
                            .build(mediaDrmCallback);
        }

        if (drmSessionManager == null) {
            showToast(errorStringId);
            ((Activity) getContext()).finish();
            return null;
        }

        DownloadRequest downloadRequest =
                videoPlayerApplication.getDownloadTracker().getDownloadRequest(parameters.uri);
        if (downloadRequest != null) {
            return DownloadHelper.createMediaSource(downloadRequest, dataSourceFactory);
        }
        return createLeafMediaSource(parameters.uri, parameters.extension, drmSessionManager);
    }

    private MediaSource createLeafMediaSource(
            Uri uri, String extension, DrmSessionManager<ExoMediaCrypto> drmSessionManager) {
        @C.ContentType int type = Util.inferContentType(uri, extension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private HttpMediaDrmCallback createMediaDrmCallback(
            String licenseUrl, String[] keyRequestPropertiesArray) {
        HttpDataSource.Factory licenseDataSourceFactory =
                ((VideoPlayerApplication) ((AppCompatActivity) getContext()).getApplication())
                        .buildHttpDataSourceFactory();
        HttpMediaDrmCallback drmCallback =
                new HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory);
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        return drmCallback;
    }

    private void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            // debugViewHelper.stop();
            //debugViewHelper = null;
            //playerNotificationManager.setPlayer(null);
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;

        }
        if (adsLoader != null) {
            adsLoader.setPlayer(null);
        }
    }

    private void releaseAdsLoader() {
        if (adsLoader != null) {
            adsLoader.release();
            adsLoader = null;
            loadedAdTagUri = null;
            playerView.getOverlayFrameLayout().removeAllViews();
        }
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
            //Log.e("bb", "startWindow : " + startWindow + "startPosition :" + startPosition);
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    /**
     * Returns a new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory() {
        return ((VideoPlayerApplication) ((AppCompatActivity) getContext()).getApplication())
                .buildDataSourceFactory();
    }

    /**
     * Returns an ads media source, reusing the ads loader if one exists.
     */
    @Nullable
    private MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) {
        // Load the extension source using reflection so the demo app doesn't have to depend on it.
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
        try {
            // Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
            ImaAdsLoader imaAdsLoader = new ImaAdsLoader.Builder(getContext()).setAdEventListener(new AdEvent.AdEventListener() {
                @Override
                public void onAdEvent(AdEvent adEvent) {
                    switch (adEvent.getType()) {

                        case CLICKED:
                            phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("adclick"));
                            break;
                        case STARTED:
                            phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("adplay"));
                            break;
                        case SKIPPED:
                            phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("adskip"));
                            break;
                        case COMPLETED:
                            phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("adended"));
                            break;
                        case ALL_ADS_COMPLETED:

                            break;
                    }

                }
            }).buildForAdTag(adTagUri);
            imaAdsLoader.addCallback(new VideoAdPlayer.VideoAdPlayerCallback() {

                @Override
                public void onPlay() {

                }

                @Override
                public void onVolumeChanged(int i) {

                }

                @Override
                public void onPause() {

                }

                @Override
                public void onLoaded() {

                }

                @Override
                public void onResume() {

                }

                @Override
                public void onEnded() {

                }

                @Override
                public void onError() {
                    phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("aderror"));
                }

                @Override
                public void onBuffering() {

                }
            });
            if (adsLoader == null) {
                // Full class names used so the LINT.IfChange rule triggers should any of the classes move.
                // LINT.IfChange
//                Constructor<? extends AdsLoader> loaderConstructor =
//                        loaderClass
//                                .asSubclass(AdsLoader.class)
//                                .getConstructor(Context.class, Uri.class);
                // LINT.ThenChange(../../../../../../../../proguard-rules.txt)
                // adsLoader = loaderConstructor.newInstance(getContext(), adTagUri);
                adsLoader = imaAdsLoader;
            }
            MediaSourceFactory adMediaSourceFactory =
                    new MediaSourceFactory() {
                        @Override
                        public MediaSource createMediaSource(Uri uri) {
                            return PhandoPlayerView.this.createLeafMediaSource(
                                    uri, /* extension=*/ null, DrmSessionManager.getDummyDrmSessionManager());
                        }

                        @Override
                        public int[] getSupportedTypes() {
                            return new int[]{C.TYPE_DASH, C.TYPE_SS, C.TYPE_HLS, C.TYPE_OTHER};
                        }
                    };
            AdsMediaSource adsMediaSource = new AdsMediaSource(mediaSource, adMediaSourceFactory, adsLoader, playerView);
            return adsMediaSource;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // User controls

    private void updateButtonVisibility() {
        boolean enableButton = player != null && TrackSelectionDialog.willHaveContent(trackSelector);
        if (phandoPlayerCallback != null) {
            phandoPlayerCallback.updateSettingButton(enableButton);
            playerControlSetting.setEnabled(enableButton);
        }
        // selectTracksButton.setEnabled(enableButton);
    }


    private void showToast(int messageId) {
        showToast(getContext().getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
          //  Log.e("setting-", getVideoString());
            switch (playbackState) {

                case Player.STATE_ENDED:
                    unRegisterCurrentPositionTracker();
                    phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("play-100"));
                    break;
                case Player.STATE_READY:
                    if (playWhenReady && playbackState == Player.STATE_READY) {
                        if (!player.isPlayingAd()) {
                            phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent("playerstart"));
                            if (currentPositionTracker == null) {
                                registerCurrentPositionTracker();
                            }
                        }
                    }
                    break;
                default:
                    unRegisterCurrentPositionTracker();
                    break;
            }
            updateButtonVisibility();
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer();
            } else {
                updateButtonVisibility();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            updateButtonVisibility();
            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector
                        .getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video);
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio);
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getContext().getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException
                                .getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getContext().getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getContext().getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getContext().getString(R.string.error_no_decoder,
                                            decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getContext().getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.codecInfo.name);
                    }
                }
            }

            return Pair.create(0, errorString);
        }
    }

    public int getCurrentPosition() {
        if (player != null) {
            return (int) (player.getCurrentPosition() / 1000);
        } else {
            return 0;
        }

    }

    public int getTotalDuration() {
        if (player != null) {
            return (int) (player.getDuration() / 1000);
        } else {
            return 0;
        }

    }

    public void seekTo(long position) {
        player.seekTo(startWindow, position * 1000);
    }

    /**
     * Returns a string containing video debugging information.
     */
    protected String getVideoString() {
        Format format = player.getVideoFormat();
        DecoderCounters decoderCounters = player.getVideoDecoderCounters();
        if (format == null || decoderCounters == null) {
            return "";
        }
        return "\n"
                + format.sampleMimeType
                + "(id:"
                + format.id
                + " r:"
                + format.width
                + "x"
                + format.height
                + getPixelAspectRatioString(format.pixelWidthHeightRatio)
                + getDecoderCountersBufferCountString(decoderCounters)
                + ")";
    }

    private static String getDecoderCountersBufferCountString(DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        return " sib:" + counters.skippedInputBufferCount
                + " sb:" + counters.skippedOutputBufferCount
                + " rb:" + counters.renderedOutputBufferCount
                + " db:" + counters.droppedBufferCount
                + " mcdb:" + counters.maxConsecutiveDroppedBufferCount
                + " dk:" + counters.droppedToKeyframeCount;
    }

    private static String getPixelAspectRatioString(float pixelAspectRatio) {
        return pixelAspectRatio == Format.NO_VALUE || pixelAspectRatio == 1f ? ""
                : (" par:" + String.format(Locale.US, "%.02f", pixelAspectRatio));
    }

//    private int getDownloadUnsupportedStringId(VideoPlayerMetadata sample) {
//
//        if (sample instanceof VideoPlayerMetadata.PlaylistSample) {
//            return R.string.download_playlist_unsupported;
//        }
//        VideoPlayerMetadata.UriSample uriSample = (VideoPlayerMetadata.UriSample) sample;
//        if (uriSample.drmInfo != null) {
//            return R.string.download_drm_unsupported;
//        }
//        if (uriSample.isLive) {
//            return R.string.download_live_unsupported;
//        }
////        if (uriSample.adTagUri != null) {
////            return R.string.download_ads_unsupported;
////        }
//        String scheme = uriSample.uri.getScheme();
//        if (!("http".equals(scheme) || "https".equals(scheme))) {
//            return R.string.download_scheme_unsupported;
//        }
//        return 0;
//    }

    private static boolean isNonNullAndChecked(@Nullable MenuItem menuItem) {
        // Temporary workaround for layouts that do not inflate the options menu.
        return menuItem != null && menuItem.isChecked();
    }


    private void registerCurrentPositionTracker() {
        progressSet.clear();
        // checkingRange = player.getDuration() / 50;
        unRegisterCurrentPositionTracker();
        currentPositionTracker = new Runnable() {
            @Override
            public void run() {
                if (player!=null) {
                    long currentPosition = player.getCurrentPosition();
                    phandoPlayerCallback.onPlayerProgress(currentPosition / 1000);
                    long totalDuration = player.getDuration();
                    long percent = (currentPosition * 100) / totalDuration;
                    if (lastKnownPlaybackPercent != percent) {
                        lastKnownPlaybackPercent = percent;
                        sendProgressEvent(percent);

                    }
                }
                handler.postDelayed(currentPositionTracker, checkingRange);
            }
        };
        handler.postDelayed(currentPositionTracker, checkingRange);
    }

    private void unRegisterCurrentPositionTracker() {
        if (currentPositionTracker != null) {
            handler.removeCallbacks(currentPositionTracker);
            currentPositionTracker = null;
        }
    }

    private Set<Integer> progressSet = new TreeSet<Integer>();
    private int[] percentArray = {20, 40, 60, 80};
    private int range = 2;
    private long checkingRange = 1000;

    private void sendProgressEvent(long percent) {
        String progress = null;
        for (int i = 0; i < percentArray.length; i++) {
            if (percent > percentArray[i] - range && percent < percentArray[i] + range) {
                if (!progressSet.contains(percentArray[i])) {
                    progressSet.add(percentArray[i]);
                    progress = "play-" + percentArray[i];

                }
            }
        }
        if (progress != null)
            phandoPlayerCallback.onPlayerEvent(new PlayerTrackingEvent(progress));

    }

    public class PlayerDownloadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (phandoPlayerCallback != null)
                phandoPlayerCallback.onDownloadStateChanged();
        }
    }
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            playerControlOrientation.setImageResource(R.drawable.player_screen_zoom_out);
        }else {
            playerControlOrientation.setImageResource(R.drawable.player_screen_zoom_in);
        }
    }
}
