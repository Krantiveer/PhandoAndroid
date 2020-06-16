package com.perseverance.phando.utils;

import android.util.Log;

import com.perseverance.phando.BuildConfig;
import com.perseverance.phando.Session;

public class MyLog {

    private MyLog() {
    }


    public static void d(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
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


}
