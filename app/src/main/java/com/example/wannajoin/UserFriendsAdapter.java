package com.example.wannajoin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.ViewHolder> implements Filterable {

    private ArrayList<DBCollection.User> friends = new ArrayList<>();
    private final Context context;
    private List<DBCollection.User> filteredFriendsList;

    public UserFriendsAdapter(Context context,ArrayList<DBCollection.User> friends) {
        this.friends = friends;
        this.filteredFriendsList = new ArrayList<>(friends);
        this.context = context;
    }

    @NonNull
    @Override
    public UserFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        return new UserFriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFriendsAdapter.ViewHolder holder, int position) {
        final int pos = position;
        int placeholderResourceId = R.drawable.image_not_found;
        Glide.with(context)
                .asBitmap()
                .load(filteredFriendsList.get(pos).getImage())
                .placeholder(placeholderResourceId)
                //.transform(new CircleCrop())
                .into(holder.profilePicImageView);

        holder.userNameTextView.setText(filteredFriendsList.get(pos).getName());
        holder.hearingPointsTextView.setText(String.valueOf(filteredFriendsList.get(pos).getHearingPoints()));
    }

    @Override
    public int getItemCount() {
        return filteredFriendsList.size();
    }

    public void updateData() {
        filteredFriendsList = friends;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                List<DBCollection.User> filtered = new ArrayList<>();

                for (DBCollection.User friend : friends) {
                    if (friend.getName().toLowerCase().contains(searchText)) {
                        filtered.add(friend);

                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredFriendsList = (List<DBCollection.User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userNameTextView,hearingPointsTextView;
        ImageView profilePicImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profilePicImageView = itemView.findViewById(R.id.profilePicCustom);
            this.userNameTextView = itemView.findViewById(R.id.usernameCustomTextView);
            this.hearingPointsTextView = itemView.findViewById(R.id.hearingPointsCustomTextView);
        }
    }
}
