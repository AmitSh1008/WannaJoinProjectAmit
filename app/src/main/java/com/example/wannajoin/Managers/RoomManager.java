package com.example.wannajoin.Managers;

import static com.example.wannajoin.Utilities.FBRef.refPlaylists;
import static com.example.wannajoin.Utilities.FBRef.refRooms;
import static com.example.wannajoin.Utilities.FBRef.refSongs;
import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wannajoin.Utilities.Constants;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class RoomManager {

    public static RoomManager instance;
    private DBCollection.Room currentRoom;
    private ArrayList<DBCollection.User> roomParticipants;
    private ArrayList<DBCollection.Song> roomSongs;
    private boolean isOwnedRoom = false;
    private boolean isInRoom = false;

    private Messenger musicService = null;

    private interface OnParticipantAddedListener {
        void onParticipantAdded();
    }


    public static synchronized RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

    public DBCollection.Room getCurrentRoom() {
        return currentRoom;
    }

    public void setMusicService(Messenger musicService) {
        this.musicService = musicService;
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

    public void joinRoom(DBCollection.Room room, boolean isOwner)
    {
        currentRoom = room;
        isOwnedRoom = isOwner;
        isInRoom = true;
        roomParticipants = new ArrayList<DBCollection.User>();
        roomSongs = new ArrayList<DBCollection.Song>();
        addParticipant(new OnParticipantAddedListener() {
            @Override
            public void onParticipantAdded() {
                getRoomParticipants();
                getRoomSongs();
                EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(true));
                Bundle bundle = new Bundle();
                bundle.putString("RoomID", currentRoom.getId());
                sendMessageToService(Constants.MESSANGER.START_LISTENING_TO_ROOM, bundle);
            }
        });
    }

    public void leaveRoom()
    {
        currentRoom = null;
        isOwnedRoom = false;
        isInRoom = false;
        EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(false));
    }

    public ArrayList<DBCollection.User> getCurrentRoomParticipants()
    {
        return roomParticipants;
    }

    public ArrayList<DBCollection.Song> getCurrentRoomSongs()
    {
        return roomSongs;
    }

    public boolean isOwnedRoom() {
        return isOwnedRoom;
    }

    public void setOwnedRoom(boolean ownedRoom) {
        isOwnedRoom = ownedRoom;
    }

    public boolean isInRoom() {
        return isInRoom;
    }

    public void setInRoom(boolean inRoom) {
        isInRoom = inRoom;
    }

    private void getRoomParticipants()
    {
        refRooms.child(currentRoom.getId()).child("participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomParticipants.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        fillRoomParticipants(snapshot.getChildrenCount(), userSnapshot.getValue(String.class), () -> {
                            // This callback runs when all followers have been added
                            EventBus.getDefault().post(new EventMessages.ParticipantsChangedInRoom(true));
                        });
                    }
                } else {
                    EventBus.getDefault().post(new EventMessages.ParticipantsChangedInRoom(true));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRoomSongs()
    {
        refPlaylists.child(currentRoom.getPlaylist()).child("songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomSongs.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                        fillRoomSongs(snapshot.getChildrenCount(), songSnapshot.getValue(String.class), () -> {
                            // This callback runs when all followers have been added
                            EventBus.getDefault().post(new EventMessages.ParticipantsChangedInRoom(false));
                        });
                    }
                } else {
                    EventBus.getDefault().post(new EventMessages.ParticipantsChangedInRoom(false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fillRoomParticipants(long stopCount, String id, Runnable callback) {
        refUsers.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DBCollection.User userFound = snapshot.getValue(DBCollection.User.class);
                    roomParticipants.add(userFound);
                    if (callback != null && stopCount == roomParticipants.size()) {
                        callback.run();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    public void fillRoomSongs(long stopCount, String songInfo, Runnable callback) {
        String[] songInfoArray = songInfo.split(" ");
        String songId = songInfoArray[2].trim();
        refSongs.child(songId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DBCollection.Song songFound = snapshot.getValue(DBCollection.Song.class);
                roomSongs.add(songFound);
                if (callback != null && stopCount == roomSongs.size()) {
                    callback.run();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


    private void addParticipant(OnParticipantAddedListener listener) {
        refRooms.child(currentRoom.getId()).child("participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get current participants list
                List<String> participants = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String participantId = snapshot.getValue(String.class);
                        participants.add(participantId);
                    }
                }

                // Add new participant ID
                participants.add(LoggedUserManager.getInstance().getLoggedInUser().getUserId());

                // Update the participants list in the database
                refRooms.child(currentRoom.getId()).child("participants").setValue(participants)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Participant added successfully");
                                if (listener != null) {
                                    listener.onParticipantAdded();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding participant", e);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error retrieving participants", databaseError.toException());
            }
        });
    }

    public void addSongToCurrentRoom(String newSongId) {
        refPlaylists.child(currentRoom.getPlaylist()).child("songs").addListenerForSingleValueEvent(new ValueEventListener() {
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
                refPlaylists.child(currentRoom.getPlaylist()).child("songs").setValue(songs)
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

    public DBCollection.Song getNextSong(String id) {
        if (Objects.equals(roomSongs.get(roomSongs.size() - 1).getId(), id))
        {
            return roomSongs.get(0);
        }
        for (int song = 0; song < roomSongs.size() - 1; song++)
        {

            if (Objects.equals(roomSongs.get(song).getId(), id))
            {
                return roomSongs.get(song + 1);
            }
        }
        return null;
    }

    public DBCollection.Song getPreviousSong(String id) {
        if (Objects.equals(roomSongs.get(0).getId(), id))
        {
            return roomSongs.get(roomSongs.size() - 1);
        }
        for (int song = 1; song < roomSongs.size(); song++)
        {

            if (Objects.equals(roomSongs.get(song).getId(), id))
            {
                return roomSongs.get(song - 1);
            }
        }
        return null;
    }


}
