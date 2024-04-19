package com.example.wannajoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.wannajoin.Activities.MainActivity;
import com.example.wannajoin.Activities.PlayingNowActivity;
import com.example.wannajoin.Activities.SettingsActivity;

public class MenuBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_base);

        // Bottom menu button listeners
        ImageView homeIcon = findViewById(R.id.home_icon);
        ImageView playNowIcon = findViewById(R.id.play_now_icon);
        ImageView settingsIcon = findViewById(R.id.settings_icon);

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        playNowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayNow();
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
    }

    // Methods to handle bottom menu button clicks
    protected void goToHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    protected void goToPlayNow() {
        Intent intent = new Intent(getApplicationContext(), PlayingNowActivity.class);
        //intent.putExtra("Messenger", musicService);
        startActivity(intent);
    }

    protected void goToSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

}