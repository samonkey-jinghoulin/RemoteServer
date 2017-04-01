package com.samonkey.remoteserver.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.samonkey.remoteserver.event.DataProcessor;
import com.samonkey.remoteserver.utils.LogUtils;
import com.samonkey.remoteserver.view.FloatManager;

public class ControllerService extends Service {
    private static final String TAG = "ControllerService";

    private UDPBroadcast mUdpBroadcast;
    private DataProcessor mProcessor;

    public ControllerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("ControllerService started");
        // test
//        if (!FloatManager.isAdd()) {
//            FloatManager.getInstance().addPointer();
//        }
        mProcessor = new DataProcessor(DataProcessor.SOCKET_SERVER);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUdpBroadcast = UDPBroadcast.getInstance(this, new UDPCallback() {
            @Override
            public void accept(String data) {
                if (mProcessor != null) {
                    mProcessor.accept(data);
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mUdpBroadcast.closeServer();
        FloatManager.getInstance().removePointer();
        super.onDestroy();
    }

}
