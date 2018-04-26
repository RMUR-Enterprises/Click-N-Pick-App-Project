package com.example.app;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("WeakerAccess")
public class AppPreferences {

    private static final String PREFS_NAME = "my_prefs";
    private static final String DEFAULT_BASE_URL = "192.168.1.103";

    private static final String KEY_BASE_URL = "base_url";
    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_VENDOR_TOKEN = "vendor_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_VENDOR_ID = "vendor_id";

    public static String getBaseURL(Context context) {
        String serverIP = getServerIP(context);
        //return "http://" + serverIP + "/notify_api/public/api/";
        return "http://" + serverIP + "/api/";
    }

    public static String getServerIP(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public static void setServerIP(Context context, String baseUrl) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_BASE_URL, baseUrl).apply();
    }

    public static String getUserToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_TOKEN, null);
    }

    public static int getUserID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_USER_ID, 0);
    }

    public static void setUserToken(Context context, String userToken, int userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USER_TOKEN, userToken)
                .putInt(KEY_USER_ID, userId).apply();
    }

    public static String getVendorToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_VENDOR_TOKEN, null);
    }

    public static int getVendorID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_VENDOR_ID, 0);
    }

    public static void setVendorToken(Context context, String vendorToken, int vendorId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_VENDOR_TOKEN, vendorToken)
                .putInt(KEY_VENDOR_ID, vendorId).apply();
    }

}
