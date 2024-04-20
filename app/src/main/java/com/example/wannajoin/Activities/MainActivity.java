package com.example.wannajoin.Activities;


import static com.example.wannajoin.Utilities.FBRef.refGenres;
import static com.example.wannajoin.Utilities.FBRef.refSingers;
import static com.example.wannajoin.Utilities.FBRef.refSongs;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wannajoin.Adapters.GenresRecyclerViewAdapter;
import com.example.wannajoin.Adapters.SingersRecyclerViewAdapter;
import com.example.wannajoin.Adapters.SongsRecyclerViewAdapter;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.Constants;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.example.wannajoin.Utilities.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Service
    private Messenger musicService = null;
    private boolean bound;
    private Messenger activityMessenger;

    //Search
    private RelativeLayout layoutCategories;
    private RelativeLayout layoutAllSongs;
    private EditText searchBar;

    //Libraries

    private TextView libraryTitle;
    private Map<String, ArrayList<DBCollection.Song>> singersMap;
    private Map<String, ArrayList<DBCollection.Song>> genresMap;

    private RecyclerView gridAllSongs;
    private SongsRecyclerViewAdapter gridAllSongsAdapter;


    //Categories
    private RecyclerView recentRecyclerView;
    private RecyclerView recommendedRecyclerView;
    private RecyclerView singersRecyclerView;
    private RecyclerView genresRecyclerView;
    private SongsRecyclerViewAdapter recentAdapter;
    private SongsRecyclerViewAdapter recommendedAdapter;
    private SingersRecyclerViewAdapter singersAdapter;
    private GenresRecyclerViewAdapter genresAdapter;
    private ArrayList<DBCollection.Song> recentSongs;
    private ArrayList<DBCollection.Song> allSongs;
    private ArrayList<DBCollection.Singer> allSingers;
    private ArrayList<DBCollection.Genre> allGenres;

    //Bottom Menu Bar
    private ImageView playingNowRedirect, groupsRedirect, leaderBoardRedirect, profileSettingsRedirect;

    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            musicService = new Messenger(service);
            RoomManager.getInstance().setMusicService(musicService);
            bound = true;

            Bundle bundle = new Bundle();
            bundle.putString("Message","Cool, all set up!");
            sendMessageToService(Constants.MESSANGER.TO_SERVICE_HELLO, bundle);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            musicService = null;
            bound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                layoutCategories.setVisibility(View.VISIBLE);
                layoutAllSongs.setVisibility(View.GONE);
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);


        groupsRedirect = findViewById(R.id.groups_icon);
        playingNowRedirect = findViewById(R.id.play_now_icon);
        leaderBoardRedirect = findViewById(R.id.leaderboard_icon);
        profileSettingsRedirect = findViewById(R.id.settings_icon);

        groupsRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RoomManager.getInstance().isInRoom())
                {
                    Intent intent = new Intent(getApplicationContext(), InnerRoomActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), RoomsActivity.class);
                    startActivity(intent);
                }

            }
        });

        profileSettingsRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                //intent.putExtra("userConnected", connectedUser);
                startActivity(intent);
            }
        });





        activityMessenger = new Messenger(new IncomingHandler());

        Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
        serviceIntent.setAction(Constants.ACTION.SERVICE_BUILD_ACTION);
        serviceIntent.putExtra("Messenger", activityMessenger);
        startService(serviceIntent);
        bindService(new Intent(getApplicationContext(), MusicService.class), connection,
                Context.BIND_AUTO_CREATE);

        layoutCategories = findViewById(R.id.layoutCategories);
        layoutAllSongs = findViewById(R.id.layoutAllSongs);
        searchBar = findViewById(R.id.search_edit_text);
        libraryTitle = findViewById(R.id.libraryTitleTextview);


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layoutCategories.setVisibility(View.GONE);
                layoutAllSongs.setVisibility(View.VISIBLE);

                gridAllSongs = findViewById(R.id.gridSongsList);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);
                gridAllSongs.setLayoutManager(gridLayoutManager);
                gridAllSongsAdapter = new SongsRecyclerViewAdapter(getApplicationContext(), allSongs);
                gridAllSongs.setAdapter(gridAllSongsAdapter);
                gridAllSongsAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });

        refSongs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                singersMap = new HashMap<>();
                genresMap = new HashMap<>();

                // Iterate through each child node under "Songs"
                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {

                    DBCollection.Song song = songSnapshot.getValue(DBCollection.Song.class);
                    // Check if the singer already exists in the map
                    if (singersMap.containsKey(song.getSinger())) {
                        // If the singer exists, add the song to the existing ArrayList
                        ArrayList<DBCollection.Song> songsList = singersMap.get(song.getSinger());
                        songsList.add(song);
                    } else {
                        // If the singer doesn't exist, create a new ArrayList and add the song
                        ArrayList<DBCollection.Song> songsList = new ArrayList<>();
                        songsList.add(song);
                        singersMap.put(song.getSinger(), songsList);
                    }
                    if (genresMap.containsKey(song.getGenre())) {
                        ArrayList<DBCollection.Song> songsList = genresMap.get(song.getGenre());
                        songsList.add(song);
                    } else {
                        ArrayList<DBCollection.Song> songsList = new ArrayList<>();
                        songsList.add(song);
                        genresMap.put(song.getGenre(), songsList);
                    }
                }
                // Now, singersMap contains keys as singers and values as ArrayLists of their songs
                // You can use this map as needed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });

        buildLists();


    }

    private void buildLists()
    {
        recentRecyclerView = findViewById(R.id.recently_list);
        recentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager recentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recentRecyclerView.setLayoutManager(recentLayoutManager);
        recentAdapter = new SongsRecyclerViewAdapter(getApplicationContext(), LoggedUserManager.getInstance().getUserRecents());
        recentRecyclerView.setAdapter(recentAdapter);
        recentAdapter.notifyDataSetChanged();


        recommendedRecyclerView = findViewById(R.id.recommended_list);
        recommendedRecyclerView.setHasFixedSize(true);
        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(recommendedLayoutManager);

        allSongs = new ArrayList<>();
        refSongs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DBCollection.Song song = dataSnapshot.getValue(DBCollection.Song.class);
                    allSongs.add(song);
                }
                recommendedAdapter = new SongsRecyclerViewAdapter(getApplicationContext(),allSongs);
                recommendedRecyclerView.setAdapter(recommendedAdapter);
                recommendedAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        singersRecyclerView = findViewById(R.id.singers_list);
        singersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager singersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        singersRecyclerView.setLayoutManager(singersLayoutManager);

        allSingers = new ArrayList<>();
        refSingers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DBCollection.Singer singer = dataSnapshot.getValue(DBCollection.Singer.class);
                    allSingers.add(singer);
                }
                singersAdapter = new SingersRecyclerViewAdapter(getApplicationContext(),allSingers);
                singersRecyclerView.setAdapter(singersAdapter);
                singersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        genresRecyclerView = findViewById(R.id.genres_list);
        genresRecyclerView.setHasFixedSize(true);
        LinearLayoutManager genresLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        genresRecyclerView.setLayoutManager(genresLayoutManager);

        allGenres = new ArrayList<>();
        refGenres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DBCollection.Genre genre = dataSnapshot.getValue(DBCollection.Genre.class);
                    allGenres.add(genre);
                }
                genresAdapter = new GenresRecyclerViewAdapter(getApplicationContext(),allGenres);
                genresRecyclerView.setAdapter(genresAdapter);
                genresAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSANGER.FROM_SERVICE_HELLO:
                    String message = msg.getData().getString("Message");
                    Toast.makeText(getApplicationContext(), "Service said: " + message, Toast.LENGTH_SHORT).show();
                default:
            }
        }
    }

    private void sendMessageToService(int what, Bundle b) {

        Message lMsg = Message.obtain(null, what);
        lMsg.setData(b);
        try {
            musicService.send(lMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.PlaySongEvent event) {
        DBCollection.Song song = event.getSong();
        try{
            Toast.makeText(getApplicationContext(), song.getName() + " - " + song.getSinger(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("ID", song.getId());
            bundle.putString("NAME", song.getName());
            bundle.putString("SINGER", song.getSinger());
            bundle.putInt("YEAR", song.getYear());
            bundle.putString("DURATION", song.getDuration());
            bundle.putString("IMAGE", song.getImage());
            bundle.putString("LINK", song.getLink());
            sendMessageToService(Constants.MESSANGER.TO_SERVICE_PLAY_SONG, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.SingerLibraryEvent event) {
        DBCollection.Singer singer = event.getSinger();
        try{
            Toast.makeText(getApplicationContext(), singer.getName() + "'s Songs", Toast.LENGTH_SHORT).show();
            layoutCategories.setVisibility(View.GONE);
            layoutAllSongs.setVisibility(View.VISIBLE);
            libraryTitle.setText(singer.getName() + " : " + "(" + String.valueOf(singersMap.get(singer.getName()).size())+ ")");

            gridAllSongs = findViewById(R.id.gridSongsList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);
            gridAllSongs.setLayoutManager(gridLayoutManager);
            gridAllSongsAdapter = new SongsRecyclerViewAdapter(getApplicationContext(), singersMap.get(singer.getName()));
            gridAllSongs.setAdapter(gridAllSongsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.GenreLibraryEvent event) {
        DBCollection.Genre genre = event.getGenre();
        try{
            Toast.makeText(getApplicationContext(), genre.getName() + "'s Songs", Toast.LENGTH_SHORT).show();
            layoutCategories.setVisibility(View.GONE);
            layoutAllSongs.setVisibility(View.VISIBLE);
            libraryTitle.setText(genre.getName() + " : " + "(" + String.valueOf(genresMap.get(genre.getName()).size())+ ")");

            gridAllSongs = findViewById(R.id.gridSongsList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.VERTICAL, false);
            gridAllSongs.setLayoutManager(gridLayoutManager);
            gridAllSongsAdapter = new SongsRecyclerViewAdapter(getApplicationContext(), genresMap.get(genre.getName()));
            gridAllSongs.setAdapter(gridAllSongsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.LibraryResultChangedEvent event) {
        int newSize = event.getSize();
        try{
            libraryTitle.setText("Results : " + "(" + newSize + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int convertDurationToSeconds(String duration) {
        try {
            Log.d("duration", duration);
            String[] timeComponents = duration.split(":");
            int minutes = Integer.parseInt(timeComponents[0].trim());
            int seconds = Integer.parseInt(timeComponents[1].trim());
            return (minutes * 60) + seconds;
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Print the exception details
            return 0; // Return 0 for any parsing issues
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.RecentAdded event) {
        try{
            recentAdapter.updateData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}