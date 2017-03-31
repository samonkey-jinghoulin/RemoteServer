package com.samonkey.remoteserver.ble;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

/**
 * Created on 2017/3/20
 *
 * @author saker
 */

public class BLEUtils {

    /**
     * 检查BLE是否可用
     *
     * @return
     */
    public static boolean isBLE(Context context) {
        // 1.是否支持BLE
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "不支持蓝牙BLE功能", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 2.是否API >= 18
        if (Build.VERSION.SDK_INT < 18) {
            Toast.makeText(context, "系统版本过低", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // 3.是否支持蓝牙
            BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager.getAdapter() == null) {
                Toast.makeText(context, "不支持蓝牙", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

}
