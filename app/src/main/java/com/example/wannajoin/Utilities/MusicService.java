package com.example.wannajoin.Utilities;

import static com.example.wannajoin.Utilities.FBRef.refRooms;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wannajoin.Managers.RoomManager;
import com.example.wannajoin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MusicService extends Service {

    //Notification
    public static final int CHANNEL_ID_NUM = 1;
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_NAME_1 = "FIRSTCHANNEL";
    private NotificationManager manager;
    private NotificationManagerCompat notificationManagerCompat;

    private NotificationCompat.Builder notification;

    private PendingIntent pendingIntentStop;
    private Intent stopReceive;
    private PendingIntent pendingIntentPrev;
    private Intent prevReceive;
    private PendingIntent pendingIntentNext;
    private Intent nextReceive;
    private PendingIntent pendingIntentPlay;
    private Intent playReceive;

    private BroadcastReceiver mReceiver;

    //Messenger
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Messenger activityMessenger;

    //Player
    private MediaPlayer mediaPlayer;
    private DBCollection.Song currentSong;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        try {
            stopForeground(true);
            mediaPlayer = new MediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        if (intent != null && intent.getAction() != null && intent.getAction().equals(Constants.ACTION.SERVICE_BUILD_ACTION)) {
            activityMessenger = intent.getParcelableExtra("Messenger");
            stopReceive = new Intent(getApplicationContext(), MusicService.class);
            stopReceive.setAction(Constants.ACTION.SERVICE_STOP_ACTION);
            pendingIntentStop = PendingIntent.getService(this, 12345, stopReceive, PendingIntent.FLAG_IMMUTABLE);
            Bundle bundle = new Bundle();
            bundle.putString("Message", "Hello");
            try {
                sendMessage(Constants.MESSANGER.FROM_SERVICE_HELLO, bundle);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        if (intent != null && intent.getAction() != null && intent.getAction().equals(Constants.ACTION.SERVICE_STOP_ACTION)) {
            try {
                mediaPlayer.stop();
                cancelNotification(CHANNEL_ID_NUM);
                Log.i("AudioPlayerService", "Received AUDIO_PLAYER_STOP_ACTION");
            } catch (Exception ex) {
                Log.e("AudioPlayerService", "AUDIO_PLAYER_STOP_ACTION.");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendMessage(int what, Bundle b) throws RemoteException {
        Message lMsg = Message.obtain(null, what);
        lMsg.setData(b);
        try {
            activityMessenger.send(lMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void playSong(String songLink) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songLink);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }

            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });

            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID_1,
                    CHANNEL_NAME_1,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel1.enableLights(true);

            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        } else {
            notificationManagerCompat = NotificationManagerCompat.from(this);
        }
    }

    public void sendNotification(Bundle bundle) {
        Notification channel = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_play_arrow_white_24dp)
                .setContentTitle(bundle.getString("NAME"))
                .setContentText("by " + bundle.getString("SINGER"))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.moth_to_a_flame))
                .addAction(R.drawable.ic_stop_white_24dp, "Stop", pendingIntentStop)
                .setColor(Color.MAGENTA)
                .setColorized(true)
                //.build();
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setCancelButtonIntent(pendingIntentStop)).build();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.notify(CHANNEL_ID_NUM, channel);
        } else {
            notificationManagerCompat.notify(CHANNEL_ID_NUM, channel);
        }
    }


    public void cancelNotification(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager != null)
                manager.cancel(id);
        } else {
            if (notificationManagerCompat != null)
                notificationManagerCompat.cancel(id);
        }
    }

    @SuppressLint("RestrictedApi")
    public void showNotification(int icon, String message, String label, int id, String iconPath) {
        Bitmap bmp = null;
        int placeholderResourceId = R.drawable.image_not_found;

        if (iconPath.compareTo("") == 0)
            bmp = BitmapFactory.decodeResource(this.getResources(), icon);
        else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory
                    .decodeFile(iconPath, options);

        }

        if (notification != null) {
            notification.setSmallIcon(R.drawable.ic_play_arrow_white_24dp);
            notification.setContentText(message);
            notification.setContentTitle(label);
            notification.setChannelId(CHANNEL_ID_1);
            if (notification.mActions.size() == 0) {
                notification.addAction(R.drawable.ic_stop_white_24dp, "Stop", pendingIntentStop);
                notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // show only play/pause in compact view
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(pendingIntentStop));
            }

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                    .skipMemoryCache(true)
                    .fitCenter();

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(iconPath.compareTo("") == 0 ? icon : iconPath)
                    .placeholder(placeholderResourceId)
                    .error(placeholderResourceId)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            notification.setLargeIcon(resource);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (manager != null)
                                    manager.notify(id, notification.build());
                            } else {
                                if (notificationManagerCompat != null)
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                notificationManagerCompat.notify(id, notification.build());
                            }
                        }
                    });
        } else {
            notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_1)
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentTitle(label)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    //.setLargeIcon(bmp)
                    .setSmallIcon(R.drawable.ic_play_arrow_white_24dp);

            if (label.compareTo("Playing... ") == 0) {
                notification.mActions.clear();
                notification.addAction(R.drawable.ic_stop_white_24dp, "Stop", pendingIntentStop);
                notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // show only play/pause in compact view
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(pendingIntentStop)
                        );
            } else {
                notification.mActions.clear();
                notification.addAction(R.drawable.ic_skip_previous_white_24dp, "Prev", pendingIntentPrev);
                notification.addAction(R.drawable.ic_play_arrow_white_24dp, "Play", pendingIntentPlay);
                notification.addAction(R.drawable.ic_stop_white_24dp, "Stop", pendingIntentStop);
                notification.addAction(R.drawable.ic_skip_next_white_24dp, "Next", pendingIntentNext);
                notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // show only play/pause in compact view
                        .setShowActionsInCompactView(0, 1, 3)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(pendingIntentStop)
                        );
            }

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(iconPath.compareTo("") == 0 ? icon : iconPath)
                    .placeholder(placeholderResourceId)
                    .error(placeholderResourceId)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            notification.setLargeIcon(resource);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (manager != null)
                                    manager.notify(id, notification.build());
                            } else {
                                if (notificationManagerCompat != null)
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }notificationManagerCompat.notify(id, notification.build());
                            }
                        }
                    });
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        stopForeground(true);

    }

class IncomingHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        switch (msg.what) {
            case Constants.MESSANGER.TO_SERVICE_HELLO:
                String messageFromActivity = bundle.getString("Message");
                Toast.makeText(getApplicationContext(), "Activity said: " + messageFromActivity, Toast.LENGTH_SHORT).show();
                break;
            case Constants.MESSANGER.TO_SERVICE_PLAY_SONG:
                currentSong = new DBCollection.Song(bundle.getString("ID"),bundle.getString("NAME"),
                        bundle.getString("SINGER"),
                        bundle.getInt("YEAR"),
                        bundle.getString("DURATION"),
                        bundle.getString("GENRE"),
                        bundle.getString("IMAGE"),
                        bundle.getString("LINK"));
                playSong(bundle.getString("LINK"));
                showNotification(R.drawable.ic_play_arrow_white_24dp, bundle.getString("NAME"),bundle.getString("SINGER"),CHANNEL_ID_NUM, bundle.getString("IMAGE") );
                //sendNotification(bundle);
                break;
            case Constants.MESSANGER.ALARM_TO_SERVICE_STOP_SONG:
                String messageFromAlarm = bundle.getString("Message");
                Toast.makeText(getApplicationContext(), "Alarm said: " + messageFromAlarm, Toast.LENGTH_SHORT).show();
                mediaPlayer.stop();
            case Constants.MESSANGER.PLAYING_NOW_TO_SERVICE_GET_INFO:
                /*Bundle songForPlayingNow = new Bundle();
                bundle.putString("NAME", currentSong.getName());
                bundle.putString("SINGER", currentSong.getSinger());
                bundle.putInt("YEAR", currentSong.getYear());
                bundle.putString("DURATION", currentSong.getDuration());
                bundle.putString("IMAGE", currentSong.getImage());
                bundle.putString("LINK", currentSong.getLink());*/
                if (currentSong != null && (mediaPlayer != null && mediaPlayer.isPlaying()))
                {
                    EventBus.getDefault().post(new EventMessages.SongInfoEvent(true, currentSong));
                }
                else {
                    EventBus.getDefault().post(new EventMessages.SongInfoEvent(false, null));
                }
                break;
            case Constants.MESSANGER.PLAYING_NOW_TO_SERVICE_PLAY_PAUSE:
                if ((mediaPlayer != null && mediaPlayer.isPlaying()))
                {
                    mediaPlayer.pause();
                }
                else if ((mediaPlayer != null && !mediaPlayer.isPlaying())) {
                    mediaPlayer.start();
                }
                break;
            case Constants.MESSANGER.START_LISTENING_TO_ROOM:
                String roomID = bundle.getString("RoomID");
                refRooms.child(roomID).child("currentSongStartTime").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null)
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                            refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).child("currentSong").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    DBCollection.Song songToPlay = snapshot.getValue(DBCollection.Song.class);
                                    Log.d("CheckForPlay",songToPlay.getName());
                                    playSong(songToPlay.getLink());
                                    Log.d("Playing Song", songToPlay.getLink());
                                    EventBus.getDefault().post(new EventMessages.SongPausedPlayed(true));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                refRooms.child(RoomManager.getInstance().getCurrentRoom().getId()).child("playing").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isPlaying = snapshot.getValue(Boolean.class);
                        if ((mediaPlayer != null && (mediaPlayer.isPlaying() && !isPlaying)))
                        {
                            mediaPlayer.pause();
                            EventBus.getDefault().post(new EventMessages.SongPausedPlayed(false));
                        }
                        else if (mediaPlayer != null && (!mediaPlayer.isPlaying() && isPlaying) && mediaPlayer.getCurrentPosition() > 0) {
                            mediaPlayer.start();
                            EventBus.getDefault().post(new EventMessages.SongPausedPlayed(true));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;


            default:
                super.handleMessage(msg);
        }
    }
}
}
