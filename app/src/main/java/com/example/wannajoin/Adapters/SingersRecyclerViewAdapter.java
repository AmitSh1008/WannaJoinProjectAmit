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

import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Utilities.EventMessages;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SingersRecyclerViewAdapter extends RecyclerView.Adapter<SingersRecyclerViewAdapter.ViewHolder> implements Filterable {

    private ArrayList<DBCollection.Singer> singers = new ArrayList<>();
    private Context context;
    private List<DBCollection.Singer> filteredSingersList;

    public SingersRecyclerViewAdapter(Context context, ArrayList<DBCollection.Singer> singers) {
        this.singers = singers;
        this.filteredSingersList = new ArrayList<>(singers);
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
                .load(filteredSingersList.get(pos).getImage())
                .placeholder(placeholderResourceId)
                //.transform(new CircleCrop())
                .into(holder.imageSinger);

        holder.nameSinger.setText(filteredSingersList.get(pos).getName());

        holder.imageSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new EventMessages.SingerLibraryEvent(singers.get(pos)));
            }
        });


    }

    @Override
    public int getItemCount() {
        return filteredSingersList.size();
    }

    public void updateData(ArrayList<DBCollection.Singer> singers) {
        this.singers.clear();
        this.singers.addAll(singers);
        this.filteredSingersList.addAll(this.singers);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                List<DBCollection.Singer> filtered = new ArrayList<>();
                List<String> filteredImages = new ArrayList<>();

                for (DBCollection.Singer singer : singers) {
                    if (singer.getName().toLowerCase().contains(searchText)) {
                        filtered.add(singer);

                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredSingersList = (List<DBCollection.Singer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageSinger;
        TextView nameSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageSinger = itemView.findViewById(R.id.imageItem);
            this.nameSinger = itemView.findViewById(R.id.nameItem);
        }
    }
}
