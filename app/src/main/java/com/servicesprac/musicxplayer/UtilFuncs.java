package com.servicesprac.musicxplayer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import static com.servicesprac.musicxplayer.BottomPlayer.playPause;
import static com.servicesprac.musicxplayer.FrontScreen.CURR_PLAY_TO_FRAG;
import static com.servicesprac.musicxplayer.FrontScreen.musicFiles;
import static com.servicesprac.musicxplayer.MainActivity.play_pause;
import static com.servicesprac.musicxplayer.MainActivity.rot;
import static com.servicesprac.musicxplayer.MainActivity.rotate;

public class UtilFuncs {

    MusicService musicService;

    public UtilFuncs(MusicService musicService) {
        this.musicService = musicService;
    }

    public UtilFuncs() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playPausebtn(int playPauseIndex, int x) {
        if (playPauseIndex == 1) {
            musicService.start(x);
            musicService.showNotification(R.drawable.pausebtn, x, playPauseIndex);
            play_pause.setImageResource(R.drawable.pausebtn);
            playPause.setImageResource(R.drawable.pausebtn);
            CURR_PLAY_TO_FRAG = 1;
            if (rot != 0) {
                rotate.resume();
            } else {
                rotate.start();
                rot = 1;
            }

        } else {
            if (musicService.isPlaying()) {
                musicService.pause();
                rotate.pause();
                CURR_PLAY_TO_FRAG = 0;
            }
            musicService.showNotification(R.drawable.playbtn, x, playPauseIndex);
            play_pause.setImageResource(R.drawable.playbtn);
            playPause.setImageResource(R.drawable.playbtn);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void BtnNext(int x) {
//        x = x + 1;
        if (x >= musicFiles.size()) {
            x = 0;
        }
        musicService.startNext(x);
        rotate.cancel();
        rot = 0;
        rotate.start();
        rot = 1;
        musicService.showNotification(R.drawable.pausebtn, x, 1);
        play_pause.setImageResource(R.drawable.pausebtn);
        playPause.setImageResource(R.drawable.pausebtn);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void BtnPrev(int x) {
//        x = x - 1;
        if (x < 0) {
            x = musicFiles.size() - 1;
        }
        musicService.startPrev(x);
        rotate.cancel();
        rot = 0;
        rotate.start();
        rot = 1;
        musicService.showNotification(R.drawable.pausebtn, x, 1);
        play_pause.setImageResource(R.drawable.pausebtn);
        playPause.setImageResource(R.drawable.pausebtn);
    }

}
