package com.turbybela.movietracker;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {

    @SuppressLint("StaticFieldLeak")
    private static EntryCollection adapter;
    public static void SetAdapter(EntryCollection a){
        adapter = a;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        SharedPreferences sp = context.getSharedPreferences("movies", MODE_PRIVATE);

        if (action.equals("refresh")){
            NotificationHelper.getInstance().ShowNotification();
        }
        else if (action.equals("inc_season") || action.equals("inc_episode")){
            String latest = sp.getString("__latest", "");
            if (latest.isBlank()) return;

            String entryData = sp.getString(latest, "");
            if (entryData.isBlank()) return;

            MovieEntry entry = MovieEntry.EntryFromResource(latest, entryData);

            if (action.equals("inc_season")) entry.IncrementSeason();
            else entry.IncrementEpisode();

            SharedPreferences.Editor e = sp.edit();
            e.putString(latest, entry.DataOut());
            e.commit();

            NotificationHelper.getInstance().ShowNotification();
            if (adapter != null) adapter.notifyDataSetChanged();
        }

    }
}