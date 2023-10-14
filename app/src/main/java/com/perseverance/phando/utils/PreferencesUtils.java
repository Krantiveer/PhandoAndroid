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
    public static final String TEMP_TOKEN = "temp_token";
    public static final String LANGUAGE_IS_SELECTED = "selected_language";
    public static final String IS_SUBSCRIBE = "is_subscribe";
    public static final String FROM_SOCIAL = "from_social";
    public static final String BSNL_URL = "bsnl_url";
    public static final String WHATSAPP_TEXT = "whatsApp_text";
    public static final String EMAIL_CONTACTUS = "email_contact";
    public static final String IS_REVIEW = "is_review";

    public static final String WHATSAPP = "whatsApp";
    public static final String GAME_URL = "game_url";

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

    public static void setTempToken(String accessToken) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(TEMP_TOKEN, accessToken);
        editor.apply();
    }

    public static void setBsnlUrl(String bsnlUrl) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(BSNL_URL, bsnlUrl);
        editor.apply();
    }

    public static String getBSNLUrl() {
        return getPreferences().getString(BSNL_URL, "");
    }

    public static void setWhatsAppNumber(String bsnlUrl) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(WHATSAPP, bsnlUrl);
        editor.apply();
    }

    public static String getWhatsappnumber() {
        return getPreferences().getString(WHATSAPP, "");
    }

    public static void setWhatsText(String bsnlUrl) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(WHATSAPP_TEXT, bsnlUrl);
        editor.apply();
    }

    public static String getWhatsappText() {
        return getPreferences().getString(WHATSAPP_TEXT, "");
    }

    public static void setEmailContactus(String emailUs) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(EMAIL_CONTACTUS, emailUs);
        editor.apply();
    }

    public static String getEmailContactUs() {
        return getPreferences().getString(EMAIL_CONTACTUS, "");
    }

    public static void setGameUrl(String gameurl) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(GAME_URL, gameurl);
        editor.apply();
    }

    public static String getGameUrl() {
        return getPreferences().getString(GAME_URL, "");
    }


    public static void setLanguageSelectedSkip(Boolean isSelected) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(LANGUAGE_IS_SELECTED, isSelected);
        editor.apply();
    }
    public static boolean getLanguageSelectedSkip(){
        return getPreferences().getBoolean(LANGUAGE_IS_SELECTED, false);
    }


    // is subscribe
    public static void setIsSubscribe(Boolean isSubscribe) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(IS_SUBSCRIBE, isSubscribe);
        editor.apply();
    }
    public static boolean getIsSubscribe(){
        return getPreferences().getBoolean(IS_SUBSCRIBE, false);
    }

    public static void setIsFromSocial(Boolean fromSocial) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(FROM_SOCIAL, fromSocial);
        editor.apply();
    }
    public static boolean getFromSocial(){
        return getPreferences().getBoolean(FROM_SOCIAL, false);
    }

    public static void setIsReview(Integer isReview) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(IS_REVIEW, isReview);
        editor.apply();
    }
    public static Integer getIsReview(){
        return getPreferences().getInt(IS_REVIEW, 0);
    }

    public static String getLoggedStatus() {
        return getPreferences().getString(LOGGED_IN_ACCESS_TOKEN, "");
    }

    public static String getTempToken() {
        return "Bearer " +getPreferences().getString(TEMP_TOKEN, "");
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
