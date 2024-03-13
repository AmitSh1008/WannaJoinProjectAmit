package com.example.wannajoin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class AlarmReciever extends BroadcastReceiver {

    private Messenger musicService;

    @Override
    public void onReceive(Context context, Intent intent) {
        musicService = intent.getParcelableExtra("Messenger");

        Bundle bundle = new Bundle();
        bundle.putString("Message","Please stop the music!");
        sendMessageToService(Constants.MESSANGER.ALARM_TO_SERVICE_STOP_SONG, bundle);
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
}
