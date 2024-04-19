package com.example.wannajoin.Utilities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refSongs = FBDB.getReference("Songs");
    public static DatabaseReference refSingers = FBDB.getReference("Singers");
    public static DatabaseReference refGenres = FBDB.getReference("Genres");
    public static DatabaseReference refFollows = FBDB.getReference("Follows");
    public static DatabaseReference refRooms = FBDB.getReference("Rooms");
    public static DatabaseReference refPlaylists = FBDB.getReference("Playlists");
}
