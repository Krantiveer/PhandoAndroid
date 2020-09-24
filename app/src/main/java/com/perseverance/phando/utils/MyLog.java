package com.perseverance.phando.utils;

import android.util.Log;

import com.perseverance.phando.BuildConfig;
import com.perseverance.phando.Session;

import org.jetbrains.annotations.NotNull;

public class MyLog {

    private MyLog() {
    }


    public static void d(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
    public static void i(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void e(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }


    public static void e(String message) {
        Log.e(Session.Companion.getInstance().getPackageName(), message);
    }


    public static void infoAnalytic(@NotNull String message) {
       Log.i("FirebaseAnalytics", message);
    }
}
