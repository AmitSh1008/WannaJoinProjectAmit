package com.example.wannajoin;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ImageView profilePic;
    private TextView userName, userStatus, friendsTitle;

    private ArrayList<DBCollection.User> userFriends;
    private RecyclerView friendsList;

    private UserFriendsAdapter friendsAdapter;
    private EditText updateUN, updateStatus;
    private Button btnLogOut, btnAddFriends, btnSetNewUN, btnSetNewStatus;

    AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DBCollection.User connectedUser = LoggedUserManager.getInstance().getLoggedInUser();
        EventBus.getDefault().register(this);

        auth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        profilePic = findViewById(R.id.profilePicture);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        friendsTitle = findViewById(R.id.friendsTitleTextView);
        friendsList = findViewById(R.id.friendsList);


        btnLogOut = findViewById(R.id.logoutButton);

        updateUN = findViewById(R.id.updateUsernameEditText);
        updateStatus = findViewById(R.id.updateStatusEditText);
        btnSetNewUN = findViewById(R.id.updateUsernameBtn);
        btnSetNewStatus = findViewById(R.id.updateStatusBtn);

        userName.setText(connectedUser.getName());
        userStatus.setText(connectedUser.getStatus());
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(connectedUser.getImage())
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

        btnSetNewUN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!updateUN.getText().toString().isEmpty()) {
                    LoggedUserManager.getInstance().setNewUsernameToUser(updateUN.getText().toString());
                    Toast.makeText(getApplicationContext(), "Username changed successfully, Update after restart!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSetNewStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!updateStatus.getText().toString().isEmpty()) {
                    LoggedUserManager.getInstance().setNewStatusToUser(updateStatus.getText().toString());
                    Toast.makeText(getApplicationContext(), "Status changed successfully, Update after restart!", Toast.LENGTH_LONG).show();
                }
            }
        });

        friendsAdapter = new UserFriendsAdapter(getApplicationContext(), LoggedUserManager.getInstance().getUserFriends());
        LinearLayoutManager friendsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        friendsList.setLayoutManager(friendsLayoutManager);
        friendsList.setAdapter(friendsAdapter);

        SearchForFriendAdapter searchFriendsAdapter = new SearchForFriendAdapter(this, new ArrayList<DBCollection.User>());
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(searchFriendsAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBCollection.User selectedUser = searchFriendsAdapter.getItem(position);
                LoggedUserManager.getInstance().addFriendByUser(selectedUser);
                friendsAdapter.updateData();
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

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.FriendAdded event) {
        try{
            friendsAdapter = new UserFriendsAdapter(getApplicationContext(), LoggedUserManager.getInstance().getUserFriends());
            friendsList.setAdapter(friendsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}