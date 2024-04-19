package com.example.wannajoin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;

import java.util.ArrayList;
import java.util.List;

public class UserFollowersFollowingAdapter extends RecyclerView.Adapter<UserFollowersFollowingAdapter.ViewHolder> implements Filterable {

    private ArrayList<DBCollection.User> users = new ArrayList<>();
    private final Context context;
    private List<DBCollection.User> filteredUsersList;

    public UserFollowersFollowingAdapter(Context context, ArrayList<DBCollection.User> users) {
        this.users = users;
        this.filteredUsersList = new ArrayList<>(users);
        this.context = context;
    }

    @NonNull
    @Override
    public UserFollowersFollowingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, parent, false);
        return new UserFollowersFollowingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFollowersFollowingAdapter.ViewHolder holder, int position) {
        final int pos = position;
        DBCollection.User userToBuild = filteredUsersList.get(pos);
        int placeholderResourceId = R.drawable.image_not_found;
        Glide.with(context)
                .asBitmap()
                .load(userToBuild.getImage())
                .placeholder(placeholderResourceId)
                //.transform(new CircleCrop())
                .into(holder.profilePicImageView);

        holder.userNameTextView.setText(userToBuild.getName());
        holder.mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FollowerFollowingProfileActivity.class);
                intent.putExtra("UserInfo", userToBuild);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredUsersList.size();
    }

    public void updateData() {
        filteredUsersList = users;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase();
                List<DBCollection.User> filtered = new ArrayList<>();

                for (DBCollection.User following : users) {
                    if (following.getName().toLowerCase().contains(searchText)) {
                        filtered.add(following);

                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredUsersList = (List<DBCollection.User>) filterResults.values;
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
            this.mainContainer = itemView.findViewById(R.id.mainUserContainer);
            this.profilePicImageView = itemView.findViewById(R.id.profilePicCustom);
            this.userNameTextView = itemView.findViewById(R.id.usernameCustomTextView);
        }
    }
}
