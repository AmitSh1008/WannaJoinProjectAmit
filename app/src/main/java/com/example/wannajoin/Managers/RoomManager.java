package com.example.wannajoin.Managers;

import static com.example.wannajoin.Utilities.FBRef.refPlaylists;
import static com.example.wannajoin.Utilities.FBRef.refRooms;
import static com.example.wannajoin.Utilities.FBRef.refSongs;
import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wannajoin.Utilities.Constants;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomManager {

    public static RoomManager instance;
    private DBCollection.Room currentRoom;
    private ArrayList<DBCollection.User> roomParticipants = new ArrayList<DBCollection.User>();
    private ArrayList<DBCollection.Song> roomSongs = new ArrayList<DBCollection.Song>();
    private boolean isOwnedRoom = false;
    private boolean isInRoom = false;

    private ValueEventListener roomListener;
    private ValueEventListener songsListener;


    private interface OnParticipantStateChangedListener {
        void onParticipantsChanged();
    }
    private interface OnRoomUpdateStartFinishedListener {
        void onRoomUpdateStartFinishedReceived();
    }

    private interface OnRoomSongsReceivedListener {
        void onRoomSongsReceived();
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

    public void joinRoom(DBCollection.Room room, boolean isOwner)
    {
        currentRoom = room;
        changeParticipantState(new OnParticipantStateChangedListener() {
            @Override
            public void onParticipantsChanged() {
                startListeningForRoomUpdates(new OnRoomUpdateStartFinishedListener() {
                    @Override
                    public void onRoomUpdateStartFinishedReceived() {
                        startListeningForSongsUpdates(new OnRoomSongsReceivedListener() {
                            @Override
                            public void onRoomSongsReceived() {
                                isOwnedRoom = isOwner;
                                isInRoom = true;
                                Bundle bundle = new Bundle();
                                bundle.putString("RoomID", currentRoom.getId());
                                PlaylistManager.sendMessageToService(Constants.MESSANGER.START_LISTENING_TO_ROOM, bundle);
                            }
                        });
                    }
                });
            }
        }, true);

//        changeParticipantState(new OnParticipantStateChangedListener() {
//            @Override
//            public void onParticipantsChanged() {
//                Log.d("checkForBug", "onParticipantsChanged");
//                getRoomParticipants(new OnRoomParticipantsReceivedListener() {
//                    @Override
//                    public void onRoomParticipantsReceived() {
//                        EventBus.getDefault().post(new EventMessages.DataChangedInRoom(true));
//                        EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(true));
//                        getRoomSongs(new OnRoomSongsReceivedListener() {
//                            @Override
//                            public void onRoomSongsReceived() {
//                                EventBus.getDefault().post(new EventMessages.DataChangedInRoom(false));
//                                Bundle bundle = new Bundle();
//                                bundle.putString("RoomID", currentRoom.getId());
//                                PlaylistManager.sendMessageToService(Constants.MESSANGER.START_LISTENING_TO_ROOM, bundle);
//                            }
//                        });
//                    }
//                });
//            }
////                getRoomParticipants();
////                getRoomSongs();
////                EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(true));
//
//        }, true);
    }

    private void startListeningForRoomUpdates(final OnRoomUpdateStartFinishedListener listener) {
        roomListener = refRooms.child(currentRoom.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DBCollection.Room oldRoom = currentRoom;
                currentRoom = snapshot.getValue(DBCollection.Room.class);
                if (currentRoom.getParticipants() != null && currentRoom.getParticipants().size() != oldRoom.getParticipants().size()) {
                    roomParticipants.clear();
                    int expectedCallbacks = currentRoom.getParticipants().size();
                    AtomicInteger receivedCallbacks = new AtomicInteger(0);
                    for (String userId : currentRoom.getParticipants()) {
                        refUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                DBCollection.User user = userSnapshot.getValue(DBCollection.User.class);
                                if (user != null) {
                                    roomParticipants.add(user);
                                }
                                receivedCallbacks.incrementAndGet();
                                if (receivedCallbacks.get() == expectedCallbacks) {
                                    EventBus.getDefault().post(new EventMessages.DataChangedInRoom(true));
                                    EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(true));
                                    if (!isInRoom)
                                    {
                                        listener.onRoomUpdateStartFinishedReceived();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                receivedCallbacks.incrementAndGet();
                                if (receivedCallbacks.get() == expectedCallbacks) {
                                    EventBus.getDefault().post(new EventMessages.DataChangedInRoom(true));
                                    EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(true));
                                    if (!isInRoom)
                                    {
                                        listener.onRoomUpdateStartFinishedReceived();
                                    }
                                }
                            }
                        });
                    }
                }
                else if (currentRoom.getParticipants() == null && roomParticipants.size() != 0)
                {
                    roomParticipants.clear();
                    EventBus.getDefault().post(new EventMessages.DataChangedInRoom(true));
                    EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(true));
                    if (!isInRoom)
                    {
                        listener.onRoomUpdateStartFinishedReceived();
                    }
                }
                else if ((currentRoom.getCurrentSong() != null && oldRoom.getCurrentSong() == null)  || (currentRoom.getCurrentSongStartTime() != null && oldRoom.getCurrentSongStartTime() == null) || (currentRoom.getCurrentSongStartTime() == null && oldRoom.getCurrentSongStartTime() != null) || (currentRoom.getCurrentSongStartTime() != null && !currentRoom.getCurrentSongStartTime().equals(oldRoom.getCurrentSongStartTime())))
                {
                    EventBus.getDefault().post(new EventMessages.DataChangedInRoom(false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void startListeningForSongsUpdates(final OnRoomSongsReceivedListener listener) {
        songsListener = refPlaylists.child(currentRoom.getPlaylist()).child("songs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    roomSongs.clear();
                    int expectedCallbacks = (int) snapshot.getChildrenCount();
                    AtomicInteger receivedCallbacks = new AtomicInteger(0);
                    for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                            String songInfo = songSnapshot.getValue(String.class);
                            String[] songInfoArray = songInfo.split(" ");
                            String songId = songInfoArray[2].trim();
                            refSongs.child(songId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot songSnapshot) {
                                    DBCollection.Song song = songSnapshot.getValue(DBCollection.Song.class);
                                    if (song != null) {
                                        roomSongs.add(song);
                                    }
                                    receivedCallbacks.incrementAndGet();
                                    if (receivedCallbacks.get() == expectedCallbacks) {
                                        if (roomSongs.size() == 1)
                                        {
                                            refRooms.child(currentRoom.getId()).child("currentSong").setValue(roomSongs.get(0));
                                        }
                                        EventBus.getDefault().post(new EventMessages.DataChangedInRoom(false));
                                        if (!isInRoom)
                                        {
                                            listener.onRoomSongsReceived();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    receivedCallbacks.incrementAndGet();
                                    if (receivedCallbacks.get() == expectedCallbacks) {
                                        EventBus.getDefault().post(new EventMessages.DataChangedInRoom(false));
                                        if (!isInRoom)
                                        {
                                            listener.onRoomSongsReceived();
                                        }
                                    }
                                }
                            });
                    }
                }
                else {
                    roomSongs.clear();
                    EventBus.getDefault().post(new EventMessages.DataChangedInRoom(false));
                    if (!isInRoom)
                    {
                        listener.onRoomSongsReceived();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    public void leaveRoom()
    {
        refRooms.child(currentRoom.getId()).removeEventListener(roomListener);
//        refRooms.child(currentRoom.getId()).child("participants").removeEventListener(roomParticipantsListener);
        refPlaylists.child(currentRoom.getPlaylist()).child("songs").removeEventListener(songsListener);
        changeParticipantState(new OnParticipantStateChangedListener() {
            @Override
            public void onParticipantsChanged() {
                currentRoom = null;
                isInRoom = false;
                roomParticipants.clear();
                roomSongs.clear();
                EventBus.getDefault().post(new EventMessages.UserInRoomStateChanged(false));
            }
        }, false);
    }

    public void leaveRoomAndJoinAnother(DBCollection.Room room,boolean isOwner)
    {
        refRooms.child(currentRoom.getId()).removeEventListener(roomListener);
//        refRooms.child(currentRoom.getId()).child("participants").removeEventListener(roomParticipantsListener);
        refPlaylists.child(currentRoom.getPlaylist()).child("songs").removeEventListener(songsListener);
        changeParticipantState(new OnParticipantStateChangedListener() {
            @Override
            public void onParticipantsChanged() {
                currentRoom = null;
                isInRoom = false;
                joinRoom(room, isOwner);
            }
        }, false);
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

//    private void getRoomParticipants(OnRoomUpdateStartFinishedListener listener)
//    {
//        roomParticipantsListener =  refRooms.child(currentRoom.getId()).child("participants").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                roomParticipants.clear();
//                if (snapshot.getChildrenCount() != 0) {
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        fillRoomParticipants(snapshot.getChildrenCount(), userSnapshot.getValue(String.class), () -> {
//                            if (listener != null) {
//                                //listener.onRoomParticipantsReceived();
//                            }
//                        });
//                    }
//                } else {
//                    if (listener != null) {
//                        //listener.onRoomParticipantsReceived();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void getRoomSongs(OnRoomSongsReceivedListener listener)
//    {
//        roomSongsListener = refPlaylists.child(currentRoom.getPlaylist()).child("songs").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                roomSongs.clear();
//                if (snapshot.getChildrenCount() != 0) {
//                    for (DataSnapshot songSnapshot : snapshot.getChildren()) {
//                        fillRoomSongs(snapshot.getChildrenCount(), songSnapshot.getValue(String.class), () -> {
//                            if (listener != null) {
//                                listener.onRoomSongsReceived();
//                            }
//                        });
//                    }
//                } else {
//                    if (listener != null) {
//                        listener.onRoomSongsReceived();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void fillRoomParticipants(long stopCount, String id, Runnable callback) {
        refUsers.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DBCollection.User userFound = snapshot.getValue(DBCollection.User.class);
                    roomParticipants.add(userFound);
                    Log.d("checkForBug", "ParticipantAdded");
                    if (callback != null && stopCount == roomParticipants.size()) {
                        Log.d("checkForBug", "finishAdding1");
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
                Log.d("checkForBug", "SongAdded");
                if (callback != null && stopCount == roomSongs.size()) {
                    Log.d("checkForBug", "finishAdding2");
                    callback.run();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }


    private void changeParticipantState(OnParticipantStateChangedListener listener,boolean toAdd) {

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
                if (toAdd) {
                    participants.add(LoggedUserManager.getInstance().getLoggedInUser().getUserId());
                } else {
                    participants.remove(LoggedUserManager.getInstance().getLoggedInUser().getUserId());
                }

                // Update the participants list in the database
                refRooms.child(currentRoom.getId()).child("participants").setValue(participants)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Participant state changed successfully");
                                if (listener != null) {
                                    listener.onParticipantsChanged();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error changing state for participant", e);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Error retrieving participants", databaseError.toException());
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
