package com.example.notif_connex_sans_fil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WifiScanService extends Service {
    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private ExecutorService executorService;
    private List<ScanResult> scanResults;

    public static final String TAG = "WifiScanService";
    public static final String LIST_SCAN_CHANGED = "com.example";
    public final IBinder binder = new WifiScanBinder();
    public class WifiScanBinder extends Binder {
        public WifiScanService getService(){
            return WifiScanService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(WIFI_SERVICE);
        this.executorService = Executors.newFixedThreadPool(5);
        this.wifiScanReceiver = new WifiScanReceiver();
        registerReceiver(listWifiChanged, new IntentFilter(LIST_SCAN_CHANGED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(wifiManager.isWifiEnabled()){
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if((wifiConnection != null) && wifiConnection.isConnectedOrConnecting()){
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.i(TAG, "Connecté amty pr e: " + wifiInfo.getSSID());
            } else {
                this.executorService.execute(new WifiScanExecutor(startId));
            }
            return START_STICKY;
        }
        else {
            Toast.makeText(this, "Wifi disabled", Toast.LENGTH_LONG);
            return START_REDELIVER_INTENT;
        }

    }

    private class WifiScanExecutor implements Runnable {
        private int startId;

        WifiScanExecutor(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {
            Log.i(TAG, "WifiService begins the scan");
            scanWifi();
        }
    }

    public void scanWifi() {
        wifiManager.startScan();
        registerReceiver( wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private final BroadcastReceiver listWifiChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), LIST_SCAN_CHANGED)) {
                ArrayList<String> wifiScanResults = intent.getStringArrayListExtra("scanResults");
                if (wifiScanResults != null)
                    for(String ssid : wifiScanResults){
                        Log.i(TAG, ssid);
                    }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(listWifiChanged);
        unregisterReceiver(wifiScanReceiver);
    }
}
