package com.samonkey.remoteserver.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.samonkey.remoteserver.server.ControllerService;

/**
 * 用于开机后，启动相关Service
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(context, ControllerService.class);
        context.startService(serviceIntent);

//        Intent aIntent = new Intent(context, MainActivity.class);
//        aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(aIntent);
    }
}
