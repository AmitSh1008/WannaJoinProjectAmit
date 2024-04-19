package com.example.wannajoin.Activities;

import static com.example.wannajoin.Utilities.FBRef.refFollows;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Managers.LoggedUserManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.DBCollection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FollowerFollowingProfileActivity extends AppCompatActivity {

    private DBCollection.User userInfo;
    private ImageView profilePic;
    private TextView userName, userStatus, tvFollowers, tvFollowings, tvHearingPoints;
    private Button followButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_following_profile);
        Intent intent = getIntent();
        if (intent != null)
            userInfo = (DBCollection.User)intent.getSerializableExtra("UserInfo");


        profilePic = findViewById(R.id.profilePicture);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        tvFollowers = findViewById(R.id.followersTv);
        tvFollowings = findViewById(R.id.followingsTv);
        tvHearingPoints = findViewById(R.id.hearingPointsTv);
        followButton = findViewById(R.id.btnFollow);

        if (userInfo != null) {
            userName.setText(userInfo.getName());
            userStatus.setText(userInfo.getStatus());
            tvHearingPoints.setText(String.valueOf(userInfo.getHearingPoints()));
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(userInfo.getImage())
                    .placeholder(R.drawable.default_profile_picture)
                    .into(profilePic);

            refFollows.child(userInfo.getUserId()).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tvFollowings.setText(String.valueOf(snapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            refFollows.child(userInfo.getUserId()).child("followers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tvFollowers.setText(String.valueOf(snapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (LoggedUserManager.getInstance().isUserAlreadyFollowed(userInfo.getUserId()))
            {
                followButton.setText("Unfollow");
            }

            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (followButton.getText().toString().equals("Follow"))
                    {
                        followButton.setText("Unfollow");
                        LoggedUserManager.getInstance().addUserToFollowByUser(userInfo);
                    }
                    else
                    {
                        followButton.setText("Follow");
                        LoggedUserManager.getInstance().removeUserFromFollowByUser(userInfo);
                    }
                }
            });

        }

    }

}