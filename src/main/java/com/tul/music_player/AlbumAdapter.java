package com.tul.music_player;

import static com.tul.music_player.PlayerActivity.uri;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {

    private Context context;
    private ArrayList<MusicFiles> albumFiles;
    View view;

   public AlbumAdapter(Context context, ArrayList<MusicFiles> albumFiles){
        this.context = context;
        this.albumFiles = albumFiles;
    }

    public AlbumAdapter(View view) {
    }

    @NonNull
    @Override
    public AlbumAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.album_item, parent, false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.album_name.setText(albumFiles.get(position).getAlbum());
        byte[] image = new byte[0];
        try {
            image = getAlbumArt(albumFiles.get(position).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .into(holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,AlbumDetails.class);
                i.putExtra("album", albumFiles.get(position).getAlbum());

                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView album_name;
        ImageView album_image;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            album_name = itemView.findViewById(R.id.album_name);
             album_image = itemView.findViewById(R.id.album_image);
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

