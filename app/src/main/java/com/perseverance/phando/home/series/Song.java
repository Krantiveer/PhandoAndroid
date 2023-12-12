package com.perseverance.phando.home.series;

import android.net.Uri;

public class Song {
    String title;
    Uri uri;
    Uri uriNetwork;
    int duration;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUriNetwork() {
        return uriNetwork;
    }

    public void setUriNetwork(Uri uriNetwork) {
        this.uriNetwork = uriNetwork;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Song(String title, Uri uri, Uri uriNetwork, int duration) {
        this.title = title;
        this.uri = uri;
        this.uriNetwork = uriNetwork;
        this.duration = duration;
    }
}
