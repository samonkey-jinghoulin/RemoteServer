package com.samonkey.remoteserver.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.samonkey.remoteserver.ble.BLEService;
import com.samonkey.remoteserver.socket.ControllerService;

/**
 * 用于开机后，启动相关Service
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ControllerService.class));
        context.startService(new Intent(context, BLEService.class));
    }
}
