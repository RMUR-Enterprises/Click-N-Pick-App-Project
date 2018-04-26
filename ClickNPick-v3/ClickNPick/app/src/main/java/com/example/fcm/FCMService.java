package com.example.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.example.app.AppPreferences;
import com.example.app.BuildConfig;
import com.example.app.R;
import com.example.app.customer.CustomerOrdersActivity;
import com.example.app.vendor.VendorOrdersActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Default Notification Channel");
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (data == null) return;
        if (BuildConfig.DEBUG) System.err.println("Notification data: " + data);

        try {
            showNotification(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showNotification(Map<String, String> data) {
        String title = data.get("title");
        String text = data.get("text");
        int vendor = Integer.parseInt(data.get("vendor"));
        int userId = Integer.parseInt(data.get("userId"));

        Intent intent;
        if (vendor == 0) { //customer
            String userToken = AppPreferences.getUserToken(this);
            if (userToken == null) {
                System.err.println("No user token..");
                return;
            }
            if (userId != AppPreferences.getUserID(this)) {
                System.err.println("Different user id: " + AppPreferences.getUserID(this));
                return;
            }
            intent = new Intent(this, CustomerOrdersActivity.class);
            intent.putExtra(CustomerOrdersActivity.KEY_EXTRA_NOTIFCATION_ONLY, true);
        } else { // vendor
            String vendorToken = AppPreferences.getVendorToken(this);
            if (vendorToken == null) {
                System.err.println("No vendor token.");
                return;
            }
            if (userId != AppPreferences.getVendorID(this)) {
                System.err.println("Different vendor id: " + AppPreferences.getVendorID(this));
                return;
            }
            intent = new Intent(this, VendorOrdersActivity.class);
            intent.putExtra(VendorOrdersActivity.KEY_EXTRA_NOTIFCATION_ONLY, true);
        }

        int notificationId = 1000 + userId;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setOngoing(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(text)) builder.setContentText(text);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }

    }

}
