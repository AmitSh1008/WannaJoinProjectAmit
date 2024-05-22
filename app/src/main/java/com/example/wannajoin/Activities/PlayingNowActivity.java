package com.example.wannajoin.Activities;

import static com.example.wannajoin.Utilities.FBRef.refRooms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wannajoin.Managers.PlaylistManager;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.example.wannajoin.Utilities.Constants;
import com.example.wannajoin.Utilities.DBCollection;
import com.example.wannajoin.Utilities.EventMessages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

public class PlayingNowActivity extends AppCompatActivity {

    private Messenger musicService;
    private TextView songName, songSinger, songDurationStart, songDurationEnd;
    private ImageView outerSongImage, homeRedirect, profileSettingsRedirect;

    private ImageButton prevBtn, playPauseBtn, nextBtn;
    private FrameLayout discSongLayout;
    private Boolean isFirstPlaying = true;
    private com.google.android.material.imageview.ShapeableImageView innerSongImage;

    private SeekBar songProgressBar;
    private int songDuration;
    private int currentTime = 0;
    private Timer timer;


    @Override
    protected void onStart() {
        super.onStart();
        // Register EventBus subscriber
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        // Unregister EventBus subscriber
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_now);
        Bundle bundle = new Bundle();
        bundle.putString("Message", "Are U playing right now?");
        PlaylistManager.sendMessageToService(Constants.MESSANGER.PLAYING_NOW_TO_SERVICE_GET_INFO, bundle);

