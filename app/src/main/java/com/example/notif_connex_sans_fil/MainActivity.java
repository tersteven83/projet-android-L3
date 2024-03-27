package com.example.notif_connex_sans_fil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Intent wifiIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.wifiIntent = new Intent(this, WifiScanService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(this.wifiIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(this.wifiIntent);
    }
}