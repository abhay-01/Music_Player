 package com.tul.music_player;

 import static com.tul.music_player.MainActivity.repeatButton;
 import static com.tul.music_player.MainActivity.shuffleButton;

 import android.content.ComponentName;
 import android.content.Context;
 import android.content.Intent;
 import android.content.ServiceConnection;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.graphics.Color;
 import android.graphics.drawable.GradientDrawable;
 import android.media.MediaMetadataRetriever;
 import android.media.MediaPlayer;
 import android.net.Uri;
 import android.os.Bundle;
 import android.os.Handler;
 import androidx.annotation.Nullable;
 import androidx.palette.graphics.Palette;

 import android.os.IBinder;
 import android.view.View;
 import android.view.animation.Animation;
 import android.view.animation.AnimationUtils;
 import android.widget.ImageView;
 import android.widget.RelativeLayout;
 import android.widget.SeekBar;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.appcompat.app.AppCompatActivity;

 import com.bumptech.glide.Glide;
 import com.google.android.material.floatingactionbutton.FloatingActionButton;

 import java.util.ArrayList;
 import java.util.Random;

 public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, ActionPlaying, ServiceConnection {

     TextView song_name, artist_name, duration_played, duration_total;
     ImageView alum_art, shuffle_off, repeat_off, prev, next, back;
     FloatingActionButton play_pause;
     SeekBar seekBar;
     int position = -1;
     public static ArrayList<MusicFiles> listSongs;
     static Uri uri;
     // static MediaPlayer mediaPlayer;
     private Handler handler = new Handler();
     private Thread playThread, prevThread, nextThread;
     private MusicService musicService;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_player);
         initView();
         getIntentMethod();

         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 if (musicService != null && fromUser) {
                     musicService.seekTo(progress*1000);
                 }
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });
         PlayerActivity.this.runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 if (musicService != null) {
                     int CurrentPosition = musicService.getCurrentPosition() / 1000;
                     seekBar.setProgress(CurrentPosition);
                     duration_played.setText(formattedTime(CurrentPosition));
                 }
                 handler.postDelayed(this, 1000);
             }
         });

         shuffle_off.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(shuffleButton){
                     shuffleButton = false;
                     shuffle_off.setImageResource(R.drawable.ic_baseline_shuffle_off);
                 } else {
                     shuffleButton = true;
                     shuffle_off.setImageResource(R.drawable.ic_baseline_shuffle_on_);
                 }
             }
         });

         repeat_off.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 if(repeatButton){
                     repeatButton = false;
                     repeat_off.setImageResource(R.drawable.ic_baseline_repeat_24);
                 }else {
                     repeatButton = true;
                     repeat_off.setImageResource(R.drawable.ic_baseline_repeat_on_24);
                 }
             }
         });
     }

     private void getIntentMethod() {
         position = getIntent().getIntExtra("position", -1);
         listSongs = MainActivity.musicFiles;

         if (listSongs != null) {
             play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
             uri = uri.parse(listSongs.get(position).getPath());
         }

         Intent i = new Intent(this, MusicService.class);
         i.putExtra("string position", position);
         startService(i);

     }

     private String metaData(Uri uri) {
         MediaMetadataRetriever retriever = new MediaMetadataRetriever();
         retriever.setDataSource(uri.toString());
         int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
         duration_total.setText(formattedTime(durationTotal));
         byte[] art = retriever.getEmbeddedPicture();
         Bitmap bitmap;

         if (art != null) {

             bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
             ImageAnimation(this,alum_art,bitmap);
             Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                 @Override
                 public void onGenerated(@Nullable Palette palette) {
                     Palette.Swatch swatch = palette.getDominantSwatch();
                     if (swatch != null) {
                         ImageView gredient = findViewById(R.id.imag_gredient);
                         RelativeLayout Container = findViewById(R.id.container);
                         gredient.setBackgroundResource(R.drawable.gradient_bg);
                         Container.setBackgroundResource(R.drawable.main_bg);
                         GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                 new int[]{swatch.getRgb(), 0x00000000});
                         gredient.setBackground(gradientDrawable);
                         GradientDrawable gradientDrawablebg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                 new int[]{swatch.getRgb(), swatch.getRgb()});
                         Container.setBackground(gradientDrawablebg);
                         song_name.setTextColor(swatch.getTitleTextColor());
                         artist_name.setTextColor(swatch.getBodyTextColor());
                     } else {

                             ImageView gredient = findViewById(R.id.imag_gredient);
                             RelativeLayout Container = findViewById(R.id.container);
                             gredient.setBackgroundResource(R.drawable.gradient_bg);
                             Container.setBackgroundResource(R.drawable.main_bg);
                             GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                     new int[]{0xff000000, 0x00000000});
                             gredient.setBackground(gradientDrawable);
                             GradientDrawable gradientDrawablebg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                     new int[]{0xff000000, 0x00000000});
                             Container.setBackground(gradientDrawablebg);
                             song_name.setTextColor(Color.WHITE);
                             artist_name.setTextColor(Color.DKGRAY);
                         }
                     }
             });
             }
         else {
             Glide.with(this)
                     .asBitmap()
                     .load(R.drawable.ic_launcher_background)
                     .into(alum_art);
                 ImageView gredient = findViewById(R.id.imag_gredient);
                 RelativeLayout Container = findViewById(R.id.container);
                 gredient.setBackgroundResource(R.drawable.gradient_bg);
                 Container.setBackgroundResource(R.drawable.main_bg);
                 song_name.setTextColor(Color.WHITE);
                 artist_name.setTextColor(Color.DKGRAY);
         }

         return uri.toString();
     }


     private void initView() {

         song_name = findViewById(R.id.song_name);
         artist_name = findViewById(R.id.artist_name);
         duration_played = findViewById(R.id.durationPlayed);
         duration_total = findViewById(R.id.durationTotal);
         alum_art = findViewById(R.id.cover);
         shuffle_off = findViewById(R.id.shuffle_button);
         repeat_off = findViewById(R.id.repeat);
         prev = findViewById(R.id.prev);
         next = findViewById(R.id.next);
         back = findViewById(R.id.prev);
         play_pause = findViewById(R.id.play_pause);
         seekBar = findViewById(R.id.seek_play);
     }

     private String formattedTime(int currentPosition) {
         String totalout = "";
         String totalNew = "";
         String seconds = String.valueOf(currentPosition % 60);
         String minutes = String.valueOf(currentPosition / 60);
         totalout = minutes + ":" + seconds;
         totalNew = minutes + ":" + "0" + seconds;

         if (seconds.length() == 1) {
             return totalNew;
         } else {
             return totalout;
         }
     }


     @Override
     protected void onResume() {
         Intent intent = new Intent(this, MusicService.class);
         bindService(intent,this, BIND_AUTO_CREATE);
         playThread();
         nextThread();
         prevThread();
         super.onResume();
     }

     @Override
     protected void onPause() {
         super.onPause();
         unbindService(this);
     }

     private void prevThread() {
         prevThread = new Thread() {
             @Override
             public void run() {
                 super.run();
                 prev.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         prevClicked();
                     }
                 });
             }
         };

         prevThread.start();

     }

     public void prevClicked() {

         if(musicService.isPlaying()){

             musicService.stop();
             musicService.release();
             if(shuffleButton && !repeatButton){
                 position = getRandom(listSongs.size()-1);
             } else if(!shuffleButton && !repeatButton){
                 position = ((position-1)<0 ? (listSongs.size()-1) : (position-1));
             }
             uri = Uri.parse(listSongs.get(position).getPath());
             musicService.createMediaPlayer(position);
             metaData(uri);
             song_name.setText((listSongs.get(position).getTitle()));
             artist_name.setText((listSongs.get(position).getArtist()));
             seekBar.setMax(musicService.getDuration() / 1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (musicService != null) {
                         int CurrentPosition = musicService.getCurrentPosition() / 1000;
                         seekBar.setProgress(CurrentPosition);
                     }
                     handler.postDelayed(this, 1000);
                 }
             });
             musicService.onCompletion();
             play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
             musicService.start();
         } else{

             musicService.stop();
             musicService.release();
             if(shuffleButton && !repeatButton){
                 position = getRandom(listSongs.size()-1);
             } else if(!shuffleButton && !repeatButton){
                 position = ((position-1)<0 ? (listSongs.size()-1) : (position-1));
             }
             uri = Uri.parse(listSongs.get(position).getPath());
             musicService.createMediaPlayer(position);
             metaData(uri);
             song_name.setText((listSongs.get(position).getTitle()));
             artist_name.setText((listSongs.get(position).getArtist()));
             seekBar.setMax(musicService.getDuration() / 1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (musicService != null) {
                         int CurrentPosition = musicService.getCurrentPosition() / 1000;
                         seekBar.setProgress(CurrentPosition);
                     }
                     handler.postDelayed(this, 1000);
                 }
             });
             musicService.onCompletion();
             play_pause.setBackgroundResource(R.drawable.ic_baseline_play);
         }
     }

     private void nextThread() {
         nextThread = new Thread() {
             @Override
             public void run() {
                 super.run();
                 next.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         nextClicked();
                     }
                 });
             }
         };

         nextThread.start();
     }

     public void nextClicked() {

         if(musicService.isPlaying()){

             musicService.stop();
             musicService.release();

             if(shuffleButton && !repeatButton){
                 position = getRandom(listSongs.size()-1);
             } else if(!shuffleButton && !repeatButton){
                 position = ((position+1)%listSongs.size());
             }
             uri = Uri.parse(listSongs.get(position).getPath());
             musicService.createMediaPlayer(position);
             metaData(uri);
             song_name.setText((listSongs.get(position).getTitle()));
             artist_name.setText((listSongs.get(position).getArtist()));
             seekBar.setMax(musicService.getDuration() / 1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (musicService != null) {
                         int CurrentPosition = musicService.getCurrentPosition() / 1000;
                         seekBar.setProgress(CurrentPosition);
                     }
                     handler.postDelayed(this, 1000);
                 }
             });
             musicService.onCompletion();
             play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
             musicService.start();
         } else {

             musicService.stop();
             musicService.release();
             if(shuffleButton && !repeatButton){
                 position = getRandom(listSongs.size()-1);
             } else if(!shuffleButton && !repeatButton){
                 position = ((position+1)%listSongs.size());
             }
             uri = Uri.parse(listSongs.get(position).getPath());
             musicService.createMediaPlayer(position);
             metaData(uri);
             song_name.setText((listSongs.get(position).getTitle()));
             artist_name.setText((listSongs.get(position).getArtist()));
             seekBar.setMax(musicService.getDuration() / 1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (musicService != null) {
                         int CurrentPosition = musicService.getCurrentPosition() / 1000;
                         seekBar.setProgress(CurrentPosition);
                     }
                     handler.postDelayed(this, 1000);
                 }
             });
             musicService.onCompletion();
             play_pause.setBackgroundResource(R.drawable.ic_baseline_play);
         }

     }

     private int getRandom(int i) {

         Random random = new Random();
         return random.nextInt(i+1);
     }

     private void playThread() {
         playThread = new Thread() {
             @Override
             public void run() {
                 super.run();
                 play_pause.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         playPauseClicked();
                     }
                 });
             }
         };

         playThread.start();
     }

     public void playPauseClicked() {

         if (musicService.isPlaying()) {

             play_pause.setImageResource(R.drawable.ic_baseline_play);
             musicService.pause();
             seekBar.setMax(musicService.getDuration() / 1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (musicService != null) {
                         int CurrentPosition = musicService.getCurrentPosition() / 1000;
                         seekBar.setProgress(CurrentPosition);
                     }
                     handler.postDelayed(this, 1000);
                 }
             });
         } else {
             play_pause.setImageResource(R.drawable.ic_baseline_pause_24);
             musicService.start();
             seekBar.setMax(musicService.getDuration() / 1000);
             PlayerActivity.this.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (musicService != null) {
                         int CurrentPosition = musicService.getCurrentPosition() / 1000;
                         seekBar.setProgress(CurrentPosition);
                     }
                     handler.postDelayed(this, 1000);
                 }
             });
         }
     }

     public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){

         Animation animout = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
         Animation animin = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

         animout.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {

                 Glide.with(context).load(bitmap).into(imageView);
                 animin.setAnimationListener(new Animation.AnimationListener() {
                     @Override
                     public void onAnimationStart(Animation animation) {

                     }

                     @Override
                     public void onAnimationEnd(Animation animation) {

                     }

                     @Override
                     public void onAnimationRepeat(Animation animation) {

                     }
                 });

                 imageView.startAnimation(animin);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {
             }
         });

         imageView.startAnimation(animout);
     }

     @Override
     public void onCompletion(MediaPlayer mp) {
         nextClicked();
         if(musicService!= null){
             musicService.createMediaPlayer(position);
             musicService.start();
             musicService.setOnCompletionListener(this);
         }
     }

     @Override
     public void onServiceConnected(ComponentName name, IBinder service) {
         MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
         musicService = myBinder.getService();
         Toast.makeText(this,"Connected"+musicService, Toast.LENGTH_SHORT)
                 .show();
         seekBar.setMax(musicService.getDuration() / 1000);
         metaData(uri);
         song_name.setText(listSongs.get(position).getTitle());
         artist_name.setText(listSongs.get(position).getTitle());
         musicService.setOnCompletionListener(this);
     }

     @Override
     public void onServiceDisconnected(ComponentName name) {
          musicService = null;
     }
 }











