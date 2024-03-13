package com.example.wannajoin;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers=FBDB.getReference("Users");
    public static DatabaseReference refSongs=FBDB.getReference("Songs");
    public static DatabaseReference refSingers=FBDB.getReference("Singers");
    public static DatabaseReference refGenres=FBDB.getReference("Genres");
}
