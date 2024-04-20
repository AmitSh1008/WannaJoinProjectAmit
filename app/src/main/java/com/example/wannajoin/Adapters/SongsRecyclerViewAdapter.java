package com.example.wannajoin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;

//import org.greenrobot.eventbus.EventBus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<DBCollection.Song> songs = new ArrayList<>();
    private Context context;
    private List<DBCollection.Song> filteredSongsList;

    public SongsRecyclerViewAdapter(Context context,ArrayList<DBCollection.Song> songs) {
        this.songs = songs;
        this.filteredSongsList = new ArrayList<>(songs);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;
        int placeholderResourceId = R.drawable.image_not_found;
        Glide.with(context)
                .asBitmap()
                .load(filteredSongsList.get(pos).getImage())
                .placeholder(placeholderResourceId)
                //.transform(new CircleCrop())
                .into(holder.imageSong);

        holder.nameSong.setText(filteredSongsList.get(pos).getName());

        holder.imageSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessages.PlaySongEvent(filteredSongsList.get(pos)));
                LoggedUserManager.getInstance().addRecentBySong(filteredSongsList.get(pos));
                EventBus.getDefault().post(new EventMessages.RecentAdded());


            }
        });

        //TODO add to playlist
        //holder.imageSong.setOnLongClickListener();

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
                EventBus.getDefault().post(new EventMessages.LibraryResultChangedEvent(filteredSongsList.size()));
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageSong;
        TextView nameSong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageSong = itemView.findViewById(R.id.imageItem);
            this.nameSong = itemView.findViewById(R.id.nameItem);
        }
    }
}