        songName = findViewById(R.id.playingNowSongNameTv);
        songSinger = findViewById(R.id.playingNowSingerTv);
        songDurationStart = findViewById(R.id.progressStartTv);
        songDurationEnd = findViewById(R.id.progressEndTv);
        outerSongImage = findViewById(R.id.discPlayingNow);
        innerSongImage = findViewById(R.id.songInnerImage);
        prevBtn = findViewById(R.id.prevBtn);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        nextBtn = findViewById(R.id.nextBtn);
        discSongLayout = findViewById(R.id.discSongFrame);
        songProgressBar = findViewById(R.id.songProgressSeekBar);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (PlaylistManager.getInstance().isPlaying())
                        {
                            if (currentTime + 1000 > songDuration )
                            {
                                currentTime = songDuration;
                            }
                            else {
                                currentTime += 1000;
                            }

                        }
                        songProgressBar.setProgress(currentTime);
                        String currentTimeInString = formatDuration(currentTime);
                        songDurationStart.setText(currentTimeInString);
                    }
                });

            }
        }, 0, 1000);

        if (RoomManager.getInstance().getCurrentRoom().getCurrentSong() != null)
        {
            DBCollection.Song currSong = RoomManager.getInstance().getCurrentRoom().getCurrentSong();
            songName.setText(currSong.getName());
            songSinger.setText(currSong.getSinger());
            Glide.with(getApplicationContext()).load(currSong.getImage()).into(innerSongImage);
        }

        homeRedirect = findViewById(R.id.home_icon);
        profileSettingsRedirect = findViewById(R.id.settings_icon);

        homeRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        profileSettingsRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });


        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstPlaying)
                {
                    PlaylistManager.getInstance().setContext(getApplicationContext());
                    PlaylistManager.getInstance().startPlaylist();
                    playPauseBtn.setImageResource(R.drawable.pause_86px);
                    isFirstPlaying = false;
                }
                else if (PlaylistManager.getInstance().isPlaying())
                {
                    PlaylistManager.getInstance().pausePlaylist(currentTime);
                }
                else {
                    PlaylistManager.getInstance().resumePlaylist();
                }


            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFirstPlaying)
                {
                    PlaylistManager.getInstance().nextSongInPlaylist();
                }
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFirstPlaying)
                {
                    PlaylistManager.getInstance().previousSongInPlaylist();
                }
            }
        });


    }

    private String formatDuration(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.SongStarted event) {
        try {
            songName.setText(RoomManager.getInstance().getCurrentRoom().getCurrentSong().getName());
            songSinger.setText(RoomManager.getInstance().getCurrentRoom().getCurrentSong().getSinger());
            Glide.with(getApplicationContext()).load(RoomManager.getInstance().getCurrentRoom().getCurrentSong().getImage()).into(innerSongImage);
            songDuration = event.getCurrentSongDuration();
            songProgressBar.setProgress(0);
            songProgressBar.setMax(songDuration);
            currentTime = 0;
            songDurationEnd.setText(formatDuration(songDuration));
            isFirstPlaying = false;
            playPauseBtn.setImageResource(R.drawable.pause_86px);
            RotateAnimation rotateAnimation = new RotateAnimation(
                    0, 359,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setDuration(2000);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            discSongLayout.startAnimation(rotateAnimation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.SongPausedPlayed event) {
        try{
            if (event.isPlaying())
            {
                isFirstPlaying = false;
                currentTime = event.getPlaytime();
                playPauseBtn.setImageResource(R.drawable.pause_86px);
                RotateAnimation rotateAnimation = new RotateAnimation(
                            0, 359,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setDuration(2000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    discSongLayout.startAnimation(rotateAnimation);
            }
            else {
                playPauseBtn.setImageResource(R.drawable.play_86px);
                android.view.animation.Animation currentAnimation = discSongLayout.getAnimation();
                    if (currentAnimation != null) {
                        currentAnimation.setFillAfter(true);
                        discSongLayout.clearAnimation();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.SongEnded event) {
        try{
            PlaylistManager.getInstance().nextSongInPlaylist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //                if (isPlaying)
//                {
//                    playPauseBtn.setImageResource(R.drawable.play_86px);
//                    isPlaying = false;
//                    android.view.animation.Animation currentAnimation = discSongLayout.getAnimation();
//                    if (currentAnimation != null) {
//                        currentAnimation.setFillAfter(true);
//                        discSongLayout.clearAnimation();
//                    }
//                }
//                else {
//                    playPauseBtn.setImageResource(R.drawable.pause_86px);
//                    isPlaying = true;
//                    RotateAnimation rotateAnimation = new RotateAnimation(
//                            0, 359,
//                            Animation.RELATIVE_TO_SELF, 0.5f,
//                            Animation.RELATIVE_TO_SELF, 0.5f);
//                    rotateAnimation.setInterpolator(new LinearInterpolator());
//                    rotateAnimation.setDuration(2000);
//                    rotateAnimation.setRepeatCount(Animation.INFINITE);
//                    discSongLayout.startAnimation(rotateAnimation);
//                }
//                Bundle bundle = new Bundle();
//                bundle.putString("Message","Play/Pause music!");
//                sendMessageToService(Constants.MESSANGER.PLAYING_NOW_TO_SERVICE_PLAY_PAUSE, bundle);
//            }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.SongInfoEvent event) {
        int state = event.getState();
        DBCollection.Song song = event.getSong();
        try{
            switch (state)
            {
                case 0:
                    break;
                case 1:
                    isFirstPlaying = false;
                    playPauseBtn.setImageResource(R.drawable.pause_86px);
                    songName.setText(song.getName());
                    songSinger.setText(song.getSinger());
                    songDurationStart.setText(formatDuration(event.getCurrentTime()));
                    songDurationEnd.setText(formatDuration(event.getDuration()));
                    songDuration = event.getDuration();
                    Glide.with(getApplicationContext()).load(song.getImage()).into(innerSongImage);
                    RotateAnimation rotateAnimation = new RotateAnimation(
                            0, 359,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setDuration(2000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    discSongLayout.startAnimation(rotateAnimation);
                    currentTime = event.getCurrentTime();
                    songProgressBar.setMax(event.getDuration());
                    break;
                case 2:
                    isFirstPlaying = false;
                    playPauseBtn.setImageResource(R.drawable.play_86px);
                    songName.setText(song.getName());
                    songSinger.setText(song.getSinger());
                    songDurationStart.setText(formatDuration(event.getCurrentTime()));
                    songDurationEnd.setText(formatDuration(event.getDuration()));
                    songDuration = event.getDuration();
                    Glide.with(getApplicationContext()).load(song.getImage()).into(innerSongImage);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void sendMessageToService(int what, Bundle b) {
//
//        Message lMsg = Message.obtain(null, what);
//        lMsg.setData(b);
//        try {
//            musicService.send(lMsg);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public int convertDurationToSeconds(String duration) {
//        String[] timeComponents = duration.split(":");
//        int minutes = Integer.parseInt(timeComponents[0]);
//        int seconds = Integer.parseInt(timeComponents[1]);
//        return (minutes * 60) + seconds;
//    }
//
//    public String convertSecondsToDuration(int totalSeconds) {
//        int minutes = totalSeconds / 60;
//        int seconds = totalSeconds % 60;
//
//        String minutesString = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
//        String secondsString = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);
//
//        return minutesString + ":" + secondsString;
//    }
//
//    private void startSeekBarUpdate(int totalDurationInSeconds) {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                currentTimeInSeconds++;
//                songProgressBar.setProgress(currentTimeInSeconds * 100 / totalDurationInSeconds);
//
//                if (currentTimeInSeconds < totalDurationInSeconds) {
//                    handler.postDelayed(this, 1000); // Update every second (1000 milliseconds)
//                } else {
//
//                }
//            }
//        }, 1000); // Initial delay before starting the updates
//    }
}