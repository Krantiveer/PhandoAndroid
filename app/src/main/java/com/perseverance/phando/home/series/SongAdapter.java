package com.perseverance.phando.home.series;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.perseverance.phando.R;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Episode> songs;
    ExoPlayer player;

    public SongAdapter(Context context, List<Episode> songs, ExoPlayer player) {
        this.context = context;
        this.songs = songs;
        this.player = player;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Episode song = songs.get(position);
        SongViewHolder viewHolder = (SongViewHolder) holder;

        // set values to the items
        viewHolder.title.setText(song.getTitle());
        viewHolder.duration.setText(song.getDuration_str());

        Uri uriImage = Uri.parse(song.getThumbnail());

        if (uriImage != null) {

            Glide.with(context)
                    .load(uriImage)
                    .into(viewHolder.artViewHolder);

            //  viewHolder.artViewHolder.setImageURI(uriImage);

           /* if(viewHolder.artViewHolder.getDrawable()==null){
                viewHolder.artViewHolder.setImageResource(R.drawable.error_placeholder);


            }*/

        }
    /*    viewHolder.itemView.setOnClickListener(view -> {
            // playing the song
            if (!player.isPlaying()) {
                player.setMediaItems(getMediaItems(), position, 0);

            } else {

                // pause the song
                player.pause();
                player.seekTo(position, 0);
            }
            player.prepare();
            player.play();
        });*/

    }

   /* private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Episode episode : songs) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri("https:\\/\\/phando029.s.llnwi.net\\/audios\\/series\\/27\\/season_24_1676296969.mp3")
                    .setMediaMetadata(getMetaData(episode))
                    .build();

            mediaItems.add(mediaItem);

        }
        return mediaItems;
    }

    private MediaMetadata getMetaData(Episode songs) {

        return new MediaMetadata.Builder()
                .setTitle(songs.getTitle())
               *//* .setArtworkUri(Uri.parse(songs.getThumbnail()))*//*
                .build();
    }*/


    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView artViewHolder;
        TextView title, duration;


        @SuppressLint("CutPasteId")
        public SongViewHolder(@Nullable View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            duration = itemView.findViewById(R.id.songTitle);
            artViewHolder = itemView.findViewById(R.id.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    // filter song search result
    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<Episode> filteredList) {
        songs = filteredList;
        notifyDataSetChanged();
    }
}
