package com.tul.music_player;

import static com.tul.music_player.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    ImageView albumPhoto;
    RecyclerView recyclerView;
    String albumName;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    ArrayList<MusicFiles> musicfiles = MainActivity.musicFiles;
    AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.album_recycler);
        albumPhoto = findViewById(R.id.album_photo);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;

        for (int i = 0; i < musicFiles.size(); i++) {
            String album = musicFiles.get(i).getAlbum();
            if (albumName != null && albumName.equals(album)) {
                albumSongs.add(j, musicFiles.get(i));
                j++;
            }
        }

        if (!albumSongs.isEmpty()) {
            byte[] image = new byte[0];

            try {
                image = getAlbumArt(albumSongs.get(0).getAlbum());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image != null) {
                Glide.with(this)
                        .load(image)
                        .into(albumPhoto);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_launcher_background)
                        .into(albumPhoto);
            }
        } else {
            Toast.makeText(this,"Album is Empty", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!(albumSongs.size() < 1)) {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
             return art;
        }
}