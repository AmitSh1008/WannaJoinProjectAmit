package com.example.wannajoin.Adapters;


import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchForUserAdapter extends ArrayAdapter<DBCollection.User> implements Filterable {
    private List<DBCollection.User> filteredList;
    private LayoutInflater inflater;

    public SearchForUserAdapter(Context context, List<DBCollection.User> originalList) {
        super(context, R.layout.layout_imagename_vertical_item);
        if (originalList.size() > 0)
        {
            this.filteredList = new ArrayList<>(originalList);
        }
        else {
            this.filteredList = new ArrayList<>();
        }
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public DBCollection.User getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_imagename_vertical_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            // Add references to other views in your custom layout here
            convertView.setTag(viewHolder);
            DBCollection.User user = filteredList.get(position);
            if (user != null) {
                viewHolder.userNameTextView.setText(user.getName());
                Glide.with(getContext())
                        .asBitmap()
                        .load(user.getImage())
                        .placeholder(R.drawable.default_profile_picture)
                        .into(viewHolder.profilePicImageView);

            }
        }
        return convertView;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userNameTextView;
        ImageView profilePicImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profilePicImageView = itemView.findViewById(R.id.profilePicCustom);
            this.userNameTextView = itemView.findViewById(R.id.usernameCustomTextView);
        }
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<DBCollection.User> filtered = new ArrayList<>();
                if (!constraint.toString().isEmpty())
                {
                    refUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String username = dataSnapshot.child("name").getValue(String.class);
                                DBCollection.User user = dataSnapshot.getValue(DBCollection.User.class);
                                    if ((!Objects.equals(user.getUserId(), LoggedUserManager.getInstance().getLoggedInUser().getUserId())) && (!LoggedUserManager.getInstance().isUserAlreadyFollowed(user.getUserId())) && (user.getName().toLowerCase().contains(constraint.toString().toLowerCase()))) {
                                        filtered.add(user);
                                }
                            }


                            filteredList.clear();
                            if (filtered != null && filtered.size() > 0) {
                                filteredList.addAll(filtered);
                                notifyDataSetChanged();
                            } else {
                                notifyDataSetInvalidated();
                            }

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
}


