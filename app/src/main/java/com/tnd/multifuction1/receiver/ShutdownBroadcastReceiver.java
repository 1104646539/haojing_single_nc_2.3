package com.tnd.multifuction1.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        //关盖
//        SerialUtils.COM3_SendData()
        Toast.makeText(context, "关机了", Toast.LENGTH_SHORT).show();
        Log.d("ShutdownBroadcast", "onReceive=关机了");
    }
}
