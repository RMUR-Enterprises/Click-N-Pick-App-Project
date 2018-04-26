package com.example.app.models;

import android.text.format.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Order implements Serializable {

    public int id;
    public long pickUpTime;
    public int accepted; // 0 - rejected, 1 - accepted, 2 - pending.
    public String vendorReason;
    public String cancelReason;
    public int canceled; // 1 - canceled, 0 - others.
    public int userId;
    public ArrayList<Integer> item_ids;
    public int vendorId;

    @Override
    public String toString() {
        return "id: " + id + ", time: " + pickUpTime;
    }

    public String getStatus() {
        if (canceled == 1) {
            return "Canceled";
        } else if (accepted == 1) {
            return "Accepted";
        } else if (accepted == 0) {
            return "Rejected by vendor.";
        } else if (accepted == 2) {
            return "Pending";
        } else return "Unknown";
    }

    public String getReason() {
        if (vendorReason != null) return vendorReason;
        return cancelReason;
    }

    public String getItemsText() {
        if (item_ids == null) return "Null Items";
        else {
            int size = item_ids.size();
            if (size == 1) {
                return "1 item";
            }
            return size + " items";
        }
    }

    public String getPickupTimeText() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(pickUpTime);
        String timeString = DateUtils.getRelativeTimeSpanString(pickUpTime * 1000).toString();
        return "Pickup time: " + timeString;
    }

}
