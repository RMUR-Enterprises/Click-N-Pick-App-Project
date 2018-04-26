package com.example.app;

import android.content.Context;

@SuppressWarnings("WeakerAccess")
public class Routes {

    public static String getCustomerSignUpUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "register";
    }

    public static String getLoginUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "login";
    }

    public static String getProfileUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "profile";
    }

    public static String getVendorListUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "vendors";
    }

    public static String getOrdersUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "orders";
    }

    public static String getOrdersAcceptUrl(Context context, int orderId) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "orders/" + orderId + "/accept";
    }

    public static String getOrdersCompleteUrl(Context context, int orderId) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "orders/" + orderId + "/complete";
    }

    public static String getCategoriesUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "items";
    }

    public static String getItemUpdateUrl(Context context, int itemId) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "items/" + itemId + "/update";
    }

    public static String getItemDeleteUrl(Context context, int itemId) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "items/" + itemId + "/delete";
    }

    public static String getProfilePicUrl(Context context, String picName) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "profile/pic/" + picName;
    }

    public static String getUpdateProfilePicUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "profile/pic";
    }

    public static String getUpdateVendorProfileUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "vendors/update";
    }

    public static String getUpdateCustomerProfileUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "customer/update";
    }

    public static String getChangePasswordUrl(Context context) {
        String baseUrl = AppPreferences.getBaseURL(context);
        return baseUrl + "auth/update";
    }

    public static String getImagePicUrl(Context context, String image) {
        String serverIP = AppPreferences.getServerIP(context);
        //return "http://" + serverIP + "/notify_api/public/img/" + image;
        return "http://" + serverIP + "/img/" + image;
    }

}
