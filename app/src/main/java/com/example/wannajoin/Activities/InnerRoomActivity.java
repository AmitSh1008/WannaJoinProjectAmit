package com.example.wannajoin.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wannajoin.Adapters.SearchForSongAdapter;
import com.example.wannajoin.Adapters.SearchForUserAdapter;
import com.example.wannajoin.Adapters.SearchSongAdapter;
import com.example.wannajoin.Adapters.SongInPlaylistAdapter;
import com.example.wannajoin.Adapters.UserFollowersFollowingAdapter;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class InnerRoomActivity extends AppCompatActivity {

    private TextView tvRoomName, tvParticipantsTitle,tvPlaylistTitle;
    private RecyclerView lvParticipants, lvPlaylist;
    private UserFollowersFollowingAdapter participantsAdapter;
    private SongInPlaylistAdapter playlistAdapter;
    private AutoCompleteTextView actvSearchForSong;
    private ImageView playingNowRedirect;

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
        playingNowRedirect = findViewById(R.id.playingNowIcon);

        tvRoomName.setText(RoomManager.getInstance().getCurrentRoom().getName());
        participantsAdapter = new UserFollowersFollowingAdapter(getApplicationContext(), RoomManager.getInstance().getCurrentRoomParticipants());
        LinearLayoutManager participantsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvParticipants.setLayoutManager(participantsLayoutManager);
        lvParticipants.setAdapter(participantsAdapter);
        tvParticipantsTitle.setText(createParticipantsCount());

        SearchSongAdapter searchForSongAdapter = new SearchSongAdapter(this, actvSearchForSong);
        actvSearchForSong.setAdapter(searchForSongAdapter);

        actvSearchForSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBCollection.Song selectedSong = (DBCollection.Song) parent.getItemAtPosition(position);
                actvSearchForSong.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(actvSearchForSong.getWindowToken(), 0);
                RoomManager.getInstance().addSongToCurrentRoom(selectedSong.getId());
            }
        });


        playlistAdapter = new SongInPlaylistAdapter(getApplicationContext(), RoomManager.getInstance().getCurrentRoomSongs());
        LinearLayoutManager playlistLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lvPlaylist.setLayoutManager(playlistLayoutManager);
        lvPlaylist.setAdapter(playlistAdapter);
        tvPlaylistTitle.setText(createPlaylistCount());

        playingNowRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlayingNowActivity.class));
            }
        });

    }

    public String createParticipantsCount()
    {
        return "Participants: ( "
                + String.valueOf(RoomManager.getInstance().getCurrentRoomParticipants().size())
                + " / "
                + String.valueOf(RoomManager.getInstance().getCurrentRoom().getMaxParts())
                + " )";
    }

    public String createPlaylistCount()
    {
        return "Playlist: ( "
                + String.valueOf(RoomManager.getInstance().getCurrentRoomSongs().size())
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
            else {
                tvPlaylistTitle.setText(createPlaylistCount());
                playlistAdapter.updateData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}