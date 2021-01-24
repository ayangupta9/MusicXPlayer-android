package com.servicesprac.musicxplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.utilcustomfiles.circularseekbar.CircularSeekBar;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.servicesprac.musicxplayer.BottomPlayer.playPause;
import static com.servicesprac.musicxplayer.BottomPlayer.songTitle;
import static com.servicesprac.musicxplayer.FrontScreen.CURRENT_PLAYING;
import static com.servicesprac.musicxplayer.FrontScreen.MUSIC_LAST_PLAYED;
import static com.servicesprac.musicxplayer.FrontScreen.POSITION_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.musicFiles;
import static com.servicesprac.musicxplayer.MyApp.ACTION_NEXT;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PAUSE;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PLAY;
import static com.servicesprac.musicxplayer.MyApp.ACTION_PREVIOUS;
import static com.servicesprac.musicxplayer.MyApp.CHANNEL_ID_1;
import static com.servicesprac.musicxplayer.MyApp.CHANNEL_ID_2;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyTag";
    public static String MESSAGE_KEY = "message_key";
    public static ImageView play_pause;
    public static ImageView next, prev;
    public static TextView songName, artistTitle, durationTitle;
    public static CircularSeekBar circularSeekBar;
    public static ObjectAnimator rotate;
    int musicSize = 10000;
    ImageView vinyl;
    SeekBar seekBar;
    public static int playPauseIndex = 0;
    int count = 0;
    MusicService musicService;
    Boolean bound = false;
    int x;
    String result, confirm;
    private Handler handler = new Handler();
    int y;
    public static int rot = 0;
    Intent recIntent;
    UtilFuncs utilFuncs;
    String bottomPlayerApproval;
    static int btnID;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            result = intent.getStringExtra(MESSAGE_KEY);
            assert result != null;
            if (result.equals("Finished")) {
                playPauseIndex = 0;
                play_pause.setImageResource(R.drawable.playbtn);
                playPause.setImageResource(R.drawable.playbtn);
                musicService.showNotification(R.drawable.playbtn, x, playPauseIndex);
                Toast.makeText(context, "" + result, Toast.LENGTH_SHORT).show();
                rotate.cancel();
                rot = 0;
            }
        }
    };
    private final BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            confirm = intent.getStringExtra("MediaPlayer");
            y = intent.getIntExtra("MediaPlayerPos", -1);
            assert confirm != null;
            if (confirm.equals("initialised")) {
                if (y != -1) {
                    x = y;
                    Log.d(TAG, "onReceive: Broadcast Receiver 2 x: " + x);
                }
                seekBarOperations(musicService.getDuration(), x);
            }
        }
    };
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: connected");
            MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
            musicService = myBinder.getService();
            bound = true;
            utilFuncs = new UtilFuncs(musicService);
            Intent intent = new Intent(MainActivity.this, MusicService.class);
            intent.putExtra("position", x);
            Log.d(TAG, "onServiceConnected: x is " + x);
            startService(intent);
            if (bottomPlayerApproval == null) {
                if (musicService != null) {
                    if (!musicService.isNull()) {
                        if (musicService.isPlaying()) {
                            musicService.stop();
                            Log.d(TAG, "onCreate: music stopped");
                        }
                        musicService.reset();
                        musicService.release();
                        musicService.mediaPlayerNull();
                        Log.d(TAG, "onCreate: mediaPlayer released");
                    }
                }
            }
            if (bottomPlayerApproval != null) {
            } else {
                play_pause.callOnClick();
            }
            if (!musicService.isNull()) {
                if (musicService.isPlaying()) {
                    play_pause.setImageResource(R.drawable.pausebtn);
                    playPauseIndex = 1;
                    rotate.start();
                    rot = 1;
                }
                seekBarOperations(musicService.getDuration(), x);
            }
            Log.d(TAG, "onServiceConnected: playPause clicked");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: called");
        recIntent = getIntent();
        x = recIntent.getIntExtra("pos", x);
        bottomPlayerApproval = recIntent.getStringExtra("bottomPlayerApprove");
        Log.d(TAG, "onCreate: " + x);
        Log.d(TAG, "onCreate: Bottom Player " + bottomPlayerApproval);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        songName = findViewById(R.id.songName);
        artistTitle = findViewById(R.id.artist);
        durationTitle = findViewById(R.id.dur);
        songName.setSelected(true);
        artistTitle.setSelected(true);
        vinyl = findViewById(R.id.vinyl);
        circularSeekBar = findViewById(R.id.circularseekbar);
        play_pause = findViewById(R.id.play);
        play_pause.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        songName.setText(musicFiles.get(x).getTitle());
        artistTitle.setText(musicFiles.get(x).getArtist());
        durationTitle.setText(musicFiles.get(x).getDuration());
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        rotate = ObjectAnimator.ofFloat(vinyl, View.ROTATION, 0f, 360f).setDuration(musicSize);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: MainActivity called");
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(MusicService.MUSIC_COMPLETE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver2, new IntentFilter(MusicService.SERVICE_STARTED));
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStart: MainActivity called");
        if (bound) {
            bound = false;
            unbindService(connection);
        }
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver2);
    }

    public void seekBarOperations(int max, int pos) {
        circularSeekBar.setMax(max);
        songName.setText(musicFiles.get(pos).getTitle());
        artistTitle.setText(musicFiles.get(pos).getArtist());
        durationTitle.setText(musicFiles.get(pos).getDuration());
        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser && musicService != null) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {
                    durationTitle.setText(formattedTime(musicService.getCurrentPosition()));
                    circularSeekBar.setProgress(musicService.getCurrentPosition());
                }
                handler.postDelayed(this, 1000);
                if (musicService.getDuration() / 1000 == musicService.getCurrentPosition() / 1000) {
                    circularSeekBar.setProgress(0);
                    return;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (playPauseIndex == 0) {
                    playPauseIndex = 1;
                    if (utilFuncs == null) {
                        utilFuncs = new UtilFuncs(musicService);
                    }
                    utilFuncs.playPausebtn(playPauseIndex, x);
                    musicService.showNotification(R.drawable.pausebtn, x, playPauseIndex);
                } else {
                    playPauseIndex = 0;
                    if (utilFuncs == null) {
                        utilFuncs = new UtilFuncs(musicService);
                    }
                    utilFuncs.playPausebtn(playPauseIndex, x);
                    musicService.showNotification(R.drawable.playbtn, x, playPauseIndex);
                }
                break;
            case R.id.next: {
                x = x + 1;
                if (utilFuncs == null) {
                    utilFuncs = new UtilFuncs(musicService);
                }
                utilFuncs.BtnNext(x);
                play_pause.setImageResource(R.drawable.pausebtn);
                songTitle.setText(musicFiles.get(x).getTitle());
                playPauseIndex = 1;
                break;
            }
            case R.id.previous: {
                x = x - 1;
                if (x < 0) {
                    x = musicFiles.size() - 1;
                }
                if (utilFuncs == null) {
                    utilFuncs = new UtilFuncs(musicService);
                }
                utilFuncs.BtnPrev(x);
                play_pause.setImageResource(R.drawable.pausebtn);
                songTitle.setText(musicFiles.get(x).getTitle());
                playPauseIndex = 1;
                break;
            }
        }
    }

    private String formattedTime(int currentPosition) {
        String totalout = "";
        String totalnew = "";
        String seconds = String.valueOf((currentPosition / 1000) % 60);
        String minutes = String.valueOf((currentPosition / 1000) / 60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalnew;
        } else {
            return totalout;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, 0);
        editor.apply();
        utilFuncs = null;
        System.gc();
    }
}