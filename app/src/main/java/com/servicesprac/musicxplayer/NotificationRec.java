package com.servicesprac.musicxplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.servicesprac.musicxplayer.MyApp.ACTION_NEXT;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PAUSE;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PLAY;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PREVIOUS;

public class NotificationRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if (actionName != null) {
            switch (actionName) {
                case ACTION_PLAY: {
                    serviceIntent.putExtra("ActionName", "play");
                    context.startService(serviceIntent);
                    break;
                }

                case ACTION_PAUSE: {
                    serviceIntent.putExtra("ActionName", "pause");
                    context.startService(serviceIntent);
                    break;
                }

                case ACTION_PREVIOUS: {
                    serviceIntent.putExtra("ActionName", "prev");
                    context.startService(serviceIntent);
                    break;
                }

                case ACTION_NEXT: {
                    serviceIntent.putExtra("ActionName", "next");
                    context.startService(serviceIntent);
                    break;
                }
            }
        }
    }
}
