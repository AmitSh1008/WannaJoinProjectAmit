package com.example.wannajoin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Activities.FollowerFollowingProfileActivity;
import com.example.wannajoin.Managers.PlaylistManager;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SongInPlaylistAdapter extends RecyclerView.Adapter<SongInPlaylistAdapter.ViewHolder> implements Filterable {

    private ArrayList<DBCollection.Song> songs = new ArrayList<>();
    private final Context context;
    private List<DBCollection.Song> filteredSongsList;
    private int selectedPosition = -1;

    public SongInPlaylistAdapter(Context context, ArrayList<DBCollection.Song> songs) {
        this.songs = songs;
        this.filteredSongsList = new ArrayList<>(songs);
        this.context = context;
    }

    @NonNull
    @Override
    public SongInPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_imagename_vertical_item, parent, false);
        return new SongInPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongInPlaylistAdapter.ViewHolder holder, int position) {
        final int pos = position;
        DBCollection.Song songToBuild = filteredSongsList.get(pos);
        int placeholderResourceId = R.drawable.image_not_found;
        Glide.with(context)
                .asBitmap()
                .load(songToBuild.getImage())
                .placeholder(placeholderResourceId)
                //.transform(new CircleCrop())
                .into(holder.profilePicImageView);

        holder.userNameTextView.setText(songToBuild.getName());
        if (RoomManager.getInstance().getCurrentRoom().getCurrentSong() != null && Objects.equals(songToBuild.getId(), RoomManager.getInstance().getCurrentRoom().getCurrentSong().getId())) {
            holder.userNameTextView.setTextColor(Color.parseColor("#9ADE7B"));
        } else {
            holder.userNameTextView.setTextColor(Color.WHITE);
        }
        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                PlaylistManager.getInstance().jumpToSong(songToBuild);

            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredSongsList.size();
    }

    public void updateData() {
        filteredSongsList = songs;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                List<DBCollection.Song> filtered = new ArrayList<>();

                for (DBCollection.Song song : songs) {
                    if (song.getName().toLowerCase().contains(searchText)) {
                        filtered.add(song);

                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredSongsList = (List<DBCollection.Song>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout mainContainer;
        TextView userNameTextView;
        ImageView profilePicImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mainContainer = itemView.findViewById(R.id.mainImageNameContainer);
            this.profilePicImageView = itemView.findViewById(R.id.profilePicCustom);
            this.userNameTextView = itemView.findViewById(R.id.usernameCustomTextView);
        }
    }
}
