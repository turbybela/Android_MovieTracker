package com.turbybela.movietracker;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedPreferencesHelper {

    private static SharedPreferencesHelper instance;
    private SharedPreferences sharedPreferences;
    private List<OnSharedPreferenceChangeListener> listeners;

    private SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("movies", MODE_PRIVATE);
        listeners = new ArrayList<>();
    }

    public static synchronized SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Add a listener for any preference change
    public void addOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    // Remove a listener
    public void removeOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    // Listener interface
    public interface OnSharedPreferenceChangeListener
            extends SharedPreferences.OnSharedPreferenceChangeListener {
    }

    // Register all existing preferences to listen for changes
    public void registerAllPreferencesListener() {
        Map<String, ?> allPreferences = sharedPreferences.getAll();
        for (final Map.Entry<String, ?> entry : allPreferences.entrySet()) {
            if (entry.getValue() instanceof String) {
                sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
                    for (OnSharedPreferenceChangeListener listener : listeners) {
                        listener.onSharedPreferenceChanged(sharedPreferences, key);
                    }
                });
            }
        }
    }
}
