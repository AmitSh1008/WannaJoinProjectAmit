package com.example.wannajoin;

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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class GenresRecyclerViewAdapter extends RecyclerView.Adapter<GenresRecyclerViewAdapter.ViewHolder> implements Filterable {
    private ArrayList<DBCollection.Genre> genres = new ArrayList<>();
    private Context context;
    private List<DBCollection.Genre> filteredGenresList;

    public GenresRecyclerViewAdapter(Context context, ArrayList<DBCollection.Genre> genres) {
        this.genres = genres;
        this.filteredGenresList = new ArrayList<>(genres);
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
                .load(filteredGenresList.get(pos).getImage())
                .placeholder(placeholderResourceId)
                .into(holder.imageGenre);

        holder.nameGenre.setText(filteredGenresList.get(pos).getName());

        holder.imageGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessages.GenreLibraryEvent(filteredGenresList.get(pos)));
            }
        });


    }

    @Override
    public int getItemCount() {
        return filteredGenresList.size();
    }

    public void updateData(ArrayList<DBCollection.Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
        this.filteredGenresList.addAll(this.genres);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                List<DBCollection.Genre> filtered = new ArrayList<>();
                List<String> filteredImages = new ArrayList<>();

                for (DBCollection.Genre genre : genres) {
                    if (genre.getName().toLowerCase().contains(searchText)) {
                        filtered.add(genre);

                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredGenresList = (List<DBCollection.Genre>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageGenre;
        TextView nameGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageGenre = itemView.findViewById(R.id.imageItem);
            this.nameGenre = itemView.findViewById(R.id.nameItem);
        }
    }
}
