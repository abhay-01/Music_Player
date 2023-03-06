package com.tul.music_player;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<MusicFiles> files;

    MusicAdapter(Context context, ArrayList<MusicFiles> files){
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.song_name.setText(files.get(position).getTitle());
        byte[] image = new byte[0];
        try {
            image = getAlbumArt(files.get(position).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(image!= null){
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
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

        holder.more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            Toast.makeText(context, "Song Deleted!", Toast.LENGTH_SHORT).show();
                            deleteFile(position,v);
                            break;
                    }
                    return true;
                });
            }
        });
    }

    private void deleteFile(int position, View v) {

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(files.get(position).getId()));
        File file = new File(files.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            context.getContentResolver().delete(contentUri,null,null);
            files.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, files.size());
            Snackbar.make(v, "Song Deleted Successfully!", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(v, "Can't be deleted", Snackbar.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView song_name;
        ImageView album_image, more_option;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            song_name = itemView.findViewById(R.id.song_name);
            album_image = itemView.findViewById(R.id.music_img);
            more_option = itemView.findViewById(R.id.more_option);
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
