package com.example.wannajoin.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wannajoin.Adapters.UserFollowersFollowingAdapter;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.EventMessages;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

public class InnerRoomActivity extends AppCompatActivity {

    private TextView tvRoomName, tvParticipantsTitle,tvPlaylistTitle;
    private RecyclerView lvParticipants, lvPlaylist;
    private UserFollowersFollowingAdapter participantsAdapter;
    private AutoCompleteTextView actvSearchForSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner_room);
        EventBus.getDefault().register(this);

        tvRoomName = findViewById(R.id.tvRoomName);
        tvParticipantsTitle = findViewById(R.id.tvParticipantsTitle);
        lvParticipants = findViewById(R.id.lvRoomParticipants);
        tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle);
        lvPlaylist = findViewById(R.id.lvRoomPlaylist);
        actvSearchForSong = findViewById(R.id.searchForSongPlaylist);

        tvRoomName.setText(RoomManager.getInstance().getCurrentRoom().getName());
        participantsAdapter = new UserFollowersFollowingAdapter(getApplicationContext(), RoomManager.getInstance().getCurrentRoomParticipants());
        LinearLayoutManager participantsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvParticipants.setLayoutManager(participantsLayoutManager);
        lvParticipants.setAdapter(participantsAdapter);
        tvParticipantsTitle.setText(createParticipantsCount());

    }

    public String createParticipantsCount()
    {
        return "Participants: ( "
                + String.valueOf(RoomManager.getInstance().getCurrentRoomParticipants().size())
                + " / "
                + String.valueOf(RoomManager.getInstance().getCurrentRoom().getMaxParts())
                + " )";
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.ParticipantsChangedInRoom event) {
        try{
            if (event.isParticipants())
            {
                tvParticipantsTitle.setText(createParticipantsCount());
                participantsAdapter.updateData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}