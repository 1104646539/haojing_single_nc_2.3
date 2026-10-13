package com.tnd.multifuction1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tnd.FirstMainActivity;
import com.tnd.multifuction1.activity.MainActivity;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.i(TAG, "接收到开机广播.....");
        Intent in = new Intent(context, MainActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra("isBoot", true);
        context.startActivity(in);
    }
}
