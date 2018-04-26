package com.example.utils;

import java.util.Calendar;

public class AppUtils {

    public static String getDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar.getTime().toString();
    }

}
