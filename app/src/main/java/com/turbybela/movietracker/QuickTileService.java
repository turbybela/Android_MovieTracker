package com.turbybela.movietracker;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.annotation.Nullable;
import androidx.core.app.ServiceCompat;
import androidx.core.content.ContextCompat;

public class QuickTileService extends TileService {


    private Intent serviceIntent = null;


    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (tile != null) {
            NotificationHelper.getInstance().ShowNotification();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
         serviceIntent = new Intent(getApplicationContext(), NotificationHelper.class);
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);


        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);

        // Register a listener to detect any preference change
        SharedPreferencesHelper.OnSharedPreferenceChangeListener listener =
                (sharedPreferences, key) -> {
                    // Handle preference change here
                    NotificationHelper.getInstance().ShowNotification();
                };
        sharedPreferencesHelper.addOnSharedPreferenceChangeListener(listener);

        // Optional: Register listener for all existing preferences
        sharedPreferencesHelper.registerAllPreferencesListener();
    }

    @Override
    public void onDestroy() {
//        stopService(serviceIntent);
        super.onDestroy();
    }

    @Override
    public void onStartListening() {
        if (serviceIntent == null) {
            serviceIntent = new Intent(getApplicationContext(), NotificationHelper.class);
        }
        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }

    @Override
    public void onStopListening() {

    }
}