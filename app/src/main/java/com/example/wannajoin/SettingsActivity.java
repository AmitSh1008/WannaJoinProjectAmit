package com.example.wannajoin;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ImageView profilePic;
    private TextView userName, userStatus;
    private Button btnLogOut, btnAddFriends, btnSetNewUN, btnSetNewStatus;

    AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle intentExtras = getIntent().getExtras();
        Bundle connectedUser = intentExtras.getBundle("userConnected");
        auth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        profilePic = findViewById(R.id.profilePicture);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);

        btnLogOut = findViewById(R.id.logoutButton);
        btnSetNewUN = findViewById(R.id.updateUsernameBtn);
        btnSetNewStatus = findViewById(R.id.updateStatusBtn);

        userName.setText(connectedUser.getString("NAME"));
        userStatus.setText(connectedUser.getString("STATUS"));
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(connectedUser.getString("IMAGE"))
                .placeholder(R.drawable.default_profile_picture)
                .into(profilePic);


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferencess = getSharedPreferences("RememberMe",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencess.edit();
                editor.remove("StayConnect");
                editor.remove("Email");
                editor.remove("Password");
                editor.apply();

                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
            }
        });

        SearchForFriendAdapter adapter = new SearchForFriendAdapter(this, new ArrayList<DBCollection.User>());

// Set the adapter to the AutoCompleteTextView
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = (String) parent.getItemAtPosition(position);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call this method to open the drawer
    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // Call this method to close the drawer
    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}