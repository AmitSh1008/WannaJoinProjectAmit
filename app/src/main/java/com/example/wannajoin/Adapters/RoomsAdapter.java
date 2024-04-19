package com.example.wannajoin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wannajoin.Activities.InnerRoomActivity;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;

import java.util.ArrayList;
import java.util.List;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.ViewHolder> implements Filterable {

    private ArrayList<DBCollection.Room> rooms = new ArrayList<>();
    private final Context context;
    private List<DBCollection.Room> filteredRoomsList;

    public RoomsAdapter(Context context, ArrayList<DBCollection.Room> rooms) {
        this.rooms = rooms;
        this.filteredRoomsList = new ArrayList<>(rooms);
        this.context = context;
    }

    @NonNull
    @Override
    public RoomsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_room_item, parent, false);
        return new RoomsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsAdapter.ViewHolder holder, int position) {
        final int pos = position;
        holder.groupName.setText(filteredRoomsList.get(pos).getName());
        holder.groupOwner.setText(String.valueOf(filteredRoomsList.get(pos).getOwner()));
        if (filteredRoomsList.get(pos).getParticipants() == null)
        {
            holder.groupParticipants.setText("0 / " + String.valueOf(filteredRoomsList.get(pos).getMaxParts()) + " Participants");
        }
        else if (filteredRoomsList.get(pos).getParticipants().size() >= 0 && filteredRoomsList.get(pos).getParticipants().size() < filteredRoomsList.get(pos).getMaxParts())
        {
            holder.groupParticipants.setText(String.valueOf(filteredRoomsList.get(pos).getParticipants().size()) + " / " + String.valueOf(filteredRoomsList.get(pos).getMaxParts()) + " Participants");
        }
        else {
            holder.groupParticipants.setText("Full");
        }

        holder.roomItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filteredRoomsList.get(pos).getParticipants().size() == filteredRoomsList.get(pos).getMaxParts())
                {
                    Toast.makeText(context, "Room is full!", Toast.LENGTH_SHORT).show();
                }
                else if (RoomManager.getInstance().isInRoom())
                {
                    Toast.makeText(context, "Already In Room!", Toast.LENGTH_SHORT).show();
                }
                else {
                    RoomManager.getInstance().joinRoom(filteredRoomsList.get(pos), false);
                    context.startActivity(new Intent(context, InnerRoomActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredRoomsList.size();
    }

    public void updateData() {
        filteredRoomsList = rooms;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //TODO IF NEEDED
//                String searchText = charSequence.toString().toLowerCase();
//                List<DBCollection.User> filtered = new ArrayList<>();
//
//                for (DBCollection.User following : users) {
//                    if (following.getName().toLowerCase().contains(searchText)) {
//                        filtered.add(following);
//
//                    }
//                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredRoomsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredRoomsList = (List<DBCollection.Room>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout roomItemContainer;
        TextView groupName,groupOwner, groupParticipants;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.roomItemContainer = itemView.findViewById(R.id.roomItemContainer);
            this.groupName = itemView.findViewById(R.id.groupNameTextView);
            this.groupOwner = itemView.findViewById(R.id.groupOwnerTextView);
            this.groupParticipants = itemView.findViewById(R.id.groupParticipantsTextView);
        }
    }

}
