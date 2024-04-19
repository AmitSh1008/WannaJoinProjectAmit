package com.example.wannajoin.Managers;


import static com.example.wannajoin.Utilities.FBRef.refFollows;
import static com.example.wannajoin.Utilities.FBRef.refSongs;
import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class LoggedUserManager {
    private static LoggedUserManager instance;
    private DBCollection.User loggedInUser;
    private ArrayList<DBCollection.User> userFollowers;
    private ArrayList<DBCollection.User> userFollowings;
    private ArrayList<DBCollection.Song> userRecents;
    private boolean isFollowersChanged = false;
    private boolean isFollowingsChanged = false;

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
        userFollowings = new ArrayList<DBCollection.User>();
        userFollowers = new ArrayList<DBCollection.User>();
        userRecents = new ArrayList<DBCollection.Song>();
        getUserFollowersAndFollowing();
        if (loggedInUser.getRecents() != null) {
            for (String Uid : loggedInUser.getRecents()) {
                findSongInfo(Uid);
            }
        }
    }

    public void logOut() {
        loggedInUser = null;
        userFollowings = null;
        userFollowers = null;
        userRecents = null;
        isFollowersChanged = false;
        isFollowingsChanged = false;
    }

    public DBCollection.User getLoggedInUser() {
        return loggedInUser;
    }

    public ArrayList<DBCollection.User> getUserFollowers() {
        return userFollowers;
    }

    public ArrayList<DBCollection.User> getUserFollowings() {
        return userFollowings;
    }

    private void getUserFollowersAndFollowing() {
        DatabaseReference ref = refFollows.child(loggedInUser.getUserId());
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFollowers.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        fillUserFollowersFollowings(snapshot.getChildrenCount(), userSnapshot.getKey(), "followers", () -> {
                            // This callback runs when all followers have been added
                            if (isFollowersChanged)
                                EventBus.getDefault().post(new EventMessages.FollowersFollowingsChanged(true));
                        });
                    }
                } else {
                    EventBus.getDefault().post(new EventMessages.FollowersFollowingsChanged(true));
                }
                isFollowersChanged = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userFollowings.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    fillUserFollowersFollowings(snapshot.getChildrenCount(), userSnapshot.getKey(), "followings", () -> {
                        // This callback runs when all followers have been added
                        if (isFollowingsChanged)
                            EventBus.getDefault().post(new EventMessages.FollowersFollowingsChanged(false));
                    });
                }
                isFollowingsChanged = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean isUserAlreadyFollowed(String id) {
        for (DBCollection.User user : userFollowings) {
            if (user.getUserId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<DBCollection.Song> getUserRecents() {
        return userRecents;
    }

    public void addUserToFollowByUser(DBCollection.User user)
    {
        refFollows.child(loggedInUser.getUserId()).child("following").child(user.getUserId()).setValue(true);
        refFollows.child(user.getUserId()).child("followers").child(loggedInUser.getUserId()).setValue(true);
    }
    public void removeUserFromFollowByUser(DBCollection.User user)
    {
        refFollows.child(loggedInUser.getUserId()).child("following").child(user.getUserId()).removeValue();
        refFollows.child(user.getUserId()).child("followers").child(loggedInUser.getUserId()).removeValue();
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
        FirebaseCommunicatorManager.updateUserDataFirebase(loggedInUser);
    }

    public void setNewUsernameToUser(String newUsername)
    {
        loggedInUser.setName(newUsername);
        FirebaseCommunicatorManager.updateUserDataFirebase(loggedInUser);
    }

    public void setNewStatusToUser(String newStatus)
    {
        loggedInUser.setStatus(newStatus);
        FirebaseCommunicatorManager.updateUserDataFirebase(loggedInUser);
    }

    public void fillUserFollowersFollowings(long stopCount, String id, String relationshipType, Runnable callback) {
        refUsers.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DBCollection.User userFound = snapshot.getValue(DBCollection.User.class);
                if (relationshipType.equals("followers")) {
                    userFollowers.add(userFound);
                    if (callback != null && stopCount == userFollowers.size()) {
                        callback.run();
                    }
                } else if (relationshipType.equals("followings")) {
                    userFollowings.add(userFound);
                    if (callback != null && stopCount == userFollowings.size()) {
                        callback.run();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
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
        });
    }
}
