package com.example.wannajoin;

import static com.example.wannajoin.FBRef.refSongs;
import static com.example.wannajoin.FBRef.refUsers;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class LoggedUserManager {
    private static LoggedUserManager instance;
    private DBCollection.User loggedInUser;
    private ArrayList<DBCollection.User> userFriends;
    private ArrayList<DBCollection.Song> userRecents;

    private LoggedUserManager() {

    }

    public static synchronized LoggedUserManager getInstance() {
        if (instance == null) {
            instance = new LoggedUserManager();
        }
        return instance;
    }

    public void setLoggedInUser(DBCollection.User user) {
        this.loggedInUser = user;
        userFriends = new ArrayList<DBCollection.User>();
        userRecents = new ArrayList<DBCollection.Song>();
        if (loggedInUser.getFriends() != null)
        {
            for (String Uid : loggedInUser.getFriends())
            {
                findFriendInfo(Uid);
            }
        }
        if (loggedInUser.getRecents() != null)
        {
            for (String Uid : loggedInUser.getRecents())
            {
                findSongInfo(Uid);
            }
        }

    }

    public DBCollection.User getLoggedInUser() {
        return loggedInUser;
    }

    public ArrayList<DBCollection.User> getUserFriends() {
        return userFriends;
    }

    public ArrayList<DBCollection.Song> getUserRecents() {
        return userRecents;
    }

    public void addFriendByUser(DBCollection.User user)
    {
        loggedInUser.addNewFriend(user.getUserId());
        userFriends.add(user);
        updateUserDataFirebase();
    }


    public void addFriendById(String Uid)
    {
        loggedInUser.addNewFriend(Uid);
        findFriendInfo(Uid);
        updateUserDataFirebase();
    }

    public void addRecentBySong(DBCollection.Song song)
    {
        loggedInUser.addRecentSong(song.getId());
        if (userRecents.contains(song))
        {
            userRecents.remove(song);
        }
        else if (userRecents.size() == 5) {
            userRecents.remove(4);
        }
        userRecents.add(0,song);
        updateUserDataFirebase();
    }

    public void setNewUsernameToUser(String newUsername)
    {
        loggedInUser.setName(newUsername);
        updateUserDataFirebase();
    }

    public void setNewStatusToUser(String newStatus)
    {
        loggedInUser.setStatus(newStatus);
        updateUserDataFirebase();
    }

    public void updateUserDataFirebase()
    {
        refUsers.child(loggedInUser.getUserId()).setValue(loggedInUser);
    }

    public void findFriendInfo(String Uid)
    {
        refUsers.orderByChild("userId").equalTo(Uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot userSnapshot, String prevChildKey) {
                DBCollection.User friendFound = userSnapshot.getValue(DBCollection.User.class);
                userFriends.add(friendFound);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // ...
        });
    }

    private void findSongInfo(String uid) {
        refSongs.orderByChild("id").equalTo(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot songSnapshot, String prevChildKey) {
                DBCollection.Song songFound = songSnapshot.getValue(DBCollection.Song.class);
                userRecents.add(songFound);
                EventBus.getDefault().post(new EventMessages.RecentAdded());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

            // ...
        });
    }
}
