package com.turbybela.movietracker;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

public class NotificationHelper extends Service {
    public static final String CHANNEL_ID = "turbybela_MovieTracker";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_NAME = "MovieTracker";

    private NotificationManager manager;
    private SharedPreferences sp;

    private static NotificationHelper instance;

    private Handler handler;

    public static NotificationHelper getInstance() {
        return instance;
    }


    public void CreateNotificationChannel() {
        instance = this;
        if (manager == null) {
            manager = getSystemService(NotificationManager.class);
        }
        if (manager != null) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_NAME);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sp = getSharedPreferences("movies", MODE_PRIVATE);
        manager = getSystemService(NotificationManager.class);

        // Initialize a handler for background work
        HandlerThread handlerThread = new HandlerThread("NotificationHelperThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        CreateNotificationChannel();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private RemoteViews NO_notificationLayout = null;
    private RemoteViews notificationLayout = null;

    public void ShowNotification(){
        handler.post(this::_ShowNotification);
    }

    private void _ShowNotification(){
        CreateNotificationChannel();

        if (NO_notificationLayout == null)
            NO_notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_nolatest);
        if (notificationLayout == null) {
            notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);

            Intent refresh_intent = new Intent(this, NotificationActionReceiver.class);
            refresh_intent.setAction("refresh");
            PendingIntent refresh_PendingIntent = PendingIntent.getBroadcast(
                    this, 0, refresh_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            notificationLayout.setOnClickPendingIntent(R.id.notification_title, refresh_PendingIntent);

            Intent seasonInc_intent = new Intent(this, NotificationActionReceiver.class);
            seasonInc_intent.setAction("inc_season");
            PendingIntent seasonInc_PendingIntent = PendingIntent.getBroadcast(
                    this, 0, seasonInc_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            notificationLayout.setOnClickPendingIntent(R.id.season_inc, seasonInc_PendingIntent);

            Intent episodeInc_intent = new Intent(this, NotificationActionReceiver.class);
            episodeInc_intent.setAction("inc_episode");
            PendingIntent episodeInc_PendingIntent = PendingIntent.getBroadcast(
                    this, 0, episodeInc_intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            notificationLayout.setOnClickPendingIntent(R.id.episode_inc, episodeInc_PendingIntent);
        }

        String latest = sp.getString("__latest", "");
        Notification notification;
        if (latest.isBlank()){
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.large_movie)
                    .setCustomContentView(NO_notificationLayout)
                    .setCustomBigContentView(NO_notificationLayout)
                    .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                    .build();
            startForeground(NOTIFICATION_ID, notification);
        }
        else{
            String data = sp.getString(latest, "");
            if (data.isBlank()){
                SharedPreferences.Editor e = sp.edit();
                e.putString("__latest", "");
                e.commit();
                ShowNotification();
            }
            MovieEntry item = MovieEntry.EntryFromResource(latest, data);
            notificationLayout.setTextViewText(R.id.notification_title, item.getName());
            notificationLayout.setTextViewText(R.id.season_inc, "S" + item.GetSeason() + "+");
            notificationLayout.setTextViewText(R.id.episode_inc, "Ep" + item.GetEpisode() + "+");

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.large_movie)
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayout)
                    .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                    .build();
            startForeground(NOTIFICATION_ID, notification);

            Log.d("bwoa", "ShowNotification: started noti");
        }

    }

    public void DismissNotification(){
        stopForeground(STOP_FOREGROUND_REMOVE);  // Remove the notification
    }
}

