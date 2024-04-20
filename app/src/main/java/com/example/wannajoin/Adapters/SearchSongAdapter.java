package com.example.wannajoin.Adapters;

import static com.example.wannajoin.Utilities.FBRef.refSongs;
import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchSongAdapter implements ListAdapter, Filterable {

    private Context context;
    private List<DBCollection.Song> filteredSongsList;
    private LayoutInflater inflater;
    private AutoCompleteTextView autoCompleteTextView;
    private DataSetObservable dataSetObservable = new DataSetObservable();


    public SearchSongAdapter(Context context, AutoCompleteTextView autoCompleteTextView) {
        super();
        this.context = context;
        this.filteredSongsList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        dataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        dataSetObservable.unregisterObserver(observer);
    }

    @Override
    public int getCount() {
        return filteredSongsList.size();
    }

    @Override
    public DBCollection.Song getItem(int position) {
        return filteredSongsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_imagename_vertical_item, parent, false);
            holder = new ViewHolder();
            holder.mainContainer = convertView.findViewById(R.id.mainImageNameContainer);
            holder.imageSong = convertView.findViewById(R.id.profilePicCustom);
            holder.nameSong = convertView.findViewById(R.id.usernameCustomTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DBCollection.Song song = getItem(position);
        int placeholderResourceId = R.drawable.image_not_found;
        Glide.with(context)
                .asBitmap()
                .load(song.getImage())
                .placeholder(placeholderResourceId)
                //.transform(new CircleCrop())
                .into(holder.imageSong);

        holder.nameSong.setText(song.getName());

        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get the AutoCompleteTextView's member variable "mPopup" using reflection
                    Field field = AutoCompleteTextView.class.getDeclaredField("mPopup");
                    field.setAccessible(true);
                    ListPopupWindow popup = (ListPopupWindow) field.get(autoCompleteTextView);

                    // Get the ListView from the ListPopupWindow
                    ListView listView = popup.getListView();

                    // Simulate item click at position 0
                    listView.performItemClick(listView, position, listView.getItemIdAtPosition(position));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //TODO add to playlist
        //holder.imageSong.setOnLongClickListener();

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return filteredSongsList.isEmpty();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<DBCollection.Song> filtered = new ArrayList<>();
                if (!constraint.toString().isEmpty())
                {
                    refSongs.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String username = dataSnapshot.child("name").getValue(String.class);
                                DBCollection.Song song = dataSnapshot.getValue(DBCollection.Song.class);
                                if ((song.getName().toLowerCase().contains(constraint.toString().toLowerCase()))) {
                                    filtered.add(song);
                                }
                            }


                            filteredSongsList.clear();
                            if (filtered != null && filtered.size() > 0)
                                filteredSongsList.addAll(filtered);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    private void notifyDataSetChanged() {
        dataSetObservable.notifyChanged();
    }

    private void notifyDataSetInvalidated() {
        dataSetObservable.notifyInvalidated();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private static class ViewHolder {
        LinearLayout mainContainer;
        ImageView imageSong;
        TextView nameSong;
    }

}
