package com.servicesprac.musicxplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotifForegroundService extends Service {
    public NotifForegroundService() {
    }

    int songNum;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songNum = intent.getIntExtra("songNum", -1);
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotif() {
        String CHANNEL_ID = "MusicX";
        String CHANNEL_NAME = "MusicXPlayer";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.WHITE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentTitle("")
                .setContentText("MusicXPlayer playing...");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
