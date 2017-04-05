package com.samonkey.remoteserver.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.samonkey.remoteserver.event.DataProcessor;
import com.samonkey.remoteserver.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BLEService extends Service {
    private static final String TAG = "BLEService";
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler = new Handler();
    private static final int SCAN_PERIOD = 10000;
    private boolean isEnable = true;
    private BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteChar;
    private DataProcessor mProcessor;

    public BLEService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        LogUtils.d("BLEService started");
        // BLE是否可用，不可用弹出Toast
        if (!BLEUtils.isBLE(this)) {
            isEnable = false;
            stopSelf();
            return;
        }
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mProcessor = new DataProcessor(DataProcessor.BLE_SERVER);
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // BLE不可用则结束
        if (!isEnable) {
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        // 开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            BLEActivity.startBLE(this, new OpenBTCallback() {
                @Override
                public void onSuccess() {
                    isEnable = true;
                }

                @Override
                public void onFailure() {
                    isEnable = false;
                    Toast.makeText(BLEService.this, "未开启蓝牙，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (isEnable) {
            scanLEDevice(true);
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void write(String msg) {
        LogUtils.d("EventBus receive->" + msg);
        if (mBluetoothGatt != null && mWriteChar != null) {
            // 写
            mWriteChar.setValue(msg);
            mWriteChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mBluetoothGatt.writeCharacteristic(mWriteChar);// 返回写入是否成功
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service =
                        gatt.getService(UUID.fromString(BLEFlags.BLE_SERVICE_UUID));
                if (service == null) {
                    return;
                }
                BluetoothGattCharacteristic characteristic =
                        service.getCharacteristic(UUID.fromString(BLEFlags.BLE_NOTIFICATION_CHAR));
                mWriteChar = service.getCharacteristic(UUID.fromString(BLEFlags.BLE_WRITE_CHAR));
                if (characteristic == null || mWriteChar == null) {
                    return;
                }
                // 设置特征通知后才能读取
                gatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor =
                        characteristic.getDescriptor(UUID.fromString(BLEFlags.BLE_NOTIFICATION_DESC));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            // 读
            gatt.readCharacteristic(characteristic);
            String result = new String(characteristic.getValue());
            if (mProcessor != null) {
                mProcessor.accept(result);
            }
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device != null && device != mDevice) {
                        // 找到设备后停止扫描
//                if (mScanning) {
//                    mBluetoothAdapter.stopLeScan(this);
//                    mScanning = false;
//                }
                        mDevice = device;
                        mBluetoothGatt = mDevice.connectGatt(BLEService.this, false, mGattCallback);
                    }
                }
            };

    private void scanLEDevice(boolean enable) {
        if (enable) {
            if (!mScanning) {
                mBluetoothAdapter.startLeScan(
                        new UUID[]{UUID.fromString(BLEFlags.BLE_SERVICE_UUID)}, mLeScanCallback);
                mScanning = true;
                // 10s后停止扫描
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                        mScanning = false;
//                    }
//                }, SCAN_PERIOD);
            }
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
    }
}
