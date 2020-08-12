package com.perseverance.phando.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.perseverance.phando.Session;

/**
 * Created by QAIT\TrilokiNath on 18/1/17.
 */

public class PreferencesUtils {

    // Values for Shared Prefrences
    public static final String LOGGED_IN_ACCESS_TOKEN = "logged_in_status";

    public static void saveStringPreferences(String Key, String Value) {
        SharedPreferences pref = getPreferences();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    private static SharedPreferences getPreferences() {
        Context context = Session.Companion.getInstance().getApplicationContext();
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void setLoggedIn(String accessToken) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(LOGGED_IN_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static String getLoggedStatus() {
        return getPreferences().getString(LOGGED_IN_ACCESS_TOKEN, "");
    }

    public static String getLoggedStatusWithBearer() {
        return "Bearer " + getPreferences().getString(LOGGED_IN_ACCESS_TOKEN, "");
    }

    public static void deleteAllPreferences() {

        SharedPreferences sPrefs = getPreferences();
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.clear();
        editor.commit();
    }


    public static void saveObject(String key, Object object) {

        SharedPreferences appSharedPrefs = getPreferences();
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(key, json);
        prefsEditor.commit();

    }


    public static void saveBooleanPreferences(String key, Boolean value) {
        SharedPreferences sPrefs = getPreferences();
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveIntegerPreferences(String key, Integer value) {
        SharedPreferences sPrefs = getPreferences();
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static Boolean getBooleanPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferences();
        Boolean savedPref = sharedPreferences.getBoolean(key, false);
        return savedPref;
    }

    public static Boolean getBooleanPreferences(String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = getPreferences();
        Boolean savedPref = sharedPreferences.getBoolean(key, defaultValue);
        return savedPref;
    }

    public static String getStringPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferences();
        String savedPref = sharedPreferences.getString(key, "");
        return savedPref;
    }

    public static String getStringPreferences(String key, String defultValue) {
        SharedPreferences sharedPreferences = getPreferences();
        String savedPref = sharedPreferences.getString(key, defultValue);
        return savedPref;
    }

    public static Integer getIntegerPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferences();
        Integer savedPref = sharedPreferences.getInt(key, 0);
        return savedPref;
    }

    public static Integer getIntegerPreferencesForTimeout(String key) {
        SharedPreferences sharedPreferences = getPreferences();
        Integer savedPref = sharedPreferences.getInt(key, -1);
        return savedPref;
    }

    public static Long getLongPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferences();
        Long savedPref = sharedPreferences.getLong(key, 0);
        return savedPref;
    }

    public static void saveLongPreferences(String key, Long value) {
        SharedPreferences sPrefs = getPreferences();
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
