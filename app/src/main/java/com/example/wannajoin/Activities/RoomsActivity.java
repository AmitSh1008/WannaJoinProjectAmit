package com.example.wannajoin.Activities;

import static com.example.wannajoin.Utilities.FBRef.refRooms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.wannajoin.Adapters.RoomsAdapter;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomsActivity extends AppCompatActivity {

    private RecyclerView roomsList;
    private Button createRoomBtn;
    private AlertDialog createDialog;

    private ArrayList<DBCollection.Room> rooms;
    private RoomsAdapter roomsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        
        roomsList = findViewById(R.id.groupsList);
        createRoomBtn = findViewById(R.id.createRoomBtn);
        rooms = new ArrayList<DBCollection.Room>();

        roomsAdapter = new RoomsAdapter(getApplicationContext(), rooms);
        LinearLayoutManager roomsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        roomsList.setLayoutManager(roomsLayoutManager);
        roomsList.setAdapter(roomsAdapter);

        updateRoomsList();

        refRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate through each room
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    // Get reference to the "Participants" node of the room
                    DatabaseReference participantsRef = roomSnapshot.child("participants").getRef();

                    // Add ValueEventListener to listen for changes in "Participants" field
                    participantsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot participantsSnapshot) {
                            updateRoomsList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle errors
                            Log.e("TAG", "Error listening for participants changes", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("TAG", "Error listening for rooms changes", databaseError.toException());
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_open_room_layout, null);
        EditText roomNameET = view.findViewById(R.id.roomNameDialog);
        NumberPicker maxPartsNP = view.findViewById(R.id.maxPartsNPDialog);
        maxPartsNP.setMinValue(1);
        maxPartsNP.setMaxValue(10);
        maxPartsNP.setValue(5);
        Button createBtnDialog = view.findViewById(R.id.createBtnDialog);
        Button cancelBtnDialog = view.findViewById(R.id.cancelBtnDialog);

        createBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pushId = FirebaseDatabase.getInstance().getReference().push().getKey();
                DBCollection.Room newRoom = new DBCollection.Room(pushId, roomNameET.getText().toString(), LoggedUserManager.getInstance().getLoggedInUser().getName(), maxPartsNP.getValue(), true);
                refRooms.child(pushId).setValue(newRoom);
                RoomManager.getInstance().joinRoom(newRoom, true);
                roomNameET.setText("");
                maxPartsNP.setValue(5);
                createDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), InnerRoomActivity.class));
            }
        });

        cancelBtnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomNameET.setText("");
                maxPartsNP.setValue(5);
                createDialog.dismiss();
            }
        });

        builder.setView(view);
        createDialog = builder.create();

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog.show();
            }
        });
    }

    public void updateRoomsList(){
        refRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                for (DataSnapshot roomSnapshot : snapshot.getChildren())
                {
                    DBCollection.Room room = roomSnapshot.getValue(DBCollection.Room.class);
                    rooms.add(room);
                    roomsAdapter.updateData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}