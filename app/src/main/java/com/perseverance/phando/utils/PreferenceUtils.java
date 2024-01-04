package com.perseverance.phando.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class PreferenceUtils {
    public static final String TAG = "PreferenceUtils";
    private static final PreferenceUtils INSTANCE = new PreferenceUtils();
    private static final String shared_preferences = "shared_preferences";

    public static final String NotiData = "NotiData";



    // public static final Boolean focusFromWatchNow = "";

    public static PreferenceUtils getInstance() {
        return INSTANCE;
    }

    public void setNotiDataPref(Context context, String str) {
        SharedPreferences prefs = context.getSharedPreferences(shared_preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NotiData, str);
        editor.apply();
    }

    public String getNotiDataPref(Context context) {
        return context.getSharedPreferences(shared_preferences, Context.MODE_PRIVATE)
                .getString(NotiData, "0");
    }

}
