package com.example.wannajoin.Managers;

import static com.example.wannajoin.Utilities.FBRef.refRooms;
import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    public static RoomManager instance;
    private DBCollection.Room currentRoom;
    private ArrayList<DBCollection.User> roomParticipants;
    private boolean isOwnedRoom = false;
    private boolean isInRoom = false;

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
    public void joinRoom(DBCollection.Room room,boolean isOwner)
    {
        currentRoom = room;
        isOwnedRoom = isOwner;
        isInRoom = true;
        roomParticipants = new ArrayList<DBCollection.User>();
        addParticipant(new OnParticipantAddedListener() {
            @Override
            public void onParticipantAdded() {
                getRoomParticipants();
            }
        });
    }

    public void leaveRoom()
    {
        currentRoom = null;
        isOwnedRoom = false;
        isInRoom = false;
    }

    public ArrayList<DBCollection.User> getCurrentRoomParticipants()
    {
        return roomParticipants;
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

    private void addSong(String roomId, String newSongId) {
        refRooms.child(roomId).child("playlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> songs = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String songId = snapshot.getValue(String.class);
                        songs.add(songId);
                    }
                }
                songs.add(newSongId);

                // Update the participants list in the database
                refRooms.child(roomId).child("playlist").setValue(songs)
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
                Log.e("TAG", "Error retrieving room's playlist", databaseError.toException());
            }
        });
    }

}
