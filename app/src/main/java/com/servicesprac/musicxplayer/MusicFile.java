package com.servicesprac.musicxplayer;

public class MusicFile {

    private String path;
    private String title;
    private String artist;
    private String duration;

    public MusicFile(String path, String title, String artist, String duration) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }
}
