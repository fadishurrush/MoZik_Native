package com.example.mozik;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView tvTitle,tvcurrentTime,tvTotalTime;
    private SeekBar seekBar;
    private ImageView pausePlay,nextbtn,previousebtn,musicicon;
    private ArrayList<AudioModel> songslist;
    private AudioModel currentSong;
    private MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();

    private int x= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);


        connect();
        songslist=(ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();
        if (!MyMediaPlayer.previousSong.getTitle().equals(MyMediaPlayer.currentTitle)){
            MyMediaPlayer.previousSong.setSong(currentSong);
            preparePlayer();
        }else{
            continuePlayMusic();
        }
        App.createNotification(MusicPlayerActivity.this,currentSong,R.drawable.ic_baseline_pause_circle_outline_24,MyMediaPlayer.currentIndex,songslist.size()-1);

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    tvcurrentTime.setText(convertToMMS(mediaPlayer.getCurrentPosition()+""));
                    Log.i(TAG, "tvcurrentTime " + convertToMMS(mediaPlayer.getCurrentPosition()+""));
                    if (mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicicon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicicon.setRotation(x);
                    }
                }
                new Handler().postDelayed(this,100);
                Log.d(TAG, "run() called");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer!=null && b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void connect() {
        tvTitle=findViewById(R.id.tvSongtitle);
        tvcurrentTime=findViewById(R.id.tvcurrentTime);
        tvTotalTime=findViewById(R.id.tvTotalTime);
        seekBar=findViewById(R.id.seekBar);
        pausePlay=findViewById(R.id.pause_play);
        nextbtn=findViewById(R.id.next);
        previousebtn=findViewById(R.id.previous);
        musicicon=findViewById(R.id.music_icon_big);
        tvTitle.setSelected(true);

    }
    public void setResourcesWithMusic(){
        currentSong= songslist.get(MyMediaPlayer.currentIndex);
        tvTitle.setText(currentSong.getTitle());
        tvTotalTime.setText(convertToMMS(currentSong.getDuration()));
        pausePlay.setOnClickListener(view -> pausePlay());
        nextbtn.setOnClickListener(view -> playNextSong());
        previousebtn.setOnClickListener(view -> playPreviousSong());

    }
    private void continuePlayMusic(){
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(MyMediaPlayer.leftoff);
            mediaPlayer.start();
            seekBar.setProgress(MyMediaPlayer.leftoff);
            seekBar.setMax(mediaPlayer.getDuration());
        }catch (Exception e){
            Log.e(TAG, "continueplayMusic: exception ", e);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMediaPlayer.leftoff=mediaPlayer.getCurrentPosition();

    }

    private void preparePlayer(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        }catch (Exception e){
            Log.e(TAG, "preparePlayer: exception ", e);
        }
    }
    private void playNextSong(){

        if(MyMediaPlayer.currentIndex==songslist.size()){
            return;
        }

        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        preparePlayer();
    }
    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex==0){
            return;
        }

        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
        preparePlayer();
    }
    private void pausePlay(){
    if (mediaPlayer.isPlaying()){
        mediaPlayer.pause();
    }else{
        mediaPlayer.start();
    }
    }
    public static String convertToMMS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public void goBack(View view) {
        Intent i = new Intent(this,MainActivity.class);
        this.startActivity(i);

    }
}