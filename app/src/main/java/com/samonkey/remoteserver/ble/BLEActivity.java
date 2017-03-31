package com.samonkey.remoteserver.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * 用于开启蓝牙
 * Created on 2017/3/20
 *
 * @author saker
 */

public class BLEActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 9;
    private static OpenBTCallback sCallback;

    /**
     * 启动此Activity，并传入回调
     *
     * @param context
     * @param callback
     */
    public static void startBLE(Context context, OpenBTCallback callback) {
        Intent intent = new Intent(context, BLEActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        sCallback = callback;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无contentView，则透明

        // 请求开启蓝牙
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && sCallback != null) {
            if (resultCode == RESULT_OK) {
                sCallback.onSuccess();
            } else {
                sCallback.onFailure();
            }
        }
        finish();
    }
}
