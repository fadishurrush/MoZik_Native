package com.example.mozik;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer instance;

    public static MediaPlayer getInstance() {
        if(instance==null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static String currentTitle = "-1";
    public static AudioModel previousSong = new AudioModel();
    public static int leftoff = -1;
    public static int currentIndex = -1;
}
