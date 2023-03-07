package com.tul.music_player;

import static com.tul.music_player.PlayerActivity.listSongs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    MyBinder myBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "method");
        return myBinder;
    }

    public void setOnCompletionListener(PlayerActivity playerActivity) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    public class MyBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        if (myPosition != -1) {
            playMedia(myPosition);
        }
        return START_STICKY;
    }


    private void playMedia(int StartPosition) {
        musicFiles =    listSongs;

        position = StartPosition;
        if(mediaPlayer!= null){
            mediaPlayer.stop();
            mediaPlayer.release();

            if(mediaPlayer!= null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start(){
        mediaPlayer.start();
    }

    void stop(){
        mediaPlayer.stop();
    }

    boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

     void release(){
        mediaPlayer.release();
     }

     int getDuration(){
        return mediaPlayer.getDuration();
     }

     void seekTo(int pos){
        mediaPlayer.seekTo(pos);
     }

     int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
     }
     void createMediaPlayer(int position){
        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
     }

     void pause(){
        mediaPlayer.pause();
     }

     void OnCompeleted(){
        mediaPlayer.setOnCompletionListener(this);
     }

    public void onCompletion() {
    }

}
