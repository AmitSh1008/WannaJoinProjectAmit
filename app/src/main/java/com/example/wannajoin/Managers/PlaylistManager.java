package com.example.wannajoin.Managers;

import static com.example.wannajoin.Utilities.FBRef.refRooms;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.wannajoin.Utilities.DBCollection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PlaylistManager {

    public static PlaylistManager instance;
    private Context context;
    private boolean isPlaying = false;

    public static synchronized PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
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
                        room.setCurrentSong(RoomManager.getInstance().getCurrentRoomSongs().get(0));
                        room.setPlaying(true);
                        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).setValue(room);
                        isPlaying = true;
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
        isPlaying = true;
    }

    public void pausePlaylist()
    {
        refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).child("playing").setValue(false);
        isPlaying = false;
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
                        isPlaying = true;
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
                        isPlaying = true;
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
                        isPlaying = true;
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
