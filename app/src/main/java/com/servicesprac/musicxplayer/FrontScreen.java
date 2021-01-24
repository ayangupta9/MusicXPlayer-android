package com.servicesprac.musicxplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class FrontScreen extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MyTag";
    public static boolean SHOW_MINI_PLAYER = false;
    ListView songs;
    public static ArrayList<MusicFile> musicFiles;
    MyListAdapter arrayAdapter;
    ArrayList<String> songName, artistName, location, durationList;
    FrameLayout frag_bottom_player;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_LAST_PLAYED_KEY = "MUSIC_STORED_IN_SHARED";
    public static final String SONG_POSITION = "songPosition";
    public static String PATH_TO_FRAG = null;
    public static String SONG_NAME_TO_FRAG = null;
    public static String SONG_NAME = "SONG NAME";
    public static Integer POSITION_TO_FRAG = -1;
    public static final String CURRENT_PLAYING = "oneOrZero";
    public static Integer CURR_PLAY_TO_FRAG = 0;
    BottomPlayer bottomPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_screen);
        songs = findViewById(R.id.list);
        musicFiles = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {
            doStuff();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
                    if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Now view the List", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    private void doStuff() {
        getMusic(this);
        arrayAdapter = new MyListAdapter(FrontScreen.this, songName, artistName);
        songs.setAdapter(arrayAdapter);
        songs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(FrontScreen.this, MainActivity.class);
                intent.putExtra("pos", i);
                Toast.makeText(FrontScreen.this, "" + i, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    private void getMusic(Context context) {
        Uri songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        songName = new ArrayList<>();
        artistName = new ArrayList<>();
        location = new ArrayList<>();
        durationList = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };
        Cursor songCursor = context.getContentResolver().query(songsUri, projection, null,
                null, null);
        if (songCursor != null) {
            while (songCursor.moveToNext()) {
                String title = songCursor.getString(0);
                String artist = songCursor.getString(1);
                String path = songCursor.getString(2);
                String duration = songCursor.getString(3);
                MusicFile musicFile = new MusicFile(path, title, artist, duration);
                musicFiles.add(musicFile);
                songName.add(title);
                artistName.add(artist);
                Log.d(TAG, "getMusic: " + path);
                location.add(path);
                durationList.add(duration);
            }
            Log.d(TAG, "getMusic: " + musicFiles);
            songCursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putInt(CURRENT_PLAYING, 0);
        editor.apply();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Front Screen called");
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(MUSIC_LAST_PLAYED_KEY, null);
        String song = preferences.getString(SONG_NAME, null);
        int song_position = preferences.getInt(SONG_POSITION, -1);
        int curr_playing = preferences.getInt(CURRENT_PLAYING, 0);
        Log.d(TAG, "onResume: FrontScreen curr_playing " + curr_playing);
        Log.d(TAG, "onResume: FrontScreen current song position: " + song_position);
        if (path != null) {
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            SONG_NAME_TO_FRAG = song;
            POSITION_TO_FRAG = song_position;
            CURR_PLAY_TO_FRAG = curr_playing;
        } else {
            SHOW_MINI_PLAYER = false;
            PATH_TO_FRAG = null;
            SONG_NAME_TO_FRAG = null;
            POSITION_TO_FRAG = -1;
            CURR_PLAY_TO_FRAG = 0;
        }
    }
}