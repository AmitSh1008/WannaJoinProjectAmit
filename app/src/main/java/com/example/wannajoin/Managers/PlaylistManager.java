package com.example.wannajoin.Managers;

import static com.example.wannajoin.Utilities.FBRef.refPlaylists;
import static com.example.wannajoin.Utilities.FBRef.refRooms;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TimerTask;

public class PlaylistManager {

    public static PlaylistManager instance;
    private Context context;
    private boolean isPlaying = false;

    public static Messenger musicService = null;
    public static ValueEventListener playingListener;

    public static synchronized PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
            PlaylistManager.getInstance().startListeningForPlayingUpdates();
            {

            }
        }
        return instance;
    }

    public void startListeningForPlayingUpdates()
    {
        playingListener = refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).child("playing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isPlaying = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public static void sendMessageToService(int what, Bundle b) {

        Message lMsg = Message.obtain(null, what);
        lMsg.setData(b);
        try {
            musicService.send(lMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addSongToCurrentRoom(String newSongId) {
        refPlaylists.child(RoomManager.getInstance().getCurrentRoom().getPlaylist()).child("songs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> songs = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String songId = snapshot.getValue(String.class);
                        songs.add(songId);
                    }
                }

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                songs.add(sdf.format(calendar.getTime()) + " " + newSongId);

                // Update the participants list in the database
                refPlaylists.child(RoomManager.getInstance().getCurrentRoom().getPlaylist()).child("songs").setValue(songs)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Song added successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding song", e);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error retrieving playlist", databaseError.toException());
            }
        });
    }

    public void startPlaylist()
    {
        if (RoomManager.getInstance().isInRoom())
        {
            if (RoomManager.getInstance().getCurrentRoomSongs().size() > 0)
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DBCollection.Room room = snapshot.getValue(DBCollection.Room.class);
                        room.setCurrentSongStartTime(sdf.format(calendar.getTime()));
                        room.setCurrentSongPausedTime(0);
                        room.setPlaying(true);
                        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).setValue(room);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(context, "No songs in playlist!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Not In Room!", Toast.LENGTH_SHORT).show();
        }

    }

    public void resumePlaylist()
    {
        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).child("playing").setValue(true);
    }

    public void pausePlaylist(int currentTime)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DBCollection.Room room = snapshot.getValue(DBCollection.Room.class);
                room.setCurrentSongPausedTime(currentTime);
                room.setPlaying(false);
                refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).setValue(room);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void nextSongInPlaylist()
    {
        if (RoomManager.getInstance().isInRoom())
        {
            if (RoomManager.getInstance().getCurrentRoomSongs().size() > 0)
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DBCollection.Room room = snapshot.getValue(DBCollection.Room.class);
                        room.setCurrentSongStartTime(sdf.format(calendar.getTime()));
                        room.setCurrentSong(RoomManager.getInstance().getNextSong(room.getCurrentSong().getId()));
                        room.setPlaying(true);
                        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).setValue(room);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(context, "No songs in playlist!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Not In Room!", Toast.LENGTH_SHORT).show();
        }
    }

    public void previousSongInPlaylist()
    {
        if (RoomManager.getInstance().isInRoom())
        {
            if (RoomManager.getInstance().getCurrentRoomSongs().size() > 0)
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DBCollection.Room room = snapshot.getValue(DBCollection.Room.class);
                        room.setCurrentSongStartTime(sdf.format(calendar.getTime()));
                        room.setCurrentSong(RoomManager.getInstance().getPreviousSong(room.getCurrentSong().getId()));
                        room.setPlaying(true);
                        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).setValue(room);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(context, "No songs in playlist!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Not In Room!", Toast.LENGTH_SHORT).show();
        }
    }

    public void jumpToSong(DBCollection.Song song)
    {
        if (RoomManager.getInstance().isInRoom())
        {
            if (RoomManager.getInstance().getCurrentRoomSongs().size() > 0)
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DBCollection.Room room = snapshot.getValue(DBCollection.Room.class);
                        room.setCurrentSongStartTime(sdf.format(calendar.getTime()));
                        room.setCurrentSong(song);
                        room.setPlaying(true);
                        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).setValue(room);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(context, "No songs in playlist!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Not In Room!", Toast.LENGTH_SHORT).show();
        }
    }


}
