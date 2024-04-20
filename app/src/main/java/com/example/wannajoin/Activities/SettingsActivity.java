package com.example.wannajoin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Adapters.SearchForUserAdapter;
import com.example.wannajoin.Adapters.UserFollowersFollowingAdapter;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private ImageView imgLogOut, profilePic, homeRedirect;
    private TextView userName, userStatus, followingsTitle, followersTitle, tvFollowers, tvFollowings, tvHearingPoints;
    private RecyclerView followingsList, followersList;

    private UserFollowersFollowingAdapter followingsAdapter, followersAdapter;
    private EditText updateUN, updateStatus;
    private Button btnSetNewUN, btnSetNewStatus;

    private Switch rememberMeSwitch, lightModeSwitch, notiSwitch;
    private AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        EventBus.getDefault().register(this);
        DBCollection.User connectedUser = LoggedUserManager.getInstance().getLoggedInUser();


        auth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        profilePic = findViewById(R.id.profilePicture);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        tvFollowers = findViewById(R.id.followersTv);
        tvFollowings = findViewById(R.id.followingsTv);
        tvHearingPoints = findViewById(R.id.hearingPointsTv);

        rememberMeSwitch = findViewById(R.id.switchSignedIn);
        lightModeSwitch = findViewById(R.id.switchLightMode);
        notiSwitch = findViewById(R.id.switchNotifications);

        followingsTitle = findViewById(R.id.followingsTitleTextView);
        followingsList = findViewById(R.id.followingsList);
        followersTitle = findViewById(R.id.followersTitleTextView);
        followersList = findViewById(R.id.followersList);


        imgLogOut = findViewById(R.id.logoutIcon);

//        updateUN = findViewById(R.id.updateUsernameEditText);
//        updateStatus = findViewById(R.id.updateStatusEditText);
//        btnSetNewUN = findViewById(R.id.updateUsernameBtn);
//        btnSetNewStatus = findViewById(R.id.updateStatusBtn);

        userName.setText(connectedUser.getName());
        userStatus.setText(connectedUser.getStatus());
        tvFollowings.setText(String.valueOf(LoggedUserManager.getInstance().getUserFollowings().size()));
        tvFollowers.setText(String.valueOf(LoggedUserManager.getInstance().getUserFollowers().size()));
        tvHearingPoints.setText(String.valueOf(connectedUser.getHearingPoints()));
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(connectedUser.getImage())
                .placeholder(R.drawable.default_profile_picture)
                .into(profilePic);
        SharedPreferences sharedPreferences = getSharedPreferences("RememberMe",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("StayConnect", false)) {
            rememberMeSwitch.setChecked(true);
        }
        homeRedirect = findViewById(R.id.home_icon);

        homeRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferencess = getSharedPreferences("RememberMe", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencess.edit();
                editor.remove("StayConnect");
                editor.remove("Email");
                editor.remove("Password");
                editor.apply();

                auth.signOut();
                LoggedUserManager.getInstance().logOut();
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(intent);
            }
        });

//        btnSetNewUN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!updateUN.getText().toString().isEmpty()) {
//                    LoggedUserManager.getInstance().setNewUsernameToUser(updateUN.getText().toString());
//                    Toast.makeText(getApplicationContext(), "Username changed successfully, Update after restart!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//        btnSetNewStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!updateStatus.getText().toString().isEmpty()) {
//                    LoggedUserManager.getInstance().setNewStatusToUser(updateStatus.getText().toString());
//                    Toast.makeText(getApplicationContext(), "Status changed successfully, Update after restart!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        followingsAdapter = new UserFollowersFollowingAdapter(getApplicationContext(), LoggedUserManager.getInstance().getUserFollowings());
        LinearLayoutManager followingsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        followingsList.setLayoutManager(followingsLayoutManager);
        followingsList.setAdapter(followingsAdapter);

        followingsList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        followersAdapter = new UserFollowersFollowingAdapter(getApplicationContext(), LoggedUserManager.getInstance().getUserFollowers());
        LinearLayoutManager followersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        followersList.setLayoutManager(followersLayoutManager);
        followersList.setAdapter(followersAdapter);

        SearchForUserAdapter searchForUserAdapter = new SearchForUserAdapter(this, new ArrayList<DBCollection.User>());
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(searchForUserAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBCollection.User selectedUser = searchForUserAdapter.getItem(position);
                autoCompleteTextView.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                Intent intent = new Intent(getApplicationContext(), FollowerFollowingProfileActivity.class);
                intent.putExtra("UserInfo", selectedUser);
                startActivity(intent);
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

    public void handleRememberMeSwitch(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("RememberMe",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("StayConnect", false)) {
            rememberMeSwitch.setChecked(false);
            editor.putBoolean("StayConnect", false);
            editor.apply();
        }
        else
        {
            rememberMeSwitch.setChecked(true);
            editor.putBoolean("StayConnect", true);
            editor.apply();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.FollowersFollowingsChanged event) {
        try{
            if (event.isFollowers())
            {
                tvFollowers.setText(String.valueOf(LoggedUserManager.getInstance().getUserFollowers().size()));
                followersAdapter.updateData();
            }
            else {
                tvFollowings.setText(String.valueOf(LoggedUserManager.getInstance().getUserFollowings().size()));
                followingsAdapter.updateData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}