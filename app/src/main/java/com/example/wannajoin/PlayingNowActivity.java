package com.example.wannajoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlayingNowActivity extends AppCompatActivity {

    private Messenger musicService;
    private TextView songName, songSinger, songDurationStart, songDurationEnd;
    private ImageView outerSongImage;

    private ImageButton prevBtn, playPauseBtn, nextBtn;
    private FrameLayout discSongLayout;
    private Boolean isPlaying;
    private com.google.android.material.imageview.ShapeableImageView innerSongImage;

    private SeekBar songProgressBar;
    private int currentTimeInSeconds = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_now);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        musicService = intent.getParcelableExtra("Messenger");
        Bundle bundle = new Bundle();
        bundle.putString("Message","Are U playing right now?");
        sendMessageToService(Constants.MESSANGER.PLAYING_NOW_TO_SERVICE_GET_INFO, bundle);

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

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying)
                {
                    playPauseBtn.setImageResource(R.drawable.play_86px);
                    isPlaying = false;
                    android.view.animation.Animation currentAnimation = discSongLayout.getAnimation();
                    if (currentAnimation != null) {
                        currentAnimation.setFillAfter(true);
                        discSongLayout.clearAnimation();
                    }
                }
                else {
                    playPauseBtn.setImageResource(R.drawable.pause_86px);
                    isPlaying = true;
                    RotateAnimation rotateAnimation = new RotateAnimation(
                            0, 359,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    rotateAnimation.setDuration(2000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    discSongLayout.startAnimation(rotateAnimation);
                }
                Bundle bundle = new Bundle();
                bundle.putString("Message","Play/Pause music!");
                sendMessageToService(Constants.MESSANGER.PLAYING_NOW_TO_SERVICE_PLAY_PAUSE, bundle);
            }
        });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessageEvent(EventMessages.SongInfoEvent event) {
        boolean isPlayingRN = event.isPlaying();
        DBCollection.Song song = event.getSong();
        try{
            if (isPlayingRN)
            {
                playPauseBtn.setImageResource(R.drawable.pause_86px);
                isPlaying = true;
                handler = new Handler();
                startSeekBarUpdate(convertDurationToSeconds(song.getDuration()));
                songName.setText(song.getName());
                songSinger.setText(song.getSinger());
                songDurationEnd.setText(song.getDuration());
                Glide.with(getApplicationContext()).load(song.getImage()).into(innerSongImage);
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
                isPlaying = false;
                songName.setText("");
                songSinger.setText("");
                songDurationStart.setText("");
                songDurationEnd.setText("");
                innerSongImage.setImageResource(R.drawable.no_image_placeholder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMessageToService(int what, Bundle b) {

        Message lMsg = Message.obtain(null, what);
        lMsg.setData(b);
        try {
            musicService.send(lMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int convertDurationToSeconds(String duration) {
        String[] timeComponents = duration.split(":");
        int minutes = Integer.parseInt(timeComponents[0]);
        int seconds = Integer.parseInt(timeComponents[1]);
        return (minutes * 60) + seconds;
    }

    public String convertSecondsToDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String minutesString = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
        String secondsString = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);

        return minutesString + ":" + secondsString;
    }

    private void startSeekBarUpdate(int totalDurationInSeconds) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentTimeInSeconds++;
                songProgressBar.setProgress(currentTimeInSeconds * 100 / totalDurationInSeconds);

                if (currentTimeInSeconds < totalDurationInSeconds) {
                    handler.postDelayed(this, 1000); // Update every second (1000 milliseconds)
                } else {

                }
            }
        }, 1000); // Initial delay before starting the updates
    }

}