/*
 * Copyright (C) 2019 The Android Open Source Project
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

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.videoplayer.PhandoPlayerView.AD_TAG_URI_EXTRA;
import static com.videoplayer.PhandoPlayerView.EXTENSION_EXTRA;
import static com.videoplayer.PhandoPlayerView.IS_LIVE_EXTRA;


/* package */ public abstract class VideoPlayerMetadata {

  public static final class UriSample extends VideoPlayerMetadata {

    public static UriSample createFromIntent(Uri uri, Intent intent, String extrasKeySuffix) {
      String extension = intent.getStringExtra(EXTENSION_EXTRA + extrasKeySuffix);
      String adsTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA + extrasKeySuffix);
      boolean isLive =
          intent.getBooleanExtra(IS_LIVE_EXTRA + extrasKeySuffix, /* defaultValue= */ false);
      Uri adTagUri = adsTagUriString != null ? Uri.parse(adsTagUriString) : null;
      return new UriSample(
          /* name= */ null,
          uri,
          extension,
          isLive,
          DrmInfo.createFromIntent(intent, extrasKeySuffix),
          adTagUri,
          /* sphericalStereoMode= */ null,
          SubtitleInfo.createFromIntent(intent, extrasKeySuffix)
      );
    }

    public final Uri uri;
    public final String extension;
    public final boolean isLive;
    public final DrmInfo drmInfo;
    public final Uri adTagUri;
    @Nullable public final String sphericalStereoMode;
    @Nullable SubtitleInfo subtitleInfo;

    public UriSample(
        String name,
        Uri uri,
        String extension,
        boolean isLive,
        DrmInfo drmInfo,
        Uri adTagUri,
        @Nullable String sphericalStereoMode,
        @Nullable SubtitleInfo subtitleInfo) {
      super(name);
      this.uri = uri;
      this.extension = extension;
      this.isLive = isLive;
      this.drmInfo = drmInfo;
      this.adTagUri = adTagUri;
      this.sphericalStereoMode = sphericalStereoMode;
      this.subtitleInfo = subtitleInfo;
    }

    @Override
    public void addToIntent(Intent intent) {
      intent.setAction(PhandoPlayerView.ACTION_VIEW).setData(uri);
      intent.putExtra(PhandoPlayerView.IS_LIVE_EXTRA, isLive);
      intent.putExtra(PhandoPlayerView.SPHERICAL_STEREO_MODE_EXTRA, sphericalStereoMode);
      addPlayerConfigToIntent(intent, /* extrasKeySuffix= */ "");
    }

    public void addToPlaylistIntent(Intent intent, String extrasKeySuffix) {
      intent.putExtra(PhandoPlayerView.URI_EXTRA + extrasKeySuffix, uri.toString());
      intent.putExtra(PhandoPlayerView.IS_LIVE_EXTRA + extrasKeySuffix, isLive);
      addPlayerConfigToIntent(intent, extrasKeySuffix);
    }

    private void addPlayerConfigToIntent(Intent intent, String extrasKeySuffix) {
      intent
          .putExtra(EXTENSION_EXTRA + extrasKeySuffix, extension)
          .putExtra(
              AD_TAG_URI_EXTRA + extrasKeySuffix, adTagUri != null ? adTagUri.toString() : null);
      if (drmInfo != null) {
        drmInfo.addToIntent(intent, extrasKeySuffix);
      }
      if (subtitleInfo != null) {
        subtitleInfo.addToIntent(intent, extrasKeySuffix);
      }
    }
  }

  public static final class PlaylistSample extends VideoPlayerMetadata {

    public final UriSample[] children;

    public PlaylistSample(String name, UriSample... children) {
      super(name);
      this.children = children;
    }

    @Override
    public void addToIntent(Intent intent) {
      intent.setAction(PhandoPlayerView.ACTION_VIEW_LIST);
      for (int i = 0; i < children.length; i++) {
        children[i].addToPlaylistIntent(intent, /* extrasKeySuffix= */ "_" + i);
      }
    }
  }

  public static final class DrmInfo {

    public static DrmInfo createFromIntent(Intent intent, String extrasKeySuffix) {
      String schemeKey = PhandoPlayerView.DRM_SCHEME_EXTRA + extrasKeySuffix;
      String schemeUuidKey = PhandoPlayerView.DRM_SCHEME_UUID_EXTRA + extrasKeySuffix;
      if (!intent.hasExtra(schemeKey) && !intent.hasExtra(schemeUuidKey)) {
        return null;
      }
      String drmSchemeExtra =
          intent.hasExtra(schemeKey)
              ? intent.getStringExtra(schemeKey)
              : intent.getStringExtra(schemeUuidKey);
      UUID drmScheme = Util.getDrmUuid(drmSchemeExtra);
      String drmLicenseUrl = intent.getStringExtra(PhandoPlayerView.DRM_LICENSE_URL_EXTRA + extrasKeySuffix);
      String[] keyRequestPropertiesArray =
          intent.getStringArrayExtra(PhandoPlayerView.DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix);
      boolean drmMultiSession =
          intent.getBooleanExtra(PhandoPlayerView.DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, false);
      return new DrmInfo(drmScheme, drmLicenseUrl, keyRequestPropertiesArray, drmMultiSession);
    }

    public final UUID drmScheme;
    public final String drmLicenseUrl;
    public final String[] drmKeyRequestProperties;
    public final boolean drmMultiSession;

    public DrmInfo(
        UUID drmScheme,
        String drmLicenseUrl,
        String[] drmKeyRequestProperties,
        boolean drmMultiSession) {
      this.drmScheme = drmScheme;
      this.drmLicenseUrl = drmLicenseUrl;
      this.drmKeyRequestProperties = drmKeyRequestProperties;
      this.drmMultiSession = drmMultiSession;
    }

    public void addToIntent(Intent intent, String extrasKeySuffix) {
      Assertions.checkNotNull(intent);
      intent.putExtra(PhandoPlayerView.DRM_SCHEME_EXTRA + extrasKeySuffix, drmScheme.toString());
      intent.putExtra(PhandoPlayerView.DRM_LICENSE_URL_EXTRA + extrasKeySuffix, drmLicenseUrl);
      intent.putExtra(PhandoPlayerView.DRM_KEY_REQUEST_PROPERTIES_EXTRA + extrasKeySuffix, drmKeyRequestProperties);
      intent.putExtra(PhandoPlayerView.DRM_MULTI_SESSION_EXTRA + extrasKeySuffix, drmMultiSession);
    }
  }

  public static final class SubtitleInfo {

    @Nullable
    public static SubtitleInfo createFromIntent(Intent intent, String extrasKeySuffix) {
      if (!intent.hasExtra(PhandoPlayerView.SUBTITLE_URI_EXTRA + extrasKeySuffix)) {
        return null;
      }
      return new SubtitleInfo(
          //Uri.parse(intent.getStringExtra(PhandoPlayerView.SUBTITLE_URI_EXTRA + extrasKeySuffix)),
              intent.getStringArrayListExtra(PhandoPlayerView.SUBTITLE_URI_EXTRA + extrasKeySuffix),
          intent.getStringExtra(PhandoPlayerView.SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix),
          intent.getStringExtra(PhandoPlayerView.SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix));
    }

    //public final Uri uri;
    public final List<String> uri;
    public final String mimeType;
    @Nullable public final String language;

    public SubtitleInfo(List<String> uri, String mimeType, @Nullable String language) {
      //this.uri = Assertions.checkNotNull(uri);
      this.uri = uri;
      this.mimeType = Assertions.checkNotNull(mimeType);
      this.language = language;
    }

    public void addToIntent(Intent intent, String extrasKeySuffix) {
      intent.putStringArrayListExtra(PhandoPlayerView.SUBTITLE_URI_EXTRA + extrasKeySuffix, (ArrayList<String>) uri);
      intent.putExtra(PhandoPlayerView.SUBTITLE_MIME_TYPE_EXTRA + extrasKeySuffix, mimeType);
      intent.putExtra(PhandoPlayerView.SUBTITLE_LANGUAGE_EXTRA + extrasKeySuffix, language);
    }
  }

  public static VideoPlayerMetadata createFromIntent(Intent intent) {
    if (PhandoPlayerView.ACTION_VIEW_LIST.equals(intent.getAction())) {
      ArrayList<String> intentUris = new ArrayList<>();
      int index = 0;
      while (intent.hasExtra(PhandoPlayerView.URI_EXTRA + "_" + index)) {
        intentUris.add(intent.getStringExtra(PhandoPlayerView.URI_EXTRA + "_" + index));
        index++;
      }
      UriSample[] children = new UriSample[intentUris.size()];
      for (int i = 0; i < children.length; i++) {
        Uri uri = Uri.parse(intentUris.get(i));
        children[i] = UriSample.createFromIntent(uri, intent, /* extrasKeySuffix= */ "_" + i);
      }
      return new PlaylistSample(/* name= */ null, children);
    } else {
      return UriSample.createFromIntent(intent.getData(), intent, /* extrasKeySuffix= */ "");
    }
  }

  @Nullable public final String name;

  public VideoPlayerMetadata(String name) {
    this.name = name;
  }

  public abstract void addToIntent(Intent intent);
}
