package com.servicesprac.musicxplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import static com.servicesprac.musicxplayer.FrontScreen.CURR_PLAY_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.musicFiles;
import static com.servicesprac.musicxplayer.MainActivity.btnID;
import static com.servicesprac.musicxplayer.MainActivity.next;
import static com.servicesprac.musicxplayer.MainActivity.play_pause;
import static com.servicesprac.musicxplayer.MainActivity.prev;
import static com.servicesprac.musicxplayer.MyApp.ACTION_NEXT;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PAUSE;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PLAY;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PREVIOUS;
import static com.servicesprac.musicxplayer.MyApp.CHANNEL_ID_2;

public class MusicService extends Service {
    private static final String TAG = "MyTag";
    public static final String MUSIC_COMPLETE = "musicComplete";
    public static final String SERVICE_STARTED = "serviceStarted";
    MediaPlayer mediaPlayer;
    Uri uri;
    int position = -1;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_LAST_PLAYED_KEY = "MUSIC_STORED_IN_SHARED";
    public static final String SONG_NAME = "SONG NAME";
    public static final String SONG_POSITION = "songPosition";
    public static final String CURRENT_PLAYING = "oneOrZero";

    MediaSessionCompat mediaSessionCompat;
    int playPauseIndex = 0;
    UtilFuncs utilFuncs;

    public MusicService() {
    }

    MyBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate Music Service: called");
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
        utilFuncs = new UtilFuncs(this);
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Music Service: called");
        position = intent.getIntExtra("position", position);
        if (position == musicFiles.size() || position == -1) {
            position = 0;
        }
        String actionName = intent.getStringExtra("ActionName");
        if (actionName != null) {
            switch (actionName) {
                case "play": {
                    Log.d(TAG, "onStartCommand: play notif");
                    playPauseIndex = 1;
                    utilFuncs.playPausebtn(playPauseIndex, position);
                    break;
                }
                case "pause": {
                    Log.d(TAG, "onStartCommand: pause notif");
                    playPauseIndex = 0;
                    utilFuncs.playPausebtn(playPauseIndex, position);
                    break;
                }
                case "next": {
                    Log.d(TAG, "onStartCommand: next notif");
                    position = position + 1;
                    utilFuncs.BtnNext(position);
                    playPauseIndex = 1;
                    break;
                }
                case "prev": {
                    Log.d(TAG, "onStartCommand: prev notif");
                    position = position - 1;
                    utilFuncs.BtnPrev(position);
                    playPauseIndex = 1;
                    break;
                }
            }
        }
        return START_STICKY;
    }

    public void completionActs() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent(MUSIC_COMPLETE);
                intent.putExtra(MainActivity.MESSAGE_KEY, "Finished");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: called");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: called");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: called");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        mediaPlayer.release();
    }

    public void start(int pos) {
        if (mediaPlayer == null) {
            createMP(pos);
            Log.d(TAG, "start: media player is prepared");
        }
        playPauseIndex = 1;
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, playPauseIndex);
        editor.apply();
        mediaPlayer.start();
        seekBarInfo(pos);
        completionActs();
    }

    public void startNext(int pos) {
        Log.d(TAG, "startNext: called");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        position = pos;
        playPauseIndex = 1;
        createMP(pos);
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, playPauseIndex);
        editor.apply();
        mediaPlayer.start();
        seekBarInfo(pos);
        completionActs();
    }

    public void startPrev(int pos) {
        Log.d(TAG, "startPrev: called");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        position = pos;
        playPauseIndex = 1;
        createMP(pos);
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, playPauseIndex);
        editor.apply();
        mediaPlayer.start();
        seekBarInfo(pos);
        completionActs();
    }

    public void stop() {
        mediaPlayer.stop();
        playPauseIndex = 0;
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, playPauseIndex);
        editor.apply();
        CURR_PLAY_TO_FRAG = 0;
    }

    public void pause() {
        mediaPlayer.pause();
        playPauseIndex = 0;
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, playPauseIndex);
        editor.apply();
        CURR_PLAY_TO_FRAG = 0;
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public void release() {
        mediaPlayer.release();
        Log.d(TAG, "release: mediaPlayer " + mediaPlayer);
    }

    public void createMP(int pos) {
        uri = Uri.parse(musicFiles.get(pos).getPath());
        Log.d(TAG, "createMP: path " + musicFiles.get(pos).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putString(MUSIC_LAST_PLAYED_KEY, uri.toString());
        editor.putString(SONG_NAME, musicFiles.get(pos).getTitle());
        editor.putInt(SONG_POSITION, pos);
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isNull() {
        return mediaPlayer == null;
    }

    public void mediaPlayerNull() {
        mediaPlayer = null;
    }

    public void seekBarInfo(int pos) {
        Intent intent1 = new Intent(SERVICE_STARTED);
        intent1.putExtra("MediaPlayer", "initialised");
        intent1.putExtra("MediaPlayerPos", pos);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(int buttonID, int x, int ppIndex) {
        Log.d(TAG, "showNotification: notification");
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.vinyl);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Intent previntent = new Intent(this, NotificationRec.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, previntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseintent = new Intent(this, NotificationRec.class);
        if (ppIndex == 0) {
            pauseintent.setAction(ACTION_PLAY);
        } else if (ppIndex == 1) {
            pauseintent.setAction(ACTION_PAUSE);
        }
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, NotificationRec.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(buttonID)
                .setLargeIcon(icon)
                .setContentTitle("" + musicFiles.get(x).getTitle())
                .setContentText("" + musicFiles.get(x).getArtist())
                .addAction(R.drawable.prevbtn, "Previous", prevPending)
                .addAction(buttonID, "Pause", pausePending)
                .addAction(R.drawable.nextbtn, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .build();
        startForeground(1, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (mediaPlayer != null) {
            Log.d(TAG, "onTaskRemoved: mediaPlayer not null");
            if (mediaPlayer.isPlaying()) {
                Log.d(TAG, "onTaskRemoved: mediaPlayer stopped");
                stop();
            }
            reset();
            release();
            Log.d(TAG, "onTaskRemoved: mediaPlayer released");
            Log.d(TAG, "onTaskRemoved: currently playing " + playPauseIndex);
        }
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, 0);
        editor.apply();
        stopService(rootIntent);
        stopForeground(true);
        System.gc();
        System.exit(0);
        super.onTaskRemoved(rootIntent);
    }
}
