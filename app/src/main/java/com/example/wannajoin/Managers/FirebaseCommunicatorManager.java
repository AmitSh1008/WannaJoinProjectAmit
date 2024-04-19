package com.example.wannajoin.Managers;

import static com.example.wannajoin.Utilities.FBRef.refPlaylists;
import static com.example.wannajoin.Utilities.FBRef.refUsers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.wannajoin.Utilities.DBCollection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseCommunicatorManager {

    public static void addPlaylist()
    {

    }
    public static void addSongToPlaylist(Context context, String playlistID, String newSongID)
    {
        refPlaylists.child(playlistID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(newSongID)) {
                        Toast.makeText(context, "Song Already in the playlist!", Toast.LENGTH_SHORT).show();
                    } else {
                        refPlaylists.child(playlistID).child(newSongID).setValue(true);
                    }
                } else {
                    // Playlist does not exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public static void updateUserDataFirebase(DBCollection.User user)
    {
        refUsers.child(user.getUserId()).setValue(user);
    }
}
