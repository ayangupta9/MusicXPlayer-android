package com.servicesprac.musicxplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static com.servicesprac.musicxplayer.FrontScreen.CURR_PLAY_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.PATH_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.POSITION_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.SHOW_MINI_PLAYER;
import static com.servicesprac.musicxplayer.FrontScreen.SONG_NAME;
import static com.servicesprac.musicxplayer.FrontScreen.SONG_NAME_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.musicFiles;
import static com.servicesprac.musicxplayer.MainActivity.next;
import static com.servicesprac.musicxplayer.MainActivity.playPauseIndex;
import static com.servicesprac.musicxplayer.MainActivity.play_pause;
import static com.servicesprac.musicxplayer.MainActivity.prev;
import static com.servicesprac.musicxplayer.MusicService.CURRENT_PLAYING;
import static com.servicesprac.musicxplayer.MusicService.MUSIC_LAST_PLAYED;

public class BottomPlayer extends Fragment {
    private static final String TAG = "MyTag";
    public static ImageView nextBtn, prevBtn, playPause;
    public static TextView songTitle;
    View view;

    public BottomPlayer() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bottom_player, container, false);
        songTitle = view.findViewById(R.id.songname_mini);
        nextBtn = view.findViewById(R.id.skip_next_bottom);
        prevBtn = view.findViewById(R.id.skip_prev_bottom);
        playPause = view.findViewById(R.id.play_song);
        songTitle.setSelected(true);
        playPause.setImageResource(R.drawable.playbtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (next != null) {
                    next.callOnClick();
                } else {
                    Toast.makeText(getActivity(), "Play any song to show miniplayer", Toast.LENGTH_SHORT).show();
                }
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prev != null) {
                    prev.callOnClick();
                } else {
                    Toast.makeText(getActivity(), "Play any song to show miniplayer", Toast.LENGTH_SHORT).show();
                }
            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play_pause != null) {
                    if (CURR_PLAY_TO_FRAG == 1) {
                        Log.d(TAG, "onClick: Bottom player playing value " + CURR_PLAY_TO_FRAG);
                        playPauseIndex = 1;
                        play_pause.callOnClick();
                    } else {
                        Log.d(TAG, "onClick: Bottom player playing value " + CURR_PLAY_TO_FRAG);
                        playPauseIndex = 0;
                        play_pause.callOnClick();
                    }
                } else {
                    Toast.makeText(getActivity(), "Play any song to show miniplayer", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (POSITION_TO_FRAG == -1) {
                    Toast.makeText(getActivity(), "Play any song to show miniplayer", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: BottomPlayer clicked");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("pos", POSITION_TO_FRAG);
                    intent.putExtra("bottomPlayerApprove", "true");
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: bottom player resumed");
        super.onResume();
        if (SHOW_MINI_PLAYER) {
            if (PATH_TO_FRAG != null) {
                nextBtn.setVisibility(View.VISIBLE);
                prevBtn.setVisibility(View.VISIBLE);
                playPause.setVisibility(View.VISIBLE);
                songTitle.setText(SONG_NAME_TO_FRAG);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}