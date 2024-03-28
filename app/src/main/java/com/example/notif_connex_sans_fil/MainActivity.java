package com.example.notif_connex_sans_fil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Intent wifiScanIntent;
    private Intent wifiTrackingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.wifiScanIntent = new Intent(this, WifiScanService.class);
        startService(this.wifiScanIntent);

        this.wifiTrackingIntent = new Intent(this, WifiTrackerService.class);
        startService(this.wifiTrackingIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopService(this.wifiScanIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(this.wifiScanIntent);
        stopService(this.wifiTrackingIntent);
    }
}