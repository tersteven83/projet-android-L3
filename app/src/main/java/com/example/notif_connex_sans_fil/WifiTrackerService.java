package com.example.notif_connex_sans_fil;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class WifiTrackerService extends Service {

    private static final String TAG = "WifiTrackerService";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyChannel";
    private String currentWifiSSID;

    private WifiManager wifiManager;
    private BroadcastReceiver wifiReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiStateReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        createNotificationChannel();

        // Create a foreground notification to indicate the service is running
        Intent notificationIntent = new Intent(this, MainActivity.class); // Replace with your activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID) // Replace with your notification channel ID
                .setContentTitle("WiFi Tracker Running")
                .setContentText("Tracking WiFi state changes")
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your icon resource
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "WiFi Tracker Service",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Tracks changes in WiFi state");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Not intended to be bound
        return null;
    }

    // Get current wifi SSID (optional, can be exposed through a method)
    public String getCurrentWifiSSID() {
        return currentWifiSSID;
    }

    private class WifiStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) { // Check for valid connection
                    currentWifiSSID = wifiInfo.getSSID();
                    Log.i(TAG, "Connected to WiFi: " + currentWifiSSID);
                } else {
                    currentWifiSSID = null;
                    Log.i(TAG, "WiFi disconnected");
                }
            }
        }
    }
}
