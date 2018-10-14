package com.example.andrew.musiceverywhere;

public class TrackText{
    public String track;
    public String album;
    public String artist;
    public String uri;

    public TrackText(String track, String album, String artist, String uri){
        this.track = track;
        this.album = album;
        this.artist = artist;
        this.uri = uri;
    }

    public String toString(){
        return track + " by " + artist + " off of " + album;
    }
}
